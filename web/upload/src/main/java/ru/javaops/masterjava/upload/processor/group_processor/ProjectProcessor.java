package ru.javaops.masterjava.upload.processor.group_processor;

import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public class ProjectProcessor {
    private static final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private static final GroupProcessor groupProcessor = new GroupProcessor();
    private static final JaxbUnmarshaller unmarshaller;

    static {
        val jaxbParser = new JaxbParser(ObjectFactory.class);
        unmarshaller = jaxbParser.createUnmarshaller();
    }

    public void process(StaxStreamProcessor processor) throws XMLStreamException, JAXBException {
        while (processor.startElement("Project", "Projects")) {
            ru.javaops.masterjava.xml.schema.Project schemaProject =
                    unmarshaller.unmarshal(
                            processor.getReader(),
                            ru.javaops.masterjava.xml.schema.Project.class
                    );

            Project project =
                    new ru.javaops.masterjava.persist.model.Project(
                            schemaProject.getName(),
                            schemaProject.getDescription()
                    );

            val persistedProjectId = projectDao.insertGeneratedId(project);
            groupProcessor.processProject(schemaProject, persistedProjectId);
        }
    }
}
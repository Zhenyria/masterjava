package ru.javaops.masterjava.upload.processor.group_processor;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.schema.Project;

import java.util.stream.Collectors;

class GroupProcessor {
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    void processProject(Project schemaProject, int projectId) {
        groupDao.insertGeneratedId(
                schemaProject.getGroup()
                        .stream()
                        .map(group -> new Group(
                                group.getName(),
                                GroupType.valueOf(group.getType().toString()),
                                projectId
                        ))
                        .collect(Collectors.toList())
        );
    }
}

package ru.javaops.masterjava.persist;

import lombok.experimental.UtilityClass;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ProjectTestData {
    public static final Comparator<Project> PROJECT_COMPARATOR = Comparator.comparing(Project::getName);

    public static final int FIRST_PROJECT_ID = 1;
    public static final int SECOND_PROJECT_ID = 2;
    public static final int THIRD_PROJECT_ID = 3;
    public static final int FOURTH_PROJECT_ID = 4;
    public static final int OTHER_PROJECT_ID = 5;

    private static final Project FIRST_PROJECT = new Project(FIRST_PROJECT_ID, "First Java", "Java project");
    private static final Project SECOND_PROJECT = new Project(SECOND_PROJECT_ID, "Second Java", "Java project");
    private static final Project THIRD_PROJECT = new Project(THIRD_PROJECT_ID, "Third Java", "Java project");
    private static final Project FOURTH_PROJECT = new Project(FOURTH_PROJECT_ID, "Fourth Java", "Java project");
    private static final Project OTHER_PROJECT = new Project(OTHER_PROJECT_ID, "Other Java", "Java project");

    public static List<Project> getProjects() {
        return Stream
                .of(FIRST_PROJECT, SECOND_PROJECT, THIRD_PROJECT, FOURTH_PROJECT)
                .map(project -> new Project(project.getId(), project.getName(), project.getDescription()))
                .collect(Collectors.toList());
    }

    public static Project getProjectToSave() {
        return new Project(OTHER_PROJECT.getId(), OTHER_PROJECT.getName(), OTHER_PROJECT.getDescription());
    }

    public static void setUp() {
        ProjectDao dao = DBIProvider.getDao(ProjectDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((connection, status) -> getProjects().forEach(dao::insert));
    }
}

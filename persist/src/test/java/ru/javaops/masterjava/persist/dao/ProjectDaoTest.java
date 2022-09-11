package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.persist.ProjectTestData;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.javaops.masterjava.persist.ProjectTestData.PROJECT_COMPARATOR;
import static ru.javaops.masterjava.persist.ProjectTestData.getProjectToSave;
import static ru.javaops.masterjava.persist.ProjectTestData.getProjects;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {
    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @Before
    public void setUp() {
        ProjectTestData.setUp();
    }

    @Test
    public void getWithLimitTest() {
        assertProjectsAreEqual(getProjects(), dao.getWithLimit(5));
    }

    @Test
    public void insertTest() {
        dao.insert(getProjectToSave());

        List<Project> actualProjects = dao.getWithLimit(5);
        List<Project> expectedProjects = getProjects();
        expectedProjects.add(getProjectToSave());

        assertProjectsAreEqual(expectedProjects, actualProjects);
    }

    private void assertProjectsAreEqual(List<Project> expectedProjects, List<Project> actualProjects) {
        assertNotNull(actualProjects);
        assertEquals(expectedProjects.size(), actualProjects.size());

        expectedProjects.sort(PROJECT_COMPARATOR);
        actualProjects.sort(PROJECT_COMPARATOR);

        for (int i = 0; i < actualProjects.size(); i++) {
            Project actualProject = actualProjects.get(i);
            Project expectedProject = expectedProjects.get(i);
            assertNotNull(actualProject);
            assertEquals(expectedProject, actualProject);
        }
    }
}

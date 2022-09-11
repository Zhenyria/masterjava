package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.persist.GroupTestData;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.javaops.masterjava.persist.GroupTestData.getGroupToSave;
import static ru.javaops.masterjava.persist.GroupTestData.getGroups;

public class GroupDaoTest extends AbstractDaoTest<GroupDao> {
    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @Before
    public void setUp() {
        GroupTestData.setUp();
    }

    @Test
    public void getWithLimitTest() {
        assertGroupsAreEqual(getGroups(), dao.getWithLimit(5));
    }

    @Test
    public void insertTest() {
        dao.insert(getGroupToSave());

        List<Group> expectedGroups = getGroups();
        expectedGroups.add(getGroupToSave());

        assertGroupsAreEqual(expectedGroups, dao.getWithLimit(5));
    }

    private void assertGroupsAreEqual(List<Group> expectedGroups, List<Group> actualGroups) {
        assertNotNull(actualGroups);
        assertEquals(expectedGroups.size(), actualGroups.size());

        for (int i = 0; i < actualGroups.size(); i++) {
            Group actualGroup = actualGroups.get(i);
            Group expectedGroup = expectedGroups.get(i);

            assertNotNull(actualGroup);
            assertEquals(expectedGroup, actualGroup);
        }
    }
}

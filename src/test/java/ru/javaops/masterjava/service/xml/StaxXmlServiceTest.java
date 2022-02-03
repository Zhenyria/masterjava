package ru.javaops.masterjava.service.xml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.model.UserData;

import java.util.List;

import static ru.javaops.masterjava.test_util.StaxXmlServiceTest.MASTERJAVA_PROJECT_NAME;
import static ru.javaops.masterjava.test_util.StaxXmlServiceTest.MASTERJAVA_USERS;
import static ru.javaops.masterjava.test_util.StaxXmlServiceTest.TOPJAVA_PROJECT_NAME;
import static ru.javaops.masterjava.test_util.StaxXmlServiceTest.TOPJAVA_USERS;
import static ru.javaops.masterjava.test_util.StaxXmlServiceTest.getSortedUsersData;
import static ru.javaops.masterjava.test_util.XmlServiceTestUtil.PATH_TO_XML;

public class StaxXmlServiceTest {
    private StaxXmlService service;

    @Before
    public void setUp() {
        this.service = new StaxXmlService(PATH_TO_XML);
    }

    @Test
    public void getUsersByTopjavaProjectTest() {
        List<UserData> actualUsers = getSortedUsersData(service.getUsersByProject(TOPJAVA_PROJECT_NAME));

        Assert.assertEquals(TOPJAVA_USERS.size(), actualUsers.size());

        for (int i = 0; i < TOPJAVA_USERS.size(); i++) {
            UserData actualUser = actualUsers.get(i);
            UserData expectedUser = TOPJAVA_USERS.get(i);
            Assert.assertEquals(expectedUser, actualUser);
        }
    }

    @Test
    public void getUsersByMasterjavaProjectTest() {
        List<UserData> actualUsers = getSortedUsersData(service.getUsersByProject(MASTERJAVA_PROJECT_NAME));

        Assert.assertEquals(MASTERJAVA_USERS.size(), actualUsers.size());

        for (int i = 0; i < MASTERJAVA_USERS.size(); i++) {
            UserData actualUser = actualUsers.get(i);
            UserData expectedUser = MASTERJAVA_USERS.get(i);
            Assert.assertEquals(expectedUser, actualUser);
        }
    }
}

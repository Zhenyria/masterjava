package ru.javaops.masterjava.service.xml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.UserType;

import java.util.List;

import static ru.javaops.masterjava.test_util.MainXmlTestUtil.MASTERJAVA_NAME;
import static ru.javaops.masterjava.test_util.MainXmlTestUtil.MASTERJAVA_USERS;
import static ru.javaops.masterjava.test_util.MainXmlTestUtil.TOPJAVA_NAME;
import static ru.javaops.masterjava.test_util.MainXmlTestUtil.TOPJAVA_USERS;
import static ru.javaops.masterjava.test_util.XmlServiceTestUtil.PATH_TO_XML;

public class MainXmlTest {
    private static final String pathToSchema = "payload.xsd";
    private MainXml mainXml;

    @Before
    public void setUp() {
        this.mainXml = new MainXml(pathToSchema, ObjectFactory.class);
        this.mainXml.loadData(PATH_TO_XML);
    }

    @Test
    public void getUsersByProjectTopJavaTest() {
        List<UserType> actualUsers = this.mainXml.getUsersByProject(TOPJAVA_NAME);

        Assert.assertEquals(TOPJAVA_USERS.size(), actualUsers.size());

        for (int i = 0; i < actualUsers.size(); i++) {
            UserType expectedUser = TOPJAVA_USERS.get(i);
            UserType actualUser = actualUsers.get(i);
            assertUsersMatching(expectedUser, actualUser);
        }
    }

    @Test
    public void getUsersByProjectMasterJavaTest() {
        List<UserType> actualUsers = this.mainXml.getUsersByProject(MASTERJAVA_NAME);

        Assert.assertEquals(MASTERJAVA_USERS.size(), actualUsers.size());

        for (int i = 0; i < actualUsers.size(); i++) {
            UserType expectedUser = MASTERJAVA_USERS.get(i);
            UserType actualUser = actualUsers.get(i);
            assertUsersMatching(expectedUser, actualUser);
        }
    }

    private void assertUsersMatching(UserType expected, UserType actual) {
        Assert.assertEquals(expected.getFullName(), actual.getFullName());
        Assert.assertEquals(expected.getFlag(), actual.getFlag());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(((CityType) expected.getCity()).getId(), ((CityType) actual.getCity()).getId());
    }
}

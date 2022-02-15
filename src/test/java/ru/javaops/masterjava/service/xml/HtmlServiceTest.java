package ru.javaops.masterjava.service.xml;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ru.javaops.masterjava.test_util.HtmlServiceTestUtil.GROUPS_HTML;
import static ru.javaops.masterjava.test_util.HtmlServiceTestUtil.PATH_TO_GROUPS_XSL;
import static ru.javaops.masterjava.test_util.HtmlServiceTestUtil.PATH_TO_USERS_XSL;
import static ru.javaops.masterjava.test_util.HtmlServiceTestUtil.USERS_HTML;

public class HtmlServiceTest {

    @Test
    public void getAllUsersAsHtmlTest() {
        HtmlService htmlService = new HtmlService(PATH_TO_USERS_XSL);
        String htmlWithUsers = htmlService.getHtmlFromXml("payload.xml");

        Assert.assertNotNull(htmlWithUsers);
        Assert.assertEquals(USERS_HTML, htmlWithUsers);
    }

    @Test
    public void getAllGroupsByProjectAsHtmlTest() {
        Map<String, String> params = new HashMap<>();
        params.put("project_name", "topJava");
        HtmlService htmlService = new HtmlService(PATH_TO_GROUPS_XSL, params);
        String htmlWithGroups = htmlService.getHtmlFromXml("payload.xml");

        Assert.assertNotNull(htmlWithGroups);
        Assert.assertEquals(GROUPS_HTML, htmlWithGroups);
    }
}

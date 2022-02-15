package ru.javaops.masterjava.test_util;

public class HtmlServiceTestUtil {
    public static final String PATH_TO_USERS_XSL = "users.xsl";
    public static final String PATH_TO_GROUPS_XSL = "groups.xsl";

    public static final String USERS_HTML = "<html>\r\n" +
                                            "<body>\r\n" +
                                            "<div>\r\n" +
                                            "<ol>\r\n" +
                                            "<li>Full Name gmail@gmail.com</li>\r\n" +
                                            "<li>Admin admin@javaops.ru</li>\r\n" +
                                            "<li>Deleted mail@yandex.ru</li>\r\n" +
                                            "<li>Zhenya zhenyria@mail.ru</li>\r\n" +
                                            "<li>Jhonny Petrov dog@mail.ru</li>\r\n" +
                                            "<li>Empty empty@mail.ru</li>\r\n" +
                                            "</ol>\r\n" +
                                            "</div>\r\n" +
                                            "</body>\r\n" +
                                            "</html>\r\n";

    public static final String GROUPS_HTML = "<html>\r\n" +
                                             "<body>\r\n" +
                                             "<div>\r\n" +
                                             "<ol>\r\n" +
                                             "<li>topJava01</li>\r\n" +
                                             "<li>topJava02</li>\r\n" +
                                             "<li>topJava03</li>\r\n" +
                                             "</ol>\r\n" +
                                             "</div>\r\n" +
                                             "</body>\r\n" +
                                             "</html>\r\n";

    private HtmlServiceTestUtil() {
    }
}

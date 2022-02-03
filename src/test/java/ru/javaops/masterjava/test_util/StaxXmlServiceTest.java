package ru.javaops.masterjava.test_util;

import ru.javaops.masterjava.model.UserData;
import ru.javaops.masterjava.xml.schema.UserType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaxXmlServiceTest {
    public static final String TOPJAVA_PROJECT_NAME = "topJava";
    public static final String MASTERJAVA_PROJECT_NAME = "masterJava";

    private static final Comparator<UserData> userDataComparator = Comparator.comparing(UserData::getEmail);

    public static final List<UserData> TOPJAVA_USERS = getUsersDataFromList(MainXmlTestUtil.TOPJAVA_USERS);
    public static final List<UserData> MASTERJAVA_USERS = getUsersDataFromList(MainXmlTestUtil.MASTERJAVA_USERS);


    private static List<UserData> getUsersDataFromList(List<UserType> users) {
        return users.stream()
                .map(user -> new UserData(user.getFullName(), user.getEmail()))
                .sorted(userDataComparator)
                .collect(Collectors.toList());
    }

    public static List<UserData> getSortedUsersData(List<UserData> users) {
        return users.stream()
                .sorted(userDataComparator)
                .collect(Collectors.toList());
    }
}

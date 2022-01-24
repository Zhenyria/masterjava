package ru.javaops.masterjava.test_util;

import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.UserType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainXmlTestUtil {
    public static final String TOPJAVA_NAME = "topJava";
    public static final String MASTERJAVA_NAME = "masterJava";

    private static final CityType SPB_CITY = createCity("spb", "Санкт-Петербург");
    private static final CityType KIEV_CITY = createCity("kiv", "Киев");
    private static final CityType MINSK_CITY = createCity("mnsk", "Минск");

    private static final UserType FULL_NAME_USER = createUser("Full Name", FlagType.ACTIVE, "gmail@gmail.com", KIEV_CITY);
    private static final UserType ADMIN_USER = createUser("Admin", FlagType.SUPERUSER, "admin@javaops.ru", SPB_CITY);
    private static final UserType DELETED_USER = createUser("Deleted", FlagType.DELETED, "mail@yandex.ru", SPB_CITY);
    private static final UserType ZHENYA_USER = createUser("Zhenya", FlagType.ACTIVE, "zhenyria@mail.ru", MINSK_CITY);
    public static List<UserType> TOPJAVA_USERS = Stream.of(FULL_NAME_USER, ADMIN_USER, DELETED_USER, ZHENYA_USER).collect(Collectors.toList());
    private static final UserType JHONNY_PETROV_USER = createUser("Jhonny Petrov", FlagType.ACTIVE, "dog@mail.ru", KIEV_CITY);
    public static List<UserType> MASTERJAVA_USERS = Stream.of(ADMIN_USER, DELETED_USER, ZHENYA_USER, JHONNY_PETROV_USER).collect(Collectors.toList());

    private MainXmlTestUtil() {
    }

    private static CityType createCity(String id, String value) {
        CityType city = new CityType();
        city.setId(id);
        city.setValue(value);
        return city;
    }

    private static UserType createUser(String fullName, FlagType flag, String email, CityType city) {
        UserType user = new UserType();
        user.setFullName(fullName);
        user.setFlag(flag);
        user.setEmail(email);
        user.setCity(city);
        return user;
    }
}

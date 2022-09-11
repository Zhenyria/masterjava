package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.List;

import static ru.javaops.masterjava.persist.CityTestData.FIRST_CITY_ID;

public class UserTestData {
    public static User ADMIN;
    public static User DELETED;
    public static User FULL_NAME;
    public static User USER1;
    public static User USER2;
    public static User USER3;
    public static List<User> FIRST5_USERS;

    public static void init() {
        ADMIN = new User("Admin", "admin@javaops.ru", UserFlag.superuser, FIRST_CITY_ID);
        DELETED = new User("Deleted", "deleted@yandex.ru", UserFlag.deleted, FIRST_CITY_ID);
        FULL_NAME = new User("Full Name", "gmail@gmail.com", UserFlag.active, FIRST_CITY_ID);
        USER1 = new User("User1", "user1@gmail.com", UserFlag.active, FIRST_CITY_ID);
        USER2 = new User("User2", "user2@yandex.ru", UserFlag.active, FIRST_CITY_ID);
        USER3 = new User("User3", "user3@yandex.ru", UserFlag.active, FIRST_CITY_ID);
        FIRST5_USERS = ImmutableList.of(ADMIN, DELETED, FULL_NAME, USER1, USER2);
    }

    public static void setUp() {
        CityTestData.setUp();
        UserDao dao = DBIProvider.getDao(UserDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIRST5_USERS.forEach(dao::insert);
            dao.insert(USER3);
        });
    }
}

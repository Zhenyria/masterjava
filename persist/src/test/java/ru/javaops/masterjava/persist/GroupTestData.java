package ru.javaops.masterjava.persist;

import lombok.experimental.UtilityClass;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.javaops.masterjava.persist.ProjectTestData.FIRST_PROJECT_ID;
import static ru.javaops.masterjava.persist.ProjectTestData.FOURTH_PROJECT_ID;
import static ru.javaops.masterjava.persist.ProjectTestData.SECOND_PROJECT_ID;
import static ru.javaops.masterjava.persist.ProjectTestData.THIRD_PROJECT_ID;

@UtilityClass
public class GroupTestData {
    public static final int FIRST_GROUP_ID = 1;
    public static final int SECOND_GROUP_ID = 2;
    public static final int THIRD_GROUP_ID = 3;
    public static final int FOURTH_GROUP_ID = 4;
    public static final int OTHER_GROUP_ID = 5;

    private static final Group FIRST_GROUP =
            new Group(FIRST_GROUP_ID, FIRST_PROJECT_ID, "java01", GroupType.FINISHED);
    private static final Group SECOND_GROUP =
            new Group(SECOND_GROUP_ID, SECOND_PROJECT_ID, "java02", GroupType.REGISTERING);
    private static final Group THIRD_GROUP =
            new Group(THIRD_GROUP_ID, THIRD_PROJECT_ID, "java03", GroupType.REGISTERING);
    private static final Group FOURTH_GROUP =
            new Group(FOURTH_GROUP_ID, FOURTH_PROJECT_ID, "java04", GroupType.FINISHED);
    private static final Group OTHER_GROUP =
            new Group(OTHER_GROUP_ID, FIRST_PROJECT_ID, "java05", GroupType.CURRENT);

    public static List<Group> getGroups() {
        return Stream
                .of(FIRST_GROUP, SECOND_GROUP, THIRD_GROUP, FOURTH_GROUP)
                .map(group -> new Group(group.getId(), group.getProjectId(), group.getName(), group.getType()))
                .collect(Collectors.toList());
    }

    public static Group getGroupToSave() {
        return new Group(OTHER_GROUP.getId(), OTHER_GROUP.getProjectId(), OTHER_GROUP.getName(), OTHER_GROUP.getType());
    }

    public static void setUp() {
        ProjectTestData.setUp();
        GroupDao dao = DBIProvider.getDao(GroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((connection, status) -> getGroups().forEach(dao::insert));
    }
}

package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    public void insert(Group group) {
        if (group.isNew()) {
            insertWithoutId(group);
        } else {
            insertWithId(group);
        }
    }

    @SqlUpdate("INSERT INTO groups (project_id, name_val, type_val) " +
               "VALUES (:projectId, :name, CAST(:type AS group_type)) " +
               "ON CONFLICT DO NOTHING")
    protected abstract void insertWithoutId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, project_id, name_val, type_val) " +
               "VALUES (:id, :projectId, :name, CAST(:type AS group_type)) " +
               "ON CONFLICT DO NOTHING")
    protected abstract void insertWithId(@BindBean Group group);

    @SqlQuery("SELECT * FROM groups ORDER BY name_val, type_val LIMIT :it")
    public abstract List<Group> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE groups RESTART IDENTITY CASCADE")
    @Override
    public abstract void clean();
}

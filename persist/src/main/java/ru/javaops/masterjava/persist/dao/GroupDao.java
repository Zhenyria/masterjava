package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import one.util.streamex.StreamEx;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @SqlUpdate("TRUNCATE groups CASCADE ")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT nextval('common_seq')")
    public abstract int getNextVal();

    @SqlQuery("SELECT * FROM groups ORDER BY name")
    public abstract List<Group> getAll();

    public Map<String, Group> getAsMap() {
        return StreamEx.of(getAll()).toMap(Group::getName, Function.identity());
    }

    public Map<String, Integer> getIdsAsMap() {
        return StreamEx.of(getAll()).toMap(Group::getName, Group::getId);
    }

    @SqlUpdate("INSERT INTO groups (name, type, project_id)  VALUES (:name, CAST(:type AS group_type), :projectId)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean Group groups);

    @SqlBatch("INSERT INTO groups (name, type, project_id)  VALUES (:name, CAST(:type AS group_type), :projectId)")
    public abstract int[] insertGeneratedId(@BindBean List<Group> groups);

    public void insert(Group group) {
        int id = insertGeneratedId(group);
        group.setId(id);
    }

    @SqlBatch("INSERT INTO groups (id, name, type, project_id) " +
              "VALUES (:id, :name, CAST(:type AS group_type), :projectId) " +
              "ON CONFLICT DO NOTHING")
    public abstract void insert(@BindBean List<Group> groups);
}

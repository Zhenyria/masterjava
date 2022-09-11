package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    public void insert(Project project) {
        if (project.isNew()) {
            insertWithoutId(project);
        } else {
            insertWithId(project);
        }
    }

    @SqlUpdate("INSERT INTO projects (name_val, description) " +
               "VALUES (:name, :description) " +
               "ON CONFLICT DO NOTHING")
    protected abstract void insertWithoutId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (id, name_val, description) " +
               "VALUES (:id, :name, :description) " +
               "ON CONFLICT DO NOTHING")
    protected abstract void insertWithId(@BindBean Project project);

    @SqlQuery("SELECT * FROM projects ORDER BY name_val LIMIT :it")
    public abstract List<Project> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE projects RESTART IDENTITY CASCADE")
    @Override
    public abstract void clean();
}

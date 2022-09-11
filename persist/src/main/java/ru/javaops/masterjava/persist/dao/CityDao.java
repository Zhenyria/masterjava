package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    public void insert(City city) {
        if (city.isNew()) {
            insertWithoutId(city);
        } else {
            insertWithId(city);
        }
    }

    @SqlUpdate("INSERT INTO cities (name_val) VALUES (:name) ON CONFLICT DO NOTHING")
    protected abstract void insertWithoutId(@BindBean City city);

    @SqlUpdate("INSERT INTO cities (id, name_val) VALUES (:id, :name) ON CONFLICT DO NOTHING")
    protected abstract void insertWithId(@BindBean City city);

    @SqlQuery("SELECT * FROM cities ORDER BY name_val LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE cities RESTART IDENTITY CASCADE")
    @Override
    public abstract void clean();
}

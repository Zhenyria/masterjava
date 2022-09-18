package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    public int getSeqAndSkip(int step) {
        int nextId = getNextVal();
        DBIProvider
                .getDBI()
                .useHandle(handle ->
                        handle.execute(String.format("SELECT setval('cities_seq', %d)", step + nextId - 1)));
        return nextId;
    }

    public void insert(City city) {
        if (city.isNew()) {
            insertWithoutId(city);
        } else {
            insertWithId(city);
        }
    }

    public List<String> insertAndGetIgnored(List<City> cities, int batchSize) {
        int[] ignoredEntities = this.insert(cities, batchSize);
        return IntStream.range(0, ignoredEntities.length)
                .filter(idx -> ignoredEntities[idx] == 0)
                .mapToObj(cities::get)
                .map(City::getName)
                .collect(Collectors.toList());
    }

    @SqlQuery("SELECT nextval('cities_seq')")
    protected abstract int getNextVal();

    @SqlUpdate("INSERT INTO cities (code, name_val) VALUES (:code, :name) ON CONFLICT DO NOTHING")
    protected abstract void insertWithoutId(@BindBean City city);

    @SqlUpdate("INSERT INTO cities (id, code, name_val) VALUES (:id, :code, :name) ON CONFLICT DO NOTHING")
    protected abstract void insertWithId(@BindBean City city);

    @SqlBatch("INSERT INTO cities (id, code, name_val) VALUES (:id, :code, :name) ON CONFLICT DO NOTHING")
    protected abstract int[] insert(@BindBean List<City> cities, @BatchChunkSize int batchSize);

    @SqlQuery("SELECT * FROM cities WHERE code = :it")
    public abstract City getByCode(@Bind String code);

    @SqlQuery("SELECT * FROM cities ORDER BY name_val LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE cities RESTART IDENTITY CASCADE")
    @Override
    public abstract void clean();
}

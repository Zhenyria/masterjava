package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserDao implements AbstractDao {
    public static final int ALLOCATION_SIZE = 3;

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }

    @SqlBatch("INSERT INTO users (id, full_name, email, flag) " +
              "VALUES (:id, :fullName, :email, CAST(:flag AS user_flag)) " +
              "ON CONFLICT ON CONSTRAINT idx_users_email DO NOTHING")
    public abstract int[] insert(@Bind("id") List<Integer> ids,
                                 @Bind("fullName") List<String> fullNames,
                                 @Bind("email") List<String> emails,
                                 @Bind("flag") List<UserFlag> userFlags);

    @SqlQuery("SELECT nextval('user_seq');")
    @Override
    public abstract int nextVal();

    @SqlUpdate("ALTER SEQUENCE user_seq INCREMENT BY 3;")
    @Override
    public abstract void prepareSequenceToBatchInsert();

    @SqlUpdate("ALTER SEQUENCE user_seq INCREMENT BY 1;")
    @Override
    public abstract void returnSequenceToInitialCondition();

    @SqlUpdate("INSERT INTO users (full_name, email, flag) VALUES (:fullName, :email, CAST(:flag AS user_flag)) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag) VALUES (:id, :fullName, :email, CAST(:flag AS user_flag)) ")
    abstract void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    public abstract void clean();
}

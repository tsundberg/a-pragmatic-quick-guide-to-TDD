package se.thinkcode.todo;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface TodoDao {
    @SqlUpdate("""
            insert into tasks (id, owner, chore)
            values (:id, :owner, :chore )
            """)
    void createNewTask(String id,
                       String owner,
                       String chore);

    @SqlQuery("""
            select owner, 
                   chore
            from tasks
            where owner = :owner
            """)
    @RegisterRowMapper(TaskMapper.class)
    List<Task> getTasks(String owner);
}

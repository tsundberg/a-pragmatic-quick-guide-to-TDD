package se.thinkcode.todo;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements RowMapper<Task> {
    @Override
    public Task map(ResultSet rs, StatementContext ctx) throws SQLException {
        String owner = rs.getString("owner");
        User user = new User(owner);
        String choreStr = rs.getString("chore");
        Chore chore = new Chore(choreStr);

        return new Task(user, chore);
    }
}

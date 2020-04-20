package frame.core;

import frame.pojo.Configuration;
import frame.pojo.MapperStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {
    public <T> List<T> query(Configuration configuration, MapperStatement mapperStatement,Object... params) throws SQLException, NoSuchFieldException, IllegalAccessException, Exception;

    <T> int update(Configuration configuration, MapperStatement mapperStatement, Object... params) throws Exception;
}

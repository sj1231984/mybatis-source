package frame.pojo;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    private DataSource dataSource;

    private Map<String, MapperStatement> mapperStatementMap = new ConcurrentHashMap<>();

    public Map<String, MapperStatement> getMapperStatementMap() {
        return mapperStatementMap;
    }

    public void setMapperMap(Map<String, MapperStatement> mapperMap) {
        this.mapperStatementMap = mapperMap;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}

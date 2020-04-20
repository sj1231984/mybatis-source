package frame.factory;

import frame.config.XmlConfigBuilder;
import frame.core.DefaultSqlSessionFactory;
import frame.core.SqlSessionFactory;
import frame.pojo.Configuration;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {


    private Configuration configuration;

    public SqlSessionFactoryBuilder(){
        this.configuration = new Configuration();
    }

    /**
     * 构建工厂的入口，让用户传入配置文件的文件流
     * @param inputStream
     * @return
     */
    public SqlSessionFactory build(InputStream inputStream){
        // 读取数据源信息xml为Configuration对象
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(configuration);
        xmlConfigBuilder.parseConfig(inputStream);
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return sqlSessionFactory;
    }
}

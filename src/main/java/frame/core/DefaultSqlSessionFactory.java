package frame.core;

import frame.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        DefaultSqlSession sqlSession = new DefaultSqlSession(configuration);
        return sqlSession;
    }

}

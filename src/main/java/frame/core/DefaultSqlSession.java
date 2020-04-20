package frame.core;


import frame.pojo.Configuration;
import frame.pojo.MapperStatement;
import frame.pojo.SqlType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

public class DefaultSqlSession implements SqlSession  {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MapperStatement mapperStatement = configuration.getMapperStatementMap().get(statementId);
        return simpleExecutor.query(configuration,mapperStatement,params);
    }

    @Override
    public <T> T selectOne(String statementId, Object... param) throws Exception {
        List<T> objects = this.selectList(statementId, param);
        if(objects.size()==1){
            return objects.get(0);
        }else{
            throw new RuntimeException("查询结果为空或者大于一条");
        }
    }


    public int update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MapperStatement mapperStatement = configuration.getMapperStatementMap().get(statementId);
        return simpleExecutor.update(configuration,mapperStatement,params);
    }


    @Override
    public <T> T getMapper(Class<T> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className+"."+methodName;
                Type genericReturnType = method.getGenericReturnType();
                SqlType sqlType = configuration.getMapperStatementMap().get(statementId).getSqlType();
                switch (sqlType){
                    case SELECT:
                        if(genericReturnType instanceof ParameterizedType){
                            return selectList(statementId,args);
                        }else{
                            return selectOne(statementId,args);
                        }
                    case INSERT:
                    case UPDATE:
                    case DELETE:
                        return update(statementId,args);
                    default:
                        return null;
                }
            }
        });
        return (T) proxyInstance;
    }
}

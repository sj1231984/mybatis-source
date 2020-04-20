package frame.core;

import frame.pojo.Configuration;
import frame.pojo.MapperStatement;
import frame.utils.GenericTokenParser;
import frame.utils.ParameterMapping;
import frame.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {
    @Override
    public <T> List<T> query(Configuration configuration, MapperStatement mapperStatement, Object... params) throws Exception {
        Connection connection = configuration.getDataSource().getConnection();

        String sql = mapperStatement.getSql();

        BoundSql boundSql = getBoundSql(sql);

        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //设置参数
        setParams(mapperStatement,boundSql,preparedStatement,params);

        //封装结果
        List<Object> result = setResult(mapperStatement,preparedStatement);

        return (List<T>) result;
    }

    @Override
    public int update(Configuration configuration, MapperStatement mapperStatement, Object... params) throws Exception {
        Connection connection = configuration.getDataSource().getConnection();

        String sql = mapperStatement.getSql();

        BoundSql boundSql = getBoundSql(sql);

        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //设置参数
        setParams(mapperStatement,boundSql,preparedStatement,params);

        return preparedStatement.executeUpdate();
    }

    private List<Object> setResult(MapperStatement mapperStatement, PreparedStatement preparedStatement) throws Exception {
        List<Object> result = new ArrayList<>();
        String resultType = mapperStatement.getResultType();
        Class<?> resultTypeClass = getTypeClass(resultType);

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while(resultSet.next()){
            Object resultObj = resultTypeClass.newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(resultObj,value);
            }
            result.add(resultObj);
        }
        return result;
    }

    private void setParams(MapperStatement mapperStatement, BoundSql boundSql, PreparedStatement preparedStatement, Object[] params) throws Exception {
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        String paramterType = mapperStatement.getParamterType();
        if(paramterType==null){
            return ;
        }
        Class<?> classObj = getTypeClass(paramterType);
        for (int i = 0; i < parameterMappingList.size(); i++) {
            String fieldName = parameterMappingList.get(i).getContent();

            Field declaredField = classObj.getDeclaredField(fieldName);
            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
            }
            Object o = declaredField.get(params[0]);
            preparedStatement.setObject(i+1,o);
        }
    }

    private Class<?> getTypeClass(String classType) throws Exception {
        if(classType!=null){
            return Class.forName(classType);
        }
        return null;
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        return new BoundSql(parseSql,parameterMappings);


    }
}

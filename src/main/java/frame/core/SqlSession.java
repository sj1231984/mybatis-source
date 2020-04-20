package frame.core;


import java.util.List;

/**
 * 框架对外接口类
 */
public interface SqlSession {

   public <T> List<T> selectList(String statementId,Object... param) throws Exception;

   public <T> T selectOne(String statementId,Object... param)throws Exception;

   public <T> T getMapper(Class<T> mapperClass);
}


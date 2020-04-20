package frame.config;

import frame.pojo.Configuration;
import frame.pojo.MapperStatement;
import frame.pojo.SqlType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XmlMapperBuilder {
    private final Configuration configuration;

    public XmlMapperBuilder(Configuration configuration){
        this.configuration = configuration;
    }

    public void parse(InputStream resourceAsStream) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            String namespace = rootElement.attributeValue("namespace");
            List<Element> selectNodes = rootElement.selectNodes("//select");
            parseNodes(selectNodes,namespace,SqlType.SELECT);

            List<Element> insertNodes = rootElement.selectNodes("//insert");
            parseNodes(insertNodes,namespace, SqlType.INSERT);

            List<Element> updateNodes = rootElement.selectNodes("//update");
            parseNodes(updateNodes,namespace, SqlType.UPDATE);

            List<Element> deleteNodes = rootElement.selectNodes("//delete");
            parseNodes(deleteNodes,namespace, SqlType.DELETE);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void parseNodes(List<Element> selectNodes, String namespace, SqlType select) {
        if(selectNodes != null && selectNodes.size() > 0) {
            for ( Element element : selectNodes) {
                String id = element.attributeValue("id");
                String resultType = element.attributeValue("resultType");
                String paramterType = element.attributeValue("paramterType");
                String sql = element.getTextTrim();
                MapperStatement mapperStatement = new MapperStatement();
                mapperStatement.setId(id);
                mapperStatement.setSql(sql);
                mapperStatement.setResultType(resultType);
                mapperStatement.setParamterType(paramterType);
                mapperStatement.setSqlType(select);
                configuration.getMapperStatementMap().put(namespace + "." + id, mapperStatement);
            }
        }
    }
}

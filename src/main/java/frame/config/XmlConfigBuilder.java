package frame.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import frame.pojo.Configuration;
import frame.pojo.MapperStatement;
import frame.utils.Resource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XmlConfigBuilder {
    private  Configuration configuration;
    public XmlConfigBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 读取xml信息为Configuration对象
     * @param inputStream
     * @return
     */
    public Configuration parseConfig(InputStream inputStream){
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//property");
            Properties properties = new Properties();

            for (int i = 0; i < selectNodes.size(); i++) {
                Element element =  selectNodes.get(i);
                String name = element.attributeValue("name");
                String value = element.attributeValue("value");
                properties.setProperty(name,value);
            }
            ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass(properties.getProperty("driver"));
            comboPooledDataSource.setJdbcUrl(properties.getProperty("url"));
            comboPooledDataSource.setUser(properties.getProperty("username"));
            comboPooledDataSource.setPassword(properties.getProperty("password"));
            configuration.setDataSource(comboPooledDataSource);


            List<Element> mappers = rootElement.selectNodes("//mapper");
            XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
            if(mappers != null && mappers.size() > 0) {
                for (int i = 0; i < mappers.size(); i++) {
                    Element element =  mappers.get(i);
                    String mapperPath = element.attributeValue("resource");
                    InputStream resourceAsStream = Resource.getResourceAsStream(mapperPath);
                    xmlMapperBuilder.parse(resourceAsStream);
                }
            }
        } catch (DocumentException | PropertyVetoException e) {
            e.printStackTrace();
        }

        return configuration;
    }

}

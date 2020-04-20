package frame.utils;

import java.io.InputStream;

public class Resource {

    public static InputStream getResourceAsStream(String path){
        return Resource.class.getClassLoader().getResourceAsStream(path);
    }
}

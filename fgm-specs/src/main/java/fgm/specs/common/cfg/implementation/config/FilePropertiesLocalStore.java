package fgm.specs.common.cfg.implementation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class FilePropertiesLocalStore {

  Logger logger = Logger.getLogger(FilePropertiesLocalStore.class.getName());
  private Properties props;

  public FilePropertiesLocalStore() {
    loadProperties();
  }

  private void loadProperties()  {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream input = classloader.getResourceAsStream("env.properties");
      props = new Properties();
      // load a properties file
      props.load(input);
      System.out.println("props size is " + props.size());
    } catch (IOException ex) {
      logger.info(ex.toString());
    }
  }

  public static FilePropertiesLocalStore getInstance() {
    return new FilePropertiesLocalStore();
  }

  public void store(String key, Object value) {
    props.put(key, value);
  }

  public Object retrieveProperty(String key) {
    return props.get(key);
  }

  public String retrieveString(String key) {
    return props.get(key).toString();
  }

  public Integer retrieveInt(String key) { return Integer.parseInt(props.get(key).toString());}

  public Double retrieveDouble(String key) {return  Double.parseDouble(props.get(key).toString());}

  public Object remove(Object key) {
    return props.remove(key);
  }

  public boolean containsKey(String key) {
    return props.containsKey(key);
  }

}

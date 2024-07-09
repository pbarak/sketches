package fgm.specs.factory.implementation;

import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.data.FileProperty;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

public class FGMContext implements Serializable {

  public FGMContext() {
  }

  public FGMContext(Properties props) {
      this.props = props;
  }

  private Properties props = new Properties();

  public void setProps(Properties props) {
      this.props = props;
  }

  public Properties getProps() {
      return this.props;
  }

  public static FGMContext newInstance(Class<?> clazz) throws Exception{
    FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
    Properties props = new Properties();
    for(Field field  : clazz.getDeclaredFields())
    {
      System.out.println("the name is " + field.getName());
      if (field.isAnnotationPresent(FileProperty.class))
      {
        System.out.println("the annotated name is " + field.getName());

        if (filePropertiesLocalStore.containsKey(field.getName())) {
          System.out.println("it is found");
          if (validate(field.getName(),  filePropertiesLocalStore.retrieveProperty(field.getName()))) {
            props.put(field.getName(), filePropertiesLocalStore.retrieveProperty(field.getName()));
          }
        }
      }
    }
    if(props.size() != 0 ) {
         return new FGMContext(props);
    }
    else {
      throw new Exception();
    }

  }

  private static boolean validate(String key, Object value) {
    return true;
  }

}




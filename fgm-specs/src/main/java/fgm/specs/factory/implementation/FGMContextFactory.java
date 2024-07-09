package fgm.specs.factory.implementation;

import fgm.specs.factory.implementation.FGMContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FGMContextFactory {

  private static Map<String, FGMContext> contexts = new ConcurrentHashMap<>();

  /**
   * Gets the instance of FGMContext for the given class
   * @param clazz the class to get the JAXBContext for the given class.
   * @return the instance of JAXBContext for the given class.
   * @throws Exception refer to FGMContext.newInstance for more info
   */

  public static FGMContext getInstance(Class<?> clazz) throws Exception {
    FGMContext context = contexts.get(clazz.getName());
    if(context == null) {
      context = FGMContext.newInstance(clazz);
      contexts.put(clazz.getName(), context);
    }
    return context;
  }
}


//context for each class when the user wants to create
//the user gives a class and waits for object.
//it firstly gets the context of the class
//then calls the clazz constructor with the result of context factory
//it actually maps specific properties from files to specific context for a class.
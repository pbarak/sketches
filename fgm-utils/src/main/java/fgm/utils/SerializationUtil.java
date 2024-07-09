package fgm.utils;


import java.io.*;

/**
 * Clone Utilities Class
 */
public class SerializationUtil {

  /**
   * Deep copies an object
   */
  public static <T extends Serializable> T deepCopy(T source) {
    return deserialize(serialize(source));
  }

  public static <T extends Serializable> byte[] serialize(T source) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(bos);
      oos.writeObject(source);
      oos.flush();
      oos.close();
      bos.close();
    }
    catch (IOException e) {
      throw new RuntimeException("Cannot Serialize Object", e);
    }
    return bos.toByteArray();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T deserialize(byte[] byteData) {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
    Object o = null;
    try {
      o = new ObjectInputStream(bais).readObject();
    }
    catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Cannot Deserialize Object", e);
    }
    return (T) o;
  }

}

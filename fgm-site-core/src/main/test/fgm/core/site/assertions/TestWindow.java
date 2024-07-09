//package fgm.core.site.assertions;
//import fgm.BeanFactory;
//import fgm.beans.site.StateMessageBeanWindowed;
//import fgm.specs.data.SketchWindowRecord;
//import fgm.specs.data.WorldCupRecord;
//import fgm.specs.site.RemoteHandler;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.LineNumberReader;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.rmi.RemoteException;
//import java.util.List;
//import org.apache.commons.collections4.queue.CircularFifoQueue;
//import org.apache.kafka.clients.producer.Producer;
//import org.junit.*;
//
//public class TestWindow {
//
//  @Test
// public void windowTest() {
//    String line;
//    LineNumberReader reader;
//
//    try {
//
//      RemoteHandler stateMessageBeanWindowed  = BeanFactory.getRemoteHandler();
//      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//      URL resource = classLoader.getResource("wc_day46_1.txt");
//      reader = new LineNumberReader(new FileReader(new File(resource.toURI())));
//      Producer<String, byte[]> producer = BeanFactory.getProducer();
//      final CircularFifoQueue<List<SketchWindowRecord>> queue = new CircularFifoQueue<>(7200);
//
//
//      if (stateMessageBeanWindowed instanceof StateMessageBeanWindowed) {
//        while (true) {
//            line = reader.readLine();
//
//            if (line != null) {
//
//              String[] tokens = line.split(" ");
//              Long timestamp = Long.parseLong(tokens[0]);
//              Long clientID = Long.parseLong(tokens[1]);
//              Long objectID = Long.parseLong(tokens[2]);
//              Long size = Long.parseLong(tokens[3]);
//              Long method = Long.parseLong(tokens[4]);
//              Long status = Long.parseLong(tokens[5]);
//              Long type = Long.parseLong(tokens[6]);
//              Long server = Long.parseLong(tokens[7]);
//
//              WorldCupRecord wc = new WorldCupRecord(timestamp.longValue(),
//                  clientID.longValue(), objectID.longValue(), size.longValue(),
//                  method.longValue(), status.longValue(), type.longValue(),
//                  server.longValue());
//            }
//        }
//      }
//    } catch (RemoteException e) {
//      e.printStackTrace();
//    }
//    catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (URISyntaxException e) {
//      e.printStackTrace();
//    }
//
//  }
//}

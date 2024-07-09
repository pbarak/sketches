package fgm.statistics;
import java.io.PrintWriter;
import java.util.*;
import fgm.specs.common.cfg.implementation.config.FilePropertiesLocalStore;
import fgm.specs.common.vector.FastAGMS;
import fgm.specs.factory.implementation.DependencyInjection;
import fgm.specs.site.state.implementation.state.CustomSlidingWindow;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StatisticsProcessor extends AbstractProcessor<String, byte[]> {
  FilePropertiesLocalStore filePropertiesLocalStore = FilePropertiesLocalStore.getInstance();
  static Logger logger = Logger.getLogger(StatisticsProcessor.class.getName());
  private CustomSlidingWindow<?> customWindow;
  private final static String factoryPropertyKey = "factory.name";

  //create file in order to collect statistics and create graphical representations
  private PrintWriter writer;

  public StatisticsProcessor() {
    PropertyConfigurator.configure(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
    try {
      this.customWindow = new CustomSlidingWindow<>(DependencyInjection.createFactory(filePropertiesLocalStore.retrieveString(factoryPropertyKey)));
      PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
    }catch (Exception e) {
      logger.info(e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void init(ProcessorContext processorContext) {
    super.init(processorContext);
  }
  @Override
  public void process(String key, byte[] value) {
    if(key.equals("WorldCupRecord")) {
      ////StreamRecord record = (StreamRecord) BeanFactory.deserialize(value);
      ////this.customWindow.update(record, 1);
    } else if (key.equals("XiMessage")) {
      int depth = ((FastAGMS)(this.customWindow.getVector())).depth();
      double[] innerProductsArray = new double[depth];

      double update = 0.0;
      int j = 0;
      for (int i = 0; i < depth; i++) {
        Double[] sketchColumn = ((FastAGMS)(this.customWindow.getVector())).getSketchColumn(i);

        while (j < sketchColumn.length) {
          update = update + sketchColumn[j] * sketchColumn[j];
          j++;
        }
        innerProductsArray[i] = update;
        update = 0.0;
        j = 0;
      }

      Arrays.sort(innerProductsArray);
      Median median = new Median();
      logger.info("the median value is at the end of the round: " + median.evaluate(innerProductsArray) );
    }
  }

  @Override
  public void close(){
  }
}



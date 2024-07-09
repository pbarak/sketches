package fgm.specs.site.state.implementation.state;

import fgm.specs.data.StreamRecord;
import fgm.specs.data.WindowRecord;
import fgm.specs.data.implementation.stream.WorldCupRecord;
import fgm.specs.factory.IFgmFactory;
import fgm.specs.site.state.LocalStatistic;
import fgm.specs.site.convertor.IToWindowRecordConvertor;
import fgm.utils.SerializationUtil;
import org.apache.log4j.PropertyConfigurator;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import fgm.specs.common.Vector;


public class CustomSlidingWindow<T extends Vector> extends FgmStatistic<T> implements LocalStatistic<T> {
    private static final int CASHREGISTERFREQUENCY = 1;
    private final int windowWidth; //duration is in seconds
    private final int repetitionInterval; //how ofter a timer reshapes the custom window in milliseconds
    //a timer updates the window of the stream
    private final Timer timer;
    private final ConcurrentLinkedQueue<List<WindowRecord>> queue; //the data structure of the window is a queue
    private WindowRecord last;
    private List<WindowRecord> lastListInserted;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final LocalStatistic<?> Si;
    private final Logger logger = Logger.getLogger(CustomSlidingWindow.class.getName());
    private IFgmFactory<?> factory;

    public CustomSlidingWindow(IFgmFactory<?> factory) {
        this.factory = factory;
        this.queue = new ConcurrentLinkedQueue<>();
        PropertyConfigurator.configure(
                getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        this.Si = factory.getLocalStatistic();
        this.windowWidth = factory.getBaseCfg().getWindowSecondsWidth();
        this.timer = new Timer();
        this.repetitionInterval = factory.getBaseCfg().getWindowUpdateInterval();
        scheduleTimer();
    }

    public CustomSlidingWindow(int windowWidth, int repetitionInterval, LocalStatistic<?> Si) { //duration is in seconds
        PropertyConfigurator.configure(
                getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        this.Si = Si;
        this.queue = new ConcurrentLinkedQueue<>(); //read 3600 and 10 from file
        this.windowWidth = windowWidth;
        this.timer = new Timer();
        this.repetitionInterval = repetitionInterval;
        scheduleTimer(); //read from file
    }

    private synchronized void keepWindowTimeFrame() { //reference to Si sketch
        lock.writeLock().lock();
        while ( queue.size() > this.windowWidth ) { //check if parameter of operation can be used with write lock
            List<WindowRecord> list = queue.poll();
            for ( StreamRecord record2 : list ) {
                Si.update(record2, (-1)*CASHREGISTERFREQUENCY ); // a
            }
        }
        lock.writeLock().unlock();
    }

    private void scheduleTimer() {
        try {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    keepWindowTimeFrame();
                }
            }, (long) windowWidth * repetitionInterval,  repetitionInterval); //1000 should be file property
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    //the ordering key of the record already exists in the queue (window)
    private void updateStateAndAppendToWindow(WindowRecord record) {

        Si.update(record, CASHREGISTERFREQUENCY);
        lastListInserted.add(record);

    }


    // the ordering key of the record does not exist in the queue
    private void updateStateAndWindow(WindowRecord record) {

        this.Si.update(record, CASHREGISTERFREQUENCY);
        List<WindowRecord> recordList = new ArrayList<>();
        recordList.add(record);
        queue.add(recordList);
        last = SerializationUtil.deepCopy(record);
        lastListInserted = recordList;

    }

    ////it has to be a factory object convertor that converts a WindowRecord from a StreamRecord
    private WindowRecord getWindowRecord(StreamRecord record){
        WindowWorldCupRecord r = new WindowWorldCupRecord((WorldCupRecord)record);
        DateTime dt = new DateTime(DateTimeZone.UTC);
        r.setWindowOrderingKey(Long.valueOf(((long) dt.getMillis()) / (Long.valueOf(1000)))); //congestion time according to external clock
        return r;
    }

    private  IToWindowRecordConvertor<WindowRecord, StreamRecord> getToWindowRecordConvertor(){
        return this.factory.getToWindowRecordConvertor();
    }

    //the length of the queue shapes the window size and each record of the queue is a list of the records
    // that have the same timestamp (the timestamp of stream record congestion time according to an external system clock)
    public void update(StreamRecord record, int frequency) {
        IToWindowRecordConvertor<?,?> convertor = this.factory.getToWindowRecordConvertor();
        WindowRecord wr = convertor.toWindowRecord(record);
        //reorders the records in the Window according to external wall clock as they arrive to each Node
        if (queue.isEmpty()) {
            updateStateAndWindow(wr);
        } else {
            if (((WorldCupRecord)record).getKey().longValue() == last.getWindowOrderingKey().longValue()) {
                updateStateAndAppendToWindow(wr);
            } else {
                updateStateAndWindow(wr);
            }
        }
    }
}


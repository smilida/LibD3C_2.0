package cn.edu.xmu.dm.d3c.threadpool;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FUNCTION: Concurrent training of weak classifiers
 *
 */

public class ClassifiersTrainingExecutor extends ThreadPoolExecutor {
    public ConcurrentHashMap<String, Date> startTimes;

    public ClassifiersTrainingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.startTimes = new ConcurrentHashMap<String, Date>();
    }

    public void shutdown() {
        System.out.printf("MyExecutor: Going to shutdown.\n", new Object[0]);
        System.out.printf("MyExecutor: Executed tasks:%d\n", new Object[] { Long.valueOf(getCompletedTaskCount()) });
        System.out.printf("MyExecutor: Running tasks:%d\n", new Object[] { Integer.valueOf(getActiveCount()) });
        System.out.printf("MyExecutor: Pending tasks:%d\n", new Object[] { Integer.valueOf(getQueue().size()) });
        super.shutdown();
    }

    protected void beforeExecute(Thread t, Runnable r) {
        System.out.printf("MyExecutor: A task is beginning: %s :%s\n", new Object[] { t.getName(), Integer.valueOf(r.hashCode()) });
        this.startTimes.put(String.valueOf(r.hashCode()), new Date());
    }

    protected void afterExecute(Runnable r, Throwable t) {
        Future<?> result = (Future)r;
        try {
            System.out.printf("*********************************\n", new Object[0]);
            System.out.printf("MyExecutor: A task is finishing.\n", new Object[0]);
            System.out.printf("MyExecutor: queueSize: %d\n", new Object[] { Integer.valueOf(getQueue().size()) });
            Date startDate = this.startTimes.remove(String.valueOf(r.hashCode()));
            Date finishDate = new Date();
            long diff = finishDate.getTime() - startDate.getTime();
            System.out.printf("MyExecutor: Duration: %d\n", new Object[] { Long.valueOf(diff) });
            System.out.printf("*********************************\n", new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

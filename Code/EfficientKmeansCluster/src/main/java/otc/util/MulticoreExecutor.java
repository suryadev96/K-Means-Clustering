package otc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MulticoreExecutor {
	private static final Logger logger = LoggerFactory.getLogger(MulticoreExecutor.class);

    /** Utility classes should not have public constructors. */
    private MulticoreExecutor() {

    }

    /**
     * The number of processors.
     */
    private static int nprocs = -1;
    /**
     * Thread pool.
     */
    private static ThreadPoolExecutor threads = null;

    /** Creates the worker thread pool. */
    private static void createThreadPool() {
        if (nprocs == -1) {
            int n = -1;
            try {
                String env = System.getProperty("smile.threads");
                if (env != null) {
                    n = Integer.parseInt(env);
                }
            } catch (Exception ex) {
                logger.error("Failed to create multi-core execution thread pool", ex);
            }

            if (n < 1) {
                nprocs = Runtime.getRuntime().availableProcessors();
            } else {
                nprocs = n;
            }

            if (nprocs > 1) {
                threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(nprocs, new SimpleDeamonThreadFactory());
            }
        }
    }

    /**
     * Returns the number of threads in the thread pool. 0 and 1 mean no thread pool.
     * @return the number of threads in the thread pool
     */
    public static int getThreadPoolSize() {
        createThreadPool();
        return nprocs;
    }

    /**
     * Executes the given tasks serially or parallel depending on the number
     * of cores of the system. Returns a list of result objects of each task.
     * The results of this method are undefined if the given collection is
     * modified while this operation is in progress.
     * @param tasks the collection of tasks.
     * @return a list of result objects in the same sequential order as
     * produced by the iterator for the given task list.
     * @throws Exception if unable to compute a result.
     */
    public static <T> List<T> run(Collection<? extends Callable<T>> tasks) throws Exception {
        createThreadPool();

        List<T> results = new ArrayList<T>();
        if (threads == null) {
            for (Callable<T> task : tasks) {
                results.add(task.call());
            }
        } else {
            if (threads.getActiveCount() < nprocs) {
                List<Future<T>> futures = threads.invokeAll(tasks);
                for (Future<T> future : futures) {
                    results.add(future.get());
                }
            } else {
                // Thread pool is busy. Just run in the caller's thread.
                for (Callable<T> task : tasks) {
                    results.add(task.call());
                }
            }
        }
        
        return results;
    }
    
    /**
     * Shutdown the thread pool.
     */
    public static void shutdown() {
        if (threads != null) {
            threads.shutdown();
        }
    }
}

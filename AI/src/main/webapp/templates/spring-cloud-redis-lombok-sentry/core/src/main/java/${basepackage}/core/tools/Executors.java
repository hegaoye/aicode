package ${basePackage}.core.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 *
 * @author lixin 12-10-31 上午8:46
 */
public class Executors {
    private static final int NUM = 5;
    private static ExecutorService singleExecutorService = null;
    private static ExecutorService cachePoolExecutorService = null;
    private static ExecutorService fixedPoolExecutorService = java.util.concurrent.Executors.newFixedThreadPool(NUM);

    /**
     * 单任务线程执行，智能替换异常的线程，直到执行完毕
     *
     * @param runnable
     */
    public static void singleThreadExecutor(Runnable runnable) {
        if (singleExecutorService == null) {
            singleExecutorService = java.util.concurrent.Executors.newSingleThreadExecutor();
        }
        singleExecutorService.execute(runnable);
    }

    /**
     * 智能扩展线程池，不够使用自动扩展大小进行执行
     *
     * @param runnable
     */
    public static void cacheThreadExecutor(Runnable runnable) {
        if (cachePoolExecutorService == null) {
            cachePoolExecutorService = java.util.concurrent.Executors.newCachedThreadPool();
        }
        cachePoolExecutorService.execute(runnable);
    }


    /**
     * 固定大小线程池可以控制对内存的消耗上，一个线程基本上是1M所以可以控制资源消耗，但是也控制了吞吐量，不建议使用于必要并发高的操作上
     *
     * @param runnable
     */
    public static void fixedThreadExecutor(Runnable runnable) {
        if (fixedPoolExecutorService == null) {
            fixedPoolExecutorService = java.util.concurrent.Executors.newFixedThreadPool(NUM);
        }
        fixedPoolExecutorService.execute(runnable);
    }


    public static void fixedThreadExecutor(Callable callable) {
        if (fixedPoolExecutorService == null) {
            fixedPoolExecutorService = java.util.concurrent.Executors.newFixedThreadPool(NUM);
        }
        fixedPoolExecutorService.submit(callable);
    }


}

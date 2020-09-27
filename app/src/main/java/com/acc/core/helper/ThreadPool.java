//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public ThreadPool() {
    }

    public static void submit(Runnable task) {
        executorService.submit(task);
    }

    public static void shutDown() {
        executorService.shutdown();
    }
}

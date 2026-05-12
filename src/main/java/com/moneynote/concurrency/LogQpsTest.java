package com.moneynote.concurrency;

import java.util.concurrent.*;

public class LogQpsTest {

    static final int THREADS = 4;
    static final int TASKS = 10_000_000;

    public static void main(String[] args) throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        long start = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(TASKS);

        for (int i = 0; i < TASKS; i++) {
            pool.execute(() -> {

                // 模拟业务（极轻）
                int x = 1 + 1;

                // ❗开关：打开/关闭这一行，对比QPS
                 System.out.println(x);

                latch.countDown();
            });
        }

        latch.await();
        long end = System.currentTimeMillis();

        System.out.println("耗时(ms): " + (end - start));
        System.out.println("QPS: " + (TASKS * 1000L / (end - start)));

        pool.shutdown();
    }
}

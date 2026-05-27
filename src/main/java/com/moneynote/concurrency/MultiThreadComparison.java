package com.moneynote.concurrency;

public class MultiThreadComparison {

    private static final long LIMIT = 2_000_000_000L; // 计算总量：20 亿

    public static void main(String[] args) throws InterruptedException {
        System.out.println("CPU 核心数: " + Runtime.getRuntime().availableProcessors());
        System.out.println("计算总量: " + LIMIT);
        System.out.println("----------------------------------------------");
        System.out.printf("%-15s | %-15s | %-10s\n", "线程数", "耗时 (ms)", "加速比");
        System.out.println("----------------------------------------------");

        // 执行单线程作为基准
        long baseTime = runTest(1);

        // 依次测试 2, 5, 10 线程
        runTestWithPerformance(2, baseTime);
        runTestWithPerformance(5, baseTime);
        runTestWithPerformance(10, baseTime);

        System.out.println("----------------------------------------------");
    }

    /**
     * 执行核心测试逻辑
     */
    private static long runTest(int threadCount) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[threadCount];
        long[] results = new long[threadCount];
        long range = LIMIT / threadCount;

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final long startRange = (long) index * range + (index == 0 ? 0 : 1);
            final long endRange = (index == threadCount - 1) ? LIMIT : (long) (index + 1) * range;

            threads[i] = new Thread(() -> {
                long localSum = 0;
                for (long j = startRange; j <= endRange; j++) {
                    localSum += j;
                }
                results[index] = localSum;
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        if (threadCount == 1) {
            System.out.printf("%-15d | %-15d | %-10s\n", threadCount, duration, "1.00x");
        }
        return duration;
    }

    /**
     * 带加速比计算的测试
     */
    private static void runTestWithPerformance(int threadCount, long baseTime) throws InterruptedException {
        long duration = runTest(threadCount);
        double speedup = (double) baseTime / duration;
        System.out.printf("%-15d | %-15d | %-10.2fx\n", threadCount, duration, speedup);
    }

}

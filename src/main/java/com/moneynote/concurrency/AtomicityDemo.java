package com.moneynote.concurrency;

public class AtomicityDemo {

    static int count = 0;

    public static void main(String[] args) throws Exception {

        int threadCount = 10;
        int loop = 10000;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < loop; j++) {
                    count++;  // 看起来是一个操作，其实不是原子
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("期望结果：" + (threadCount * loop));
        System.out.println("实际结果：" + count);
    }
}

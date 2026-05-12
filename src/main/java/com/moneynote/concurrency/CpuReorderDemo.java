package com.moneynote.concurrency;

public class CpuReorderDemo {

    static int[] bigArray = new int[1024 * 1024]; // 大数组，增加缓存未命中概率
    static volatile int a = 0;
    static volatile int b = 0;

    public static void main(String[] args) throws Exception {
        while (true) {
            test();
        }
    }

    static void test() throws Exception {
        a = 0;
        b = 0;

        Thread t1 = new Thread(() -> {
            // ❗ 慢操作：大概率触发缓存未命中（类似 load(A)）
            a = bigArray[(int)(System.nanoTime() % bigArray.length)] + 1;

            // ❗ 快操作：纯计算
            b = 1 + 2;
        });

        Thread t2 = new Thread(() -> {
            // 观察执行顺序
            if (b == 3 && a == 0) {
                System.out.println("发生重排序：先看到 b，再看到 a");
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

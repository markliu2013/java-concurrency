package com.moneynote.concurrency;

public class DirectOrderDemo {
    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                // 等待主线程放行
                while (startSignal == 0);

                // --- 关键点：重排序可能发生在这里 ---
                a = 1;
                x = b;
                // -----------------------------------

                finishSignal1 = 1;
                while (startSignal == 1); // 等待重置
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                while (startSignal == 0);

                b = 1;
                y = a;

                finishSignal2 = 1;
                while (startSignal == 1);
            }
        });

        t1.start();
        t2.start();

        for (long i = 0; i < Long.MAX_VALUE; i++) {
            x = 0; y = 0; a = 0; b = 0;
            finishSignal1 = 0; finishSignal2 = 0;

            // 释放信号，让两个线程同时开跑
            startSignal = 1;

            // 等待两个线程跑完这一轮
            while (finishSignal1 == 0 || finishSignal2 == 0);

            // 只要出现 x=0 且 y=0，说明 Store 被排到了 Load 后面
            if (x == 0 && y == 0) {
                System.err.println("第 " + i + " 次实验，捕捉到重排序！x=" + x + ", y=" + y);
                System.exit(0);
            }

            // 重置信号
            startSignal = 0;
        }
    }

    // 使用 volatile 保证信号本身的可见性，但不影响 a, b 的重排序
    static volatile int startSignal = 0;
    static volatile int finishSignal1 = 0;
    static volatile int finishSignal2 = 0;
}

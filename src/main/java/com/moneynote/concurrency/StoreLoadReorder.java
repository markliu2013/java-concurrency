package com.moneynote.concurrency;

public class StoreLoadReorder {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        long count = 0;
        while (true) {
            count++;
            x = 0; y = 0;
            a = 0; b = 0;

            Thread t1 = new Thread(() -> {
                a = 1; // 线程 1 写
                x = b; // 线程 1 读
            });

            Thread t2 = new Thread(() -> {
                b = 1; // 线程 2 写
                y = a; // 线程 2 读
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            /*
             * 正常逻辑下，x 和 y 不可能同时为 0。
             * 除非：
             * 线程 1 发生了重排序：先执行 x = b (0)，再执行 a = 1
             * 线程 2 同时也发生了重排序：先执行 y = a (0)，再执行 b = 1
             */
            if (x == 0 && y == 0) {
                System.err.println("第 " + count + " 次实验，检测到重排序：x=0, y=0");
                break;
            }
        }
    }
}

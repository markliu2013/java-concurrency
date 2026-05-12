package com.moneynote.concurrency;

public class ReorderDemo {
    private static int a = 0;
    private static boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        // 循环多次以增加触发重排序优化的概率
        for (int i = 0; i < 1000000; i++) {
            a = 0;
            flag = false;

            // 线程 A：负责初始化数据
            Thread threadA = new Thread(() -> {
                a = 1;       // 语句 1
                flag = true; // 语句 2
            });

            // 线程 B：负责根据标记位读取数据
            Thread threadB = new Thread(() -> {
                if (flag) {  // 语句 3
                    // 如果发生重排序，flag 变为 true 时，a 可能还没被赋值为 1
                    if (a == 0) {
                        System.err.println("检测到重排序：flag 为 true 但 a 依然为 0！执行次数：");
                    }
                }
            });

            threadA.start();
            threadB.start();
            threadA.join();
            threadB.join();
        }
    }
}

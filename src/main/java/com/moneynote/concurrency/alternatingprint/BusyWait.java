package com.moneynote.concurrency.alternatingprint;

public class BusyWait {

    // true：数字线程执行
    // false：字母线程执行
    private static volatile boolean flag = true;

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 26; i++) {

                // 忙等（自旋）
                while (!flag) {
                    // 什么都不做，一直循环
                }

                System.out.print(i);

                flag = false; // 轮到字母线程
            }
        });

        Thread t2 = new Thread(() -> {
            for (char c = 'A'; c <= 'Z'; c++) {

                // 忙等（自旋）
                while (flag) {
                    // 什么都不做，一直循环
                }

                System.out.print(c);

                flag = true; // 轮到数字线程
            }
        });

        t1.start();
        t2.start();
    }

}

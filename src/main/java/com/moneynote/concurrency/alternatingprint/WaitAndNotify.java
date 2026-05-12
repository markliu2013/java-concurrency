package com.moneynote.concurrency.alternatingprint;

public class WaitAndNotify {

    // true：数字线程执行
    // false：字母线程执行
    private static volatile boolean flag = true;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 26; i++) {
                synchronized (lock) {
                    while (!flag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(i);
                    flag = false;
                    lock.notifyAll();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (char c = 'A'; c <= 'Z'; c++) {
                synchronized (lock) {
                    while (flag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(c);
                    flag = true;
                    lock.notifyAll();
                }
            }
        });


        t1.start();
        t2.start();
    }

}

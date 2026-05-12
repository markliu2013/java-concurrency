package com.moneynote.concurrency;

public class FlagStopFailure {

    // 一个普通的 flag 变量
    private static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            int i = 0;
            System.out.println("子线程启动，等待 stop 信号...");

            // 这种写法在 JIT 优化后极易出问题
            while (!stop) {
                i++;
                // 注意：不要在这里写 System.out.println，
                // 因为 println 源码里带了 synchronized，会强制刷新缓存，导致“实验失败”
            }

            System.out.println("子线程感知到 stop，任务结束。累加值：" + i);
        });

        worker.start();

        // 给子线程一点运行时间
        Thread.sleep(100);

        System.out.println("主线程准备修改 stop 信号为 true...");
        stop = true;
        System.out.println("主线程已修改 stop = " + stop);

        worker.join(); // 如果实验成功（其实是失败了），程序会卡在这里
        System.out.println("程序正常退出");
    }
}

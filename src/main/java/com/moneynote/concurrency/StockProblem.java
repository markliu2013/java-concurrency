package com.moneynote.concurrency;

import java.util.concurrent.CountDownLatch;

public class StockProblem {

    // 放大库存，更容易看到冲突
    private static int stock = 100;
    // 用于记录成功下单的总数
    private static int successCount = 0;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 1000;
        Thread[] threads = new Thread[threadCount];

        // 发令枪：让所有线程同时起跑
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    latch.await(); // 所有线程在这里等待
                    // 临界区：判断并扣减
                    if (stock > 0) {
                        // 模拟业务耗时，放大并发冲突的概率
                        Thread.sleep(100);
                        stock--;
                        successCount++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 启动所有线程
        for (Thread t : threads) {
            t.start();
        }

        System.out.println("所有线程已就位，准备开抢！");
        latch.countDown(); // 打响发令枪！

        // 等待所有线程结束
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("--- 结果统计 ---");
        System.out.println("预期剩余库存: 0");
        System.out.println("实际剩余库存: " + stock);
        System.out.println("成功下单人数: " + successCount);
        System.out.println("超卖数量: " + (successCount - stock));
    }
}

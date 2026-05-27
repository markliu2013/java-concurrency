package com.moneynote.concurrency;

import java.util.concurrent.*;

public class OrderPerformanceDemo {

    // 按照要求修改后的 4 个核心方法及对应耗时
    private static void deductStock() { delay(500); }    // 扣减库存 (单位：毫秒)
    private static void createOrder() { delay(80); }     // 创建订单
    private static void pay() { delay(150); }            // 支付
    private static void delivery() { delay(120); }       // 物流

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== 开始电商下单性能对比测试 ===");

        // 1. 单线程串行下单
        long startSerial = System.currentTimeMillis();
        runSerialOrder();
        long endSerial = System.currentTimeMillis();
        System.out.println("【单线程串行】下单总耗时: " + (endSerial - startSerial) + " ms\n");

        // 2. 多线程异步下单
        // 创建线程池来处理异步任务
        ExecutorService executor = Executors.newFixedThreadPool(4);

        long startAsync = System.currentTimeMillis();
        runAsyncOrder(executor);
        long endAsync = System.currentTimeMillis();
        System.out.println("【多线程异步】用户感知到的响应耗时: " + (endAsync - startAsync) + " ms");

        // 关闭线程池
        executor.shutdown();
    }

    /**
     * 单线程串行下单：所有步骤必须一步一步同步等待完成
     */
    private static void runSerialOrder() {
        System.out.println("串行下单开始...");
        deductStock();        // 1. 扣减库存
        createOrder();        // 2. 创建订单
        pay();                // 3. 支付
        delivery();           // 4. 物流
        System.out.println("串行下单结束。");
    }

    /**
     * 多线程异步下单：核心步骤串行，非核心/后续推进步骤异步化
     */
    private static void runAsyncOrder(ExecutorService executor) throws InterruptedException, ExecutionException {
        System.out.println("异步下单开始...");

        // 核心强一致性业务：必须先同步执行，确保有货且订单生成成功
        deductStock();        // 1. 扣减库存
        createOrder();        // 2. 创建订单

        // 非核心主流程阻塞业务：支付单初始化和物流对接，允许异步处理或并行推进
        // 注：在实际复杂的微服务架构中，支付和物流往往通过消息队列（MQ）或线程池异步解耦
        CompletableFuture<Void> payTask = CompletableFuture.runAsync(OrderPerformanceDemo::pay, executor);
        CompletableFuture<Void> deliveryTask = CompletableFuture.runAsync(OrderPerformanceDemo::delivery, executor);

        // 此时核心订单已经落库，主线程不需要同步等待 pay 和 delivery 的 150ms+120ms 耗时，
        // 就可以直接给用户响应“下单成功，请尽快支付”了。
        System.out.println("核心订单已生成，通知用户下单成功！(主线程流程结束)");
    }

    // 辅助方法：模拟耗时
    private static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

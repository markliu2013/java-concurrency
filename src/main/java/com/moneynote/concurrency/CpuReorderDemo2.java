package com.moneynote.concurrency;

import java.math.BigDecimal;

public class CpuReorderDemo2 {

    static int a = 0;
    static int b = 0;

    // 在类成员中准备一个大数组
    static int[] bigArray = new int[1024 * 1024 * 8]; // 32MB，超出 L3 Cache

    public static void main(String[] args) throws Exception {
        while (true) {
            test();
        }
    }

    static void test() throws Exception {
        a = 0;
        b = 0;

        Thread t1 = new Thread(() -> {
//             ❗ 真正的慢操作：从主内存读取一个随机位置（大概率不在缓存中）
//             利用当前纳秒数的低位做索引，制造随机性
            int index = (int)(System.nanoTime() & 0x7FFFFF);
            a = bigArray[index] + 1;

            // ❗ 快操作：纯计算
            b = 3;
        });

        Thread t2 = new Thread(() -> {
            // 观察执行顺序
            if (b == 3 && a == 0) {
                System.out.println("发生重排序：先看到 b，再看到 a");
                System.out.println("b=" + b);
                System.out.println("a=" + a);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

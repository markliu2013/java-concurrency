package com.moneynote.concurrency;

public class DiningPhilosophersDeadlock {

    private static final int N = 5;

    static class Philosopher extends Thread {
        private final Object left;
        private final Object right;
        private final int id;

        public Philosopher(int id, Object left, Object right) {
            this.id = id;
            // 强制按 hash 排序，保证顺序一致
            if (System.identityHashCode(left) < System.identityHashCode(right)) {
                this.left = left;
                this.right = right;
            } else {
                this.left = right;
                this.right = left;
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    eat();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void think() throws InterruptedException {
            System.out.println(id + "号哲学家在思考");
            Thread.sleep(100);
        }

        private void eat() throws InterruptedException {
            synchronized (left) {
                System.out.println(id + "号拿起左边筷子");

                // 故意让线程更容易形成环路
                Thread.sleep(100);

                synchronized (right) {
                    System.out.println(id + "号拿起右边筷子 → 开始吃饭");
                    Thread.sleep(100);
                }
            }
        }
    }

    public static void main(String[] args) {
        Object[] chopsticks = new Object[N];

        for (int i = 0; i < N; i++) {
            chopsticks[i] = new Object();
        }

        for (int i = 0; i < N; i++) {
            Object left = chopsticks[i];
            Object right = chopsticks[(i + 1) % N];

            new Philosopher(i, left, right).start();
        }
    }
}
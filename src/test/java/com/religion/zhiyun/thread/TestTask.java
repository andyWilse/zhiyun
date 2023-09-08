package com.religion.zhiyun.thread;

public class TestTask {
    public static void main(String[] args) {
        int i = 0;
        while (true) {
            if (i == 10) break;
            try {
                new Thread(new Task(i++)).start();
            } catch (Exception e) {
                System.out.println("catch exception...");
            }
        }
    }
}

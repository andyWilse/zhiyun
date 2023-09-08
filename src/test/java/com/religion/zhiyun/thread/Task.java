package com.religion.zhiyun.thread;

public class Task implements Runnable{
    private int i;

    public Task(int i) {
        this.i = i;
    }
    @Override
    public void run() {
        if (i == 5) {
            //System.out.println("throw exception");
            throw new IllegalArgumentException();
        }
        System.out.println(i);
    }
}

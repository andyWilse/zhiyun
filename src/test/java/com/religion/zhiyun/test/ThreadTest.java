package com.religion.zhiyun.test;

public class ThreadTest {
    public static void main(String[] args) {
         /*   JSONObject resp = new JSONObject();
        resp.put("uri", request.getRequestURI());
        resp.put("url", request.getRequestURL());*/

        System.out.println("创建新线程去执行其他逻辑，不会阻塞当前请求");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("新线程内逻辑执行..." + System.currentTimeMillis());
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("执行完毕 " + System.currentTimeMillis());
            }
        }).start();

        System.out.println("http响应返回 " + System.currentTimeMillis());
    }
}

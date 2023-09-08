package com.religion.zhiyun.thread;

public class ThreadTest {
    public static void main(String[] args) {
         /*   JSONObject resp = new JSONObject();
        resp.put("uri", request.getRequestURI());
        resp.put("url", request.getRequestURL());*/

        System.out.println("create start!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("run..." + System.currentTimeMillis());
                try {
                    //Thread.sleep(9000);
                    if(0==0){
                        throw new RuntimeException("error!!!");
                    }
                    System.out.println("run run run !");
                } catch (RuntimeException e) {
                    System.out.println("miss miss miss !");
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("over: " + System.currentTimeMillis());
            }

        }).start();

        System.out.println("httpResponse: " + System.currentTimeMillis());
    }


}

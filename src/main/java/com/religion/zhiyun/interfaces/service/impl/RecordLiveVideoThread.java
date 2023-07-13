package com.religion.zhiyun.interfaces.service.impl;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 录制直播视频到本地
 */
public class RecordLiveVideoThread implements Runnable {
    /**
     * 流地址 例如：rtmp://58.200.131.2:1935/livetv/hunantv 湖南卫视
     */
    //private String streamUrl = "https://flvopen.ys7.com:9188/openlive/0db58788a9df411cb0977f08c804b98a.hd.flv";

    //private String streamUrl =" http://172.20.23.35:2080/rtp/330300-01674046015445950466.flv";
    private String streamUrl ="https://xlspgx.wenzhou.gov.cn:2443/rtp/330300-01674046015445950466.flv";

    //private String streamUrl ="https://sample-videos.com/video123/flv/720/big_buck_bunny_720p_1mb.flv";
    /**
     * 停止录制时长 0为不限制时长
     */
    private long timesSec = 0L;
    /**
     * 视频文件的输出路径
     */
    private String outFilePath;
    /**
     * 录制的视频文件格式(文件后缀名)
     */
    private String filenameExtension = "mp4";
    /**
     * 是否录制音频
     */
    private boolean hasAudio = false;

    @Override
    public void run() {
        if(outFilePath == null || outFilePath.length() == 0){
            System.out.println("文件输出路径不能为空。");
            return;
        }
        System.out.println("outFilePath："+outFilePath);
        //根据直播链接实例FFmpeg抓帧器
        System.out.println("ss001：");
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamUrl);
        System.out.println("ss002：");
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();
            System.out.println("ss003：");
            Frame frame = grabber.grabFrame();
            System.out.println("ss004：");
            if (frame != null) {
                //保存到本地的文件
                System.out.println("ss005：");
                File outFile = new File(outFilePath);
                System.out.println("ss006：");
                //文件不存在 || 文件不是一个普通文件
                if(!outFile.exists() || !outFile.isFile()){
                    if(!outFile.createNewFile()){
                        System.out.println("文件创建失败");
                        return;
                    }
                }
                // 视频输出地址,视频宽分辨率(宽,高),是否录制音频（0:不录制/1:录制）
                recorder = new FFmpegFrameRecorder(outFilePath, 200, 200, hasAudio ? 1 : 0);
                //直播流格式
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                //录制的视频格式
                recorder.setFormat(filenameExtension);
                //视频帧数
                recorder.setFrameRate(60);
                //开始录制
                recorder.start();
                // 计算结束时间
                long endTime = System.currentTimeMillis() + timesSec * 1000;
                // 如果没有到录制结束时间并且获取到了下一帧则继续录制
                while ((System.currentTimeMillis() < endTime) && (frame != null)) {
                    //录制
                    recorder.record(frame);
                    //获取下一帧
                    frame = grabber.grabFrame();
                }
                recorder.record(frame);
            }
            System.out.println("录制完成。");
        } catch (IOException e) {
            System.out.println("录制出错。");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("出错。");
            e.printStackTrace();
        }finally {
            //停止录制
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) {
        RecordLiveVideoThread recordVideoThread = new RecordLiveVideoThread();
        recordVideoThread.outFilePath = "D:\\test.mp4";
        //最好设置结束时长 如直接停止程序会造成输出文件的损坏无法正常播放
        recordVideoThread.timesSec = 15L;
        recordVideoThread.hasAudio = true;

        new Thread(recordVideoThread).start();
    }

    public  void play(String url) {
        /*File[] files = File.listRoots();
        String path="";
        for(File f:files ){
            path = f.getAbsoluteFile().getPath();
        }
        System.out.println("视频保存地址："+path);
*/

        streamUrl =url;
        RecordLiveVideoThread recordVideoThread = new RecordLiveVideoThread();
        String path = this.getClass().getResource("/").getPath();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());

        System.out.println("path:"+path);
        recordVideoThread.outFilePath = path+"\\test20230705-"+date+".mp4";
        //最好设置结束时长 如直接停止程序会造成输出文件的损坏无法正常播放
        recordVideoThread.timesSec = 15L;
        recordVideoThread.hasAudio = true;
        new Thread(recordVideoThread).start();
    }
}


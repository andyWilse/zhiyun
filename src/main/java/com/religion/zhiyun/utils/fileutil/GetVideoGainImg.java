package com.religion.zhiyun.utils.fileutil;

import com.religion.zhiyun.utils.fileutil.init.FilePropertiesVo;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import  org.bytedeco.ffmpeg.global.avcodec.*;

/**
 * 获取视频首帧图片用于界面展示
 *
 * @author LunarYouI
 * 基本信息描述：https://www.cnblogs.com/liangjingfu/p/12858018.html
 *
 * Input #0, mov,mp4,m4a,3gp,3g2,mj2, from 'F:\temporary\123456789.mp4':
 *   Metadata:
 *     major_brand     : isom
 *     minor_version   : 512
 *     compatible_brands: isomiso2avc1mp41
 *     encoder         : Lavf58.12.100
 *     description     : Tencent SID MTS
 *   Duration: 00:04:13.21, start: 0.000000, bitrate: 3167 kb/s
 *     Stream #0:0(und): Video: h264 (High) (avc1 / 0x31637661), yuv420p, 1280x720 [SAR 1:1 DAR 16:9], 3033 kb/s, 24 fps, 24 tbr, 90k tbn, 48 tbc (default)
 *     Metadata:
 *       handler_name    : VideoHandler
 *     Stream #0:1(und): Audio: aac (LC) (mp4a / 0x6134706D), 44100 Hz, stereo, fltp, 128 kb/s (default)
 *     Metadata:
 *       handler_name    : SoundHandler
 *
 *
 * @create 2022-06-13 9:41
 */

@Slf4j
public class GetVideoGainImg {
    /**
     *
     * @Title: getTempPath
     * @Description: 生成视频的首帧图片方法
     * @author: Zing
     * @param: @param tempPath 生成首帧图片的文件地址
     * @param: @param filePath 传进来的线上文件
     * @param: @return
     * @param: @throws Exception
     * @return: boolean
     * @throws
     */
    public static boolean getTempPath(String tempPath, String filePath) throws Exception {
        File targetFile = new File(tempPath);
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        File file2 = new File(filePath);
        System.out.println("确认文件是否是视频...");
        //判断文件是否为视频
        if(GetVideoGainImg.isVideo(filePath)) {
            System.out.println("确认成功！");
            //判断文件是否存在
            if (file2.getParentFile().exists()) {
                System.out.println("确认存在！");
                FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file2);
                ff.start();
                int ftp = ff.getLengthInFrames();
                int flag=0;
                Frame frame = null;
                while (flag <= ftp) {
                    //获取帧
                    frame = ff.grabImage();
                    //过滤前3帧，避免出现全黑图片
                    if ((flag>3)&&(frame != null)) {
                        break;
                    }
                    flag++;
                }
                if(ImageIO.write(FrameToBufferedImage(frame), "jpg", targetFile)) {
                    ff.close();
                    ff.stop();
                    System.out.println("输出图片成功！");
                    return true;
                }else {
                    ff.close();
                    ff.stop();
                    System.out.println("输出图片失败！");
                    return false;
                }
            }else{
                System.out.println("路径内容错误！"+file2.getParentFile());
                return false;
            }
        }else{
            System.out.println("确认失败！");
            return false;
        }
    }

    /***
     *
     * @Title: FrameToBufferedImage
     * @Description: 创建格式化BufferedImage对象
     * @author: Zing
     * @param: @param frame
     * @param: @return
     * @return: RenderedImage
     * @throws
     */
    private static RenderedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    /**
     *
     * @Title: isVideo
     * @Description:判断是不是视频
     * @author: Zing
     * @param: @param path 文件路径
     * @param: @return
     * @return: boolean       true是视频 false非视频
     * @throws
     */
    public static boolean isVideo(String path) {
        //设置视频后缀
        List<String> typeList = new ArrayList<String>();
        typeList.add("mp4");
        typeList.add("flv");
        typeList.add("avi");
        typeList.add("rmvb");
        typeList.add("rm");
        typeList.add("wmv");
        //获取文件名和后缀
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        for(String type : typeList) {
            //统一为大写作比较
            if(type.toUpperCase().equals(suffix.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 视频关键帧截图
     * @param videoPath 视频路径
     * @param second 需要截取关键帧的视频时间
     * @return
     */
    public static String generateImage(String videoPath, long second){
        String picName  = UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
        String uploadFilePath="E:\\0file\\xiazai";
        File tempFileDir = new File(uploadFilePath);
        if(!tempFileDir.exists()){
            tempFileDir.mkdirs();
        }
        String imagePath = uploadFilePath + picName;

        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videoPath);
            ff.start();

            Frame f  = null;

            //传入的参数是秒，这里需要处理的时间戳是按微秒走的,需要转换一下
            /*Double d = Double.parseDouble(secs);
            long targetFrameNum = (long) (d * 1000 * 1000);*/
            long targetFrameNum = second * 1_000_000L;

            ff.setTimestamp(targetFrameNum);

            //根据传入的时间戳，抓取指定时间的视频关键帧
            f = ff.grabImage();
            if (f != null) {
                //处理并生成图片
                int owidth = f.imageWidth;
                int oheight = f.imageHeight;
                // 对截取的帧进行等比例缩放
                int width = 800;
                int height = (int) (((double) width / owidth) * oheight);
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage fecthedImage = converter.getBufferedImage(f);
                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                bi.getGraphics().drawImage(fecthedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH),
                        0, 0, null);
                ImageIO.write(bi, "jpg", new File(imagePath));
            }

            ff.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return picName;
    }

    public static void main(String[] args) throws Exception {
        //boolean tempPath = GetVideoGainImg.getTempPath("F:/temporary/542.jpg", "F:/temporary/123456789.mp4");

        String tempPath = GetVideoGainImg.generateImage("F:\\13files\\101.mp4", 1);

        System.out.println("获取首帧是否成功！"+tempPath);

        //generate("F:\\13files\\101.mp4", "E:\\0file\\xiazai\\thumb.jpg", 1); // 截取第 10 秒

    }

    /**
     * 获取历史视频
     * @param rootPath
     * @param newImgName
     * @param vpFrame
     * @return
     */
    public static Boolean cutStreamPicture(String rootPath,String newImgName,Integer vpFrame){
        // 替换为你的实际 TCP 地址
        //String streamUrl = "tcp://131.28.0.7:7082?listen";
        String streamUrl = FilePropertiesVo.getStreamUrl();
        // 目标截取第几帧
        int targetFrameNumber = vpFrame;
        // 保存图片的路径
        String outputPath = rootPath+newImgName;
        boolean isSaved = false;
        log.info("开始创建 Grabber 实例");
        FFmpegLogCallback.set();
        // 1. 创建 Grabber 实例
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamUrl)) {
            // 如果是 RTSP 流，建议设置超时和传输协议（此处为 TCP，不需要）
             grabber.setOption("rtsp_transport", FilePropertiesVo.getStreamMode());
            // 设置超时时间（毫秒），防止流断开时一直卡住
            grabber.setOption("timeout", FilePropertiesVo.getStreamTime());
            grabber.setOption("rw_timeout", FilePropertiesVo.getStreamTime());
            //加大缓冲
            grabber.setOption("buffer_size", "10240000");
            //忽略音频流
            grabber.setOption("an", "1");
            grabber.setOption("analyzeduration", "5000000");
            grabber.setOption("probesize", "5000000");
            grabber.setVideoOption("err_detect","ignore_err");
            grabber.setVideoOption("fflags","+discardcorrupt");
            //grabber.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_BGR24);
            // 强制指定输入格式为 h264（裸流），而不是让 FFmpeg 去猜测容器
            grabber.setFormat(FilePropertiesVo.getStreamCode());
            // 明确视频编码，有助于解码器初始化
            grabber.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
            //grabber.setVideoCodec(173);
            // 2. 启动抓取器
            grabber.start();
            log.info("Grabber 启动抓取器");

            Frame frame;
            int currentFrame = 0;
            // 用于将 JavaCV 的 Frame 转换为 Java 标准的 BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            log.info("开始读取视频流...");
            // 3. 逐帧读取
            while ((frame = grabber.grabImage()) != null) {
                log.info("视频流:"+frame);
                currentFrame++;
                log.info("视频流帧数:"+currentFrame);
                // 判断是否到达目标帧
                if (currentFrame == targetFrameNumber) {
                    // 4. 转换并保存图片
                    BufferedImage bufferedImage = converter.convert(frame);

                    if (bufferedImage != null) {
                        File outputFile = new File(outputPath);
                        // 确保目录存在
                        if (!outputFile.getParentFile().exists()) {
                            outputFile.getParentFile().mkdirs();
                        }
                        ImageIO.write(bufferedImage, "jpg", outputFile);
                        log.info("成功截取第 " + targetFrameNumber + " 帧，已保存至: " + outputPath);
                        isSaved = true;
                    }
                    break; // 截取到目标帧后退出循环
                }
            }

            if (!isSaved) {
                log.info("未能读取到第 " + targetFrameNumber + " 帧，流可能提前结束或帧数不足。");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSaved;
    }



 /*   public void openCam() throws IOException {
        if (!webcam.isOpen()) {
            webcam.open();
        }
        Boolean isOpened = true;
        DatagramSocket udpSocket = new DatagramSocket();
        InetAddress targetHost = InetAddress.getByName("localhost");
        int targetPort = 80;

        while (isOpened) {
            List<BufferedImage> list = new ArrayList<>();
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start <= 10000) {
                BufferedImage bfi = webcam.getImage();
                if (needsLight) {
                    bfi = bfiOps.light(bfi, lightIndex);
                }
                if (needDuibidu) {
                    bfi = bfiOps.duibidu(bfi, duibiduIndex);
                }
                list.add(bfi);
            }
            System.out.println("鹿丸！，开存！小节视频长度：" + (System.currentTimeMillis() - start));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] byteArray = outputStream.toByteArray();
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, webcam.getViewSize().width, webcam.getViewSize().height);
            recorder.setVideoCodecName("lib264");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(24);
            System.out.println("recorder: " + recorder);
            recorder.start();
            for (int i = 0; i < list.size(); i++) {
                Frame frame = converter.getFrame(list.get(i));
                recorder.record(frame);
            }
            recorder.stop();
            recorder.release();
            System.out.println("后台保存完毕");
            RawPacket rtpPacket = new RawPacket(byteArray, 0, byteArray.length);//构造中需要传入起始终结，可能表明不是让一遍传完
            DatagramPacket udpPacket = new DatagramPacket(rtpPacket.getBuffer(), rtpPacket.getLength(), targetHost, targetPort);
            udpSocket.send(udpPacket);

            udpSocket.close();
            websocket.sendOneMessage("0", "down");
        }
        webcam.close();
    }*/



}



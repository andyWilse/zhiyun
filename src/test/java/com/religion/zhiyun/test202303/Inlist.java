package com.religion.zhiyun.test202303;

import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.fileutil.DownloadPicture;
import com.religion.zhiyun.utils.fileutil.init.FilePropertiesVo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

public class Inlist {
    public static void main(String[] args) throws UnsupportedEncodingException {
        /*String aa="5b6b7b0ee8b66c19c674495a21e8c2d4";
        String encode = URLEncoder.encode(aa, "UTF-8");
        String decode =URLDecoder.decode(encode,"UTF-8");
        System.out.println(encode);*/
        /*String aa="https://zjc-cw.sittone.com:51443/hook/v1/gb/algorithm/image?storageId=B5&fileName=pkg30/rect/motorVehicleIllegalParkAreaDetection/20230607/33030400002000200826/33030400001310671922/23563759278_5618276.jpg";
        String[] split = aa.split("=");
        int length = split.length;
        String s = split[length - 1];
        System.out.println(s);*/
        String picturePath="https://img2.doubanio.com/view/status/l/public/142605515-a26d520a5d06573.jpg";
        String filePathBase="F:\\home"+File.separator+TimeTool.getCurrentYear(new Date())+File.separator+TimeTool.getCurrentMonth(new Date());
        //filePathBase="F:\\home\\a.";
        DownloadPicture.downloadPic(picturePath,filePathBase);

    }
}

package com.religion.zhiyun.utils.fileutil.init;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 生成验证码的配置
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        //设置验证码的宽度
        properties.setProperty("kaptcha.image.width", "100");
        //设置宽度
        properties.setProperty("kaptcha.image.height", "40");
        //设置字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "15");
        //设置字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        //字体,默认值Arial, Courier(如果使用中文验证码，则必须使用中文的字体，否则出现乱码)
        properties.put(".textproducer.font.names","Courier");
        //限定验证码中的字符
        //properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        //设置验证码的长度
        //properties.setProperty("kaptcha.textproducer.char.length", "4");
        //设置添加噪声与否
        //properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        //文字渲染器，默认值priv.kerlomz.kaptcha.text.impl.DefaultWordRenderer
        //properties.put("kaptcha.word.impl","priv.kerlomz.kaptcha.text.impl.DefaultWordRenderer");
        //将配置装载到一个实例中
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        //将配置传入实例
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }
}


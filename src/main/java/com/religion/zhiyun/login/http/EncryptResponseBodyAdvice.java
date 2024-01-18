package com.religion.zhiyun.login.http;

import cn.hutool.crypto.SecureUtil;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.UnsupportedEncodingException;

/**
 * 请求响应处理类<br>
 *
 * 对加了@Encrypt的方法的数据进行加密操作
 *
 * @author wangchj
 *
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Value("${crypto.charset}")
    private String charset = "UTF-8";
    @Value("${crypto.key}")
    private String key;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
        //return NeedCrypto.needEncrypt(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Object encrypt=null;
        try {
            //TODO 实现具体的加密方法
            String data = JsonUtils.beanToJson(body);
            //加密
            encrypt = SecureUtil.aes(key.getBytes(charset)).encrypt(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encrypt;

    }

}

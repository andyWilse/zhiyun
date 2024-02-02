package com.religion.zhiyun.login.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 请求数据接收处理类<br>
 *
 * 对加了@Decrypt的方法的数据进行解密操作<br>
 *
 * 只对 @RequestBody 参数有效
 * @author wangchj
 */
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    @Value("${crypto.charset}")
    private String charset = "UTF-8";
    @Value("${crypto.key}")
    private String key;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        if( NeedCrypto.needDecrypt(parameter) ){
            return new DecryptHttpInputMessage(inputMessage , charset , key);
        }
        return inputMessage;
        //return new DecryptHttpInputMessage(inputMessage , charset , key);//请求信息解密，参考DecryptHttpInputMessage解密类
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}

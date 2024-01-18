package com.religion.zhiyun.login.http;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author wangchj
 */
public class DecryptHttpInputMessage implements HttpInputMessage {
    private HttpInputMessage inputMessage;
    private String charset;
    private String key;

    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String charset , String key) {
        this.inputMessage = inputMessage;
        this.charset = charset;
        this.key = key;
    }

    @Override
    public InputStream getBody() throws IOException {
        //使用hutool开始解密
        String content = IoUtil.read(inputMessage.getBody() , charset);
        byte[] bytes = SecureUtil.aes(key.getBytes(charset)).decrypt(content);

        return new ByteArrayInputStream(bytes);
    }

    @Override
    public HttpHeaders getHeaders() {
        return inputMessage.getHeaders();
    }

}

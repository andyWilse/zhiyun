package com.religion.zhiyun.utils.sms.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.religion.zhiyun.utils.JsonUtils;
import org.springframework.http.HttpMethod;

/**
 * 请求参数
 */
public class HttpParams {
    private Map<String, Object> params = new HashMap<String, Object>();
    private HttpMethod httpMethod;
    private String jsonParam = "";

    public HttpParams(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpParams httpPostParams() {
        return new HttpParams(HttpMethod.POST);
    }

    public static HttpParams httpGetParams() {
        return new HttpParams(HttpMethod.GET);
    }

    public HttpParams addParam(String name, Object value) {
        this.params.put(name, value);
        return this;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public String getQueryString(String charset) throws IOException {
        if ((this.params == null) || (this.params.isEmpty())) {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, Object>> entries = this.params.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String name = entry.getKey();
            Object val = entry.getValue();
            String value="";
            if(val instanceof String){
                value=(String) val;
            }else if(val instanceof Integer){
                value=String.valueOf(val);
            }
            query.append("&").append(name).append("=").append(URLEncoder.encode(value, charset));
        }
        return query.substring(1);
    }

    public boolean isJson() {
        return !isEmpty(this.jsonParam);
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public String toString() {
        return "HttpParams " + JSON.toJSONString(this);
    }

    public String getJsonParam() {
        return this.jsonParam;
    }

    public void setJsonParamer(Map<String, Object> jsonParam) {
        //this.jsonParam = JSON.toJSONString(jsonParam);
        //this.jsonParam = JSONObject.toJSONString(jsonParam);
        this.jsonParam = JsonUtils.mapToJson(jsonParam);
    }
    //base64加密 json
    public void setBase64JsonParam(Map<String, Object> paramMap) {
        //转json
        String params = JsonUtils.mapToJson(paramMap);
        //加密
        /*String secret = DigestUtils.md5DigestAsHex((params).getBytes());
        System.out.println("md5加密："+secret);*/
        //加密
        String base64 = Base64.getEncoder().encodeToString(params.getBytes());
        this.jsonParam = base64;
    }
    private static boolean isEmpty(CharSequence cs) {
        return (cs == null) || (cs.length() == 0);
    }
}

package com.iflytek.asrc.callback;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/get")
public class Result {

    @ResponseBody
    @GetMapping("/result")
    public String result(String  appId, String accessKeyId,  String accessKeySecret, String taskId) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("appId", appId);
        queryParam.put("accessKeyId", accessKeyId);
        queryParam.put("utc", sdf.format(new Date()));
        queryParam.put("uuid", UUID.randomUUID().toString());
        queryParam.put("signature","");

        String signature = signature(accessKeySecret, queryParam);
        String s=getResult(queryParam, signature, taskId);
        return s;
    }

    private String getResult(Map<String, String> queryParam, String signature, String taskId) {
        String url = "https://office-api-ckm-dx.iflyaisol.com/ckm/v1/result/query";
        OkHttpClient client = new OkHttpClient();

        // 构建请求参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("appId", queryParam.get("appId"));
        urlBuilder.addQueryParameter("accessKeyId", queryParam.get("accessKeyId"));
        urlBuilder.addQueryParameter("utc", queryParam.get("utc"));
        urlBuilder.addQueryParameter("uuid", queryParam.get("uuid"));
        urlBuilder.addQueryParameter("signature", signature);
        String finalUrl = urlBuilder.build().toString();

        // 构建请求体
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("taskId", taskId);
        requestBodyJson.put("type", 4);

        // 创建请求
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyJson.toJSONString());
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                // 处理错误响应
                System.out.println("Request failed: " + response.code());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String signature(String accessKeySecret, Map<String, String> queryParam) throws Exception {
        TreeMap<String, String> treeMap = new TreeMap<>(queryParam);
        treeMap.remove("signature");
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                String encode = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                builder.append(entry.getKey()).append("=").append(encode).append("&");
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        String baseString = builder.toString();
        System.out.println("baseString：" + baseString);
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(accessKeySecret.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.name());
        mac.init(keySpec);
        byte[] signBytes = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signBytes);
    }


}

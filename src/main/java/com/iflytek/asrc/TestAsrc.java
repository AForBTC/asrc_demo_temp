package com.iflytek.asrc;

import com.alibaba.fastjson.JSON;
import com.iflytek.asrc.data.IstActionParam;
import com.iflytek.asrc.data.ResAudio;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/asrc")
class TestAsrc {
    public static void main(String[] args) {
        // 非实时转写请求地址
        String url = "http://172.30.8.97:33721/tuling/asr/v21/ist/async/process";
        System.out.println(url);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        IstActionParam istActionParam = new IstActionParam();
        ResAudio audio = new ResAudio();
        audio.setAid("CTS-CN-F2F-2019-11-15-165");
        audio.setBits(16);
        audio.setChnl(1);
        audio.setEncoding(1);
//        audio.setOffset(5000);
//        audio.setSpnk(2);
//        audio.setRate(8000);
        audio.setRate(16000);
        // 音频文件地址
        audio.setUri("http://192.168.86.117:8086/CTS-CN-F2F-2019-11-15-165.wav");
        istActionParam.setAudio(audio);
        // 设置回调地址
        istActionParam.setCallback("http://10.5.7.16:8089/asrc/writefile");
        System.out.println(JSON.toJSONString(istActionParam));
        HttpEntity<IstActionParam> request = new HttpEntity<>(istActionParam, httpHeaders);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, request, String.class);
    }
}
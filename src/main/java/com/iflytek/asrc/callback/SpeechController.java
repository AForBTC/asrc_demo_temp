package com.iflytek.asrc.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.asrc.data.IstActionParam;
import com.iflytek.asrc.data.ResAudio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/speech")
public class SpeechController {
    private static final String UPLOAD_DIR = "/app/audio/";


    @PostMapping("/getSpeechText")
    public Map<String, Object> addFiles(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> res = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        //保存文件
        try {
            // 获取上传文件的字节流
            byte[] bytes = file.getBytes();
            // 构建上传文件的保存路径
            System.out.println("文件名称:" + UPLOAD_DIR + uuid + file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + uuid + file.getOriginalFilename());
            // 将文件保存到指定目录下
            Files.write(path, bytes);
            File fileC = new File(UPLOAD_DIR + uuid + file.getOriginalFilename());
            if (fileC.exists()) {
                System.out.println("文件成功输出");
            } else {
                System.out.println("文件输出失败");
            }
        } catch (Exception e) {
            System.out.println(e);
            res.put("code", 500);
            res.put("msg", "处理失败");
            return res;
        }
        String url = "http://134.80.191.172:33721/tuling/asr/v21/ist/async/process";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        IstActionParam istActionParam = new IstActionParam();
        ResAudio audio = new ResAudio();
        String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
        audio.setAid(uuid + fileName);
        audio.setBits(16);
        audio.setChnl(1);
        audio.setEncoding(1);
        audio.setOffset(0);
        audio.setRate(16000);
        audio.setSpnk(1);
        // 音频文件地址
        audio.setUri("http://134.84.148.14:7123/" + uuid  + file.getOriginalFilename());
        istActionParam.setAudio(audio);
        // 设置回调地址
        istActionParam.setCallback("http://134.84.148.14:9099/asrc/writefile");
        HttpEntity<IstActionParam> request = new HttpEntity<>(istActionParam, httpHeaders);
        ResponseEntity<String> stringResponseEntity = null;
        try {
            stringResponseEntity = restTemplate.postForEntity(url, request, String.class);
        }catch (Exception e) {
            System.out.println(e);
            res.put("code", 500);
            res.put("msg", "处理失败");
            return res;
        }
        String body = stringResponseEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(body);
        if(jsonObject.getJSONObject("state").getInteger("code") != 0){
            res.put("code", 500);
            res.put("msg", "处理失败");
            return res;
        } else {
            System.out.println("返回结果:" + jsonObject.toJSONString());
            long l = System.currentTimeMillis();
            while(true){
                try {
                    // 读取文件内容为字符
                    String content = new String(Files.readAllBytes(Paths.get("/app/audio_text/" + uuid + fileName + ".txt")));
                    res.put("code", 200);
                    res.put("msg", "成功");
                    String str = "/app/audio_text/" + uuid + fileName + ".txt";
                    System.out.println("map的值speech: " + WriteFile.r1.toString());
                    res.put("lattices", WriteFile.r1.get(str));
                    res.put("data", content);
                    System.out.println("获取成功，返回");
                    if(WriteFile.r1.get(str) != null){
                        WriteFile.r1.remove(str);
                    }
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + uuid +file.getOriginalFilename()));
                    Files.deleteIfExists(Paths.get("/app/audio_text/" + uuid + fileName + ".txt"));
                    break;
                } catch (IOException e) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime < l + 40000) {
                        continue;
                    } else {
                        Files.deleteIfExists(Paths.get(UPLOAD_DIR + uuid +file.getOriginalFilename()));
                        Files.deleteIfExists(Paths.get("/app/audio_text/" + uuid + fileName + ".txt"));
                        System.out.println("获取不到输出的结果文件");
                        res.put("code", 500);
                        res.put("msg", "失败");
                        break;
                    }
                }
            }
            return res;
        }
    }
}

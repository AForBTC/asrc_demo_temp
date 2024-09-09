package com.iflytek.asrc.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.asrc.data.IstActionParam;
import com.iflytek.asrc.data.ResAudio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
        String ip = getClientIp();
        log.info("客户端" + ip + ": 文件名字 " + file.getOriginalFilename());
        Map<String, Object> res = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        //保存文件
        try {
            // 获取上传文件的字节流
            byte[] bytes = file.getBytes();
            // 构建上传文件的保存路径
//            System.out.println("文件名称:" + UPLOAD_DIR + uuid + file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + uuid + file.getOriginalFilename());
            // 将文件保存到指定目录下
            Files.write(path, bytes);
            File fileC = new File(UPLOAD_DIR + uuid + file.getOriginalFilename());
            if (fileC.exists()) {
//                System.out.println("文件成功输出");
            } else {
                log.error("客户端" + ip + ": 文件没有成功保存");
            }
        } catch (Exception e) {
            log.error("客户端" + ip + ": " + e);
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
            log.error("客户端" + ip + ": " + e);
            res.put("code", 500);
            res.put("msg", "处理失败");
            return res;
        }
        String body = stringResponseEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(body);
        if(jsonObject.getJSONObject("state").getInteger("code") != 0){
            log.error("客户端" + ip + ": " + jsonObject.getJSONObject("state").getInteger("code"));
            res.put("code", 500);
            res.put("msg", "处理失败");
            return res;
        } else {
            long l = System.currentTimeMillis();
            while(true){
                try {
                    // 读取文件内容为字符
                    String content = new String(Files.readAllBytes(Paths.get("/app/audio_text/" + uuid + fileName + ".txt")));
                    res.put("code", 200);
                    res.put("msg", "成功");
                    String str = "/app/audio_text/" + uuid + fileName + ".txt";
                    res.put("lattices", WriteFile.r1.get(str));
                    res.put("data", content);
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
                        log.error("客户端" + ip + ": 获取不到输出的结果文件");
                        res.put("code", 500);
                        res.put("msg", "失败");
                        break;
                    }
                }
            }
            log.info("客户端" + ip + ": 结果成功返回");
            return res;
        }
    }

    public String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            return "No HTTP request in context";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
}

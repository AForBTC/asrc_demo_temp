package com.iflytek.asrc.callback;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.asrc.data.AsrcRequestCallback;
import com.iflytek.asrc.data.ParagraphInfo;
import com.iflytek.asrc.data.TextSummaryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/asrc")
public class CallbackController {

    @Autowired
    private  RestTemplate restTemplate;

    @RequestMapping("/callback")
    public String callback(@RequestBody AsrcRequestCallback asrcRequestCallback) {
        System.out.println("==========" + asrcRequestCallback);
        System.out.println("==========================================================================");
        System.out.println(JSON.toJSONString(asrcRequestCallback.getBody()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        StringBuffer stringBuffer = new StringBuffer();
        asrcRequestCallback.getBody().getLattices().forEach((lattices) -> {
//            log.info(dateFormat.format(lattices.getBegin())+":"+lattices.getSpk()+":"+lattices.getOnebest());
//            String jsonCn1best = lattices.getJson_cn1best();
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                JsonBest jsonBest = objectMapper.readValue(jsonCn1best, JsonBest.class);
////                System.out.println("开始时间："+dateFormat.format(jsonBest.getBg())+" 结束时间："+dateFormat.format(jsonBest.getEd())+" 内容："+lattices.getOnebest());
//                jsonBest.getWs().forEach(ws -> {
//                    ws.getCw().forEach(cw -> {
//                        if (cw.getWp().equals("n")){
////                            System.out.println("开始时间："+dateFormat.format(jsonBest.getBg()+cw.getWb()*10)+" 结束时间："+dateFormat.format(jsonBest.getBg()+cw.getWe()*10)+" 内容："+cw.getW());
//                            log.info("开始时间：{} 结束时间：{} {}",dateFormat.format(jsonBest.getBg()+cw.getWb()*10),dateFormat.format(jsonBest.getBg()+cw.getWe()*10),cw.getW());
//                        }
//                    });
//                });
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }


//            stringBuffer.append(lattices.getOnebest());
            stringBuffer.append(lattices.getSpk() + ": " + lattices.getOnebest() + "\n");
        });
//        System.out.println("全文结果："+stringBuffer);
        log.info("全文结果：{}", stringBuffer);
        return stringBuffer.toString();
    }


    @RequestMapping("/callbacktest")
    public String callbacktest(@RequestBody TextSummaryInfo textSummaryInfo) {
        System.out.println("==========================================================================");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
//        System.out.println("全文结果："+stringBuffer);
        return "success";
    }

//    @RequestMapping("/callback1")
//    public String callback1(@RequestBody Object asrcRequestCallback){
//        System.out.println("=========="+asrcRequestCallback);
//        System.out.println("==========================================================================");
//
////        SimpleDateFormat dateFormat =  new SimpleDateFormat("HH:mm:ss.SS");
////        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
////        StringBuffer stringBuffer = new StringBuffer();
////        asrcRequestCallback.getBody().getLattices().forEach((lattices)->{
////            String jsonCn1best = lattices.getJson_cn1best();
////            ObjectMapper objectMapper = new ObjectMapper();
////            try {
////                JsonBest jsonBest = objectMapper.readValue(jsonCn1best, JsonBest.class);
//////                System.out.println("开始时间："+dateFormat.format(jsonBest.getBg())+" 结束时间："+dateFormat.format(jsonBest.getEd())+" 内容："+lattices.getOnebest());
////                jsonBest.getWs().forEach(ws -> {
////                    ws.getCw().forEach(cw -> {
////                        if (cw.getWp().equals("n")){
//////                            System.out.println("开始时间："+dateFormat.format(jsonBest.getBg()+cw.getWb()*10)+" 结束时间："+dateFormat.format(jsonBest.getBg()+cw.getWe()*10)+" 内容："+cw.getW());
////                            log.info("开始时间：{} 结束时间：{} {}",dateFormat.format(jsonBest.getBg()+cw.getWb()*10),dateFormat.format(jsonBest.getBg()+cw.getWe()*10),cw.getW());
////                        }
////                    });
////                });
////            } catch (JsonProcessingException e) {
////                e.printStackTrace();
////            }
////
////
////            stringBuffer.append(lattices.getOnebest());
////        });
////        System.out.println("全文结果："+stringBuffer);
////        log.info("全文结果：{}",stringBuffer);
//        return "成功";
//    }


    @ResponseBody
    @GetMapping("/test")
    public String test(String  appId, String accessKeyId,String accessKeySecret) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Map<String, String> queryParam = new HashMap<>();

        queryParam.put("appId", appId);
        queryParam.put("accessKeyId", accessKeyId);
        queryParam.put("utc", sdf.format(new Date()));
        queryParam.put("uuid", UUID.randomUUID().toString());
        queryParam.put("signature","");

        String signature = signature(accessKeySecret, queryParam);
        queryParam.put("signature",signature);
        String result= testServer(queryParam, signature);
        return result;
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

    private String testServer(Map<String, String> queryParam, String signature) throws IOException {
//       String url = "https://office-api-ckm-dx.iflyaisol.com/ckm/v1/aggregate/async?appId=" +
//               queryParam.get("appId") + "&accessKeyId=" + queryParam.get("accessKeyId") +
//               "&utc=" + queryParam.get("utc") + "&uuid=" + queryParam.get("uuid") + "&signature=" + signature;
        String url = "https://office-api-ckm-dx.iflyaisol.com/ckm/v1/aggregate/async" + "?appId=" +
                  java.net.URLEncoder.encode(queryParam.get("appId"), "UTF-8")
                + "&accessKeyId=" + java.net.URLEncoder.encode(queryParam.get("accessKeyId"), "UTF-8")
                + "&utc=" + java.net.URLEncoder.encode(queryParam.get("utc"), "UTF-8")
                + "&uuid=" + java.net.URLEncoder.encode(queryParam.get("uuid"), "UTF-8")
                + "&signature=" + java.net.URLEncoder.encode(signature, "UTF-8");

        System.out.println("url:"+url);

        Map<String, Object> params = new HashMap<>();
        List<ParagraphInfo> paragraphInfos = new ArrayList<>();
        ParagraphInfo paragraphInfo = new ParagraphInfo();
        File file = new File("C:\\Users\\jwtang9\\Documents\\WeChat Files\\wxid_rb94189pdbsy22\\FileStorage\\File\\2023-12\\1211_ast1.txt");
        InputStream inputStream = new FileInputStream(file);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e){
        }


        paragraphInfo.setWs(stringBuilder.toString());
        paragraphInfos.add(paragraphInfo);
        params.put("ps", paragraphInfos);

        List<String> fcns = new ArrayList<>();
        fcns.add("8");
        params.put("fcns", fcns);

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json;charset=UTF-8");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, JSON.toJSONString(params));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseData = response.body().string();
            System.out.println(responseData);
        } else {
            System.out.println("Request failed: " + response.code() + " " + response.message());
        }

        return "26";
    }


    @ResponseBody
    @GetMapping("/test24")
    public String test24(String  appId, String accessKeyId,String accessKeySecret) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Map<String, String> queryParam = new HashMap<>();


        queryParam.put("appId", appId);
        queryParam.put("accessKeyId", accessKeyId);
        queryParam.put("utc", sdf.format(new Date()));
        queryParam.put("uuid", UUID.randomUUID().toString());
        queryParam.put("signature","");


        String signature = signature(accessKeySecret, queryParam);
        String result= testServer24(queryParam, signature);
        return result;
    }

    private String testServer24(Map<String, String> queryParam, String signature) throws IOException {
        String url = "https://office-api-ckm-dx.iflyaisol.com/ckm/v1/text_summary/async" + "?appId=" +
                java.net.URLEncoder.encode(queryParam.get("appId"), "UTF-8")
                + "&accessKeyId=" + java.net.URLEncoder.encode(queryParam.get("accessKeyId"), "UTF-8")
                + "&utc=" + java.net.URLEncoder.encode(queryParam.get("utc"), "UTF-8")
                + "&uuid=" + java.net.URLEncoder.encode(queryParam.get("uuid"), "UTF-8")
                + "&signature=" + java.net.URLEncoder.encode(signature, "UTF-8");

        System.out.println("url:" + url);

        Map<String, Object> params = new HashMap<>();


        File file = new File("D:\\PycharmWorkspace\\调用示例\\aaa_demo\\dialogue_output\\spk2.txt");
        InputStream inputStream = new FileInputStream(file);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
        }


        params.put("text", stringBuilder.toString());

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json;charset=UTF-8");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, JSON.toJSONString(params));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        String responseData = null;
        if (response.isSuccessful()) {
            responseData = response.body().string();
            System.out.println(responseData);
        } else {
            System.out.println("Request failed: " + response.code() + " " + response.message());
        }

        return responseData;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class paragraphInfo{
        String ws;
        String pg;
        String pd;
        String prl;
    }

}

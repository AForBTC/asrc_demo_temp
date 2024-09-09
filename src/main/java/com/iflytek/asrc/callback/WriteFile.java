package com.iflytek.asrc.callback;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.asrc.data.AsrcRequestCallback;
import com.iflytek.asrc.data.Lattice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@RestController
@RequestMapping("/asrc")
public class WriteFile {

    public static HashMap<String, List> r1 = new HashMap<>();

    @RequestMapping("/writefile")
    public String callback(@RequestBody AsrcRequestCallback asrcRequestCallback) throws IOException {

//        System.out.println("=========="+asrcRequestCallback);
//        System.out.println("==========================================================================");
//        System.out.println(JSON.toJSONString(asrcRequestCallback.getBody()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer aid_sb = new StringBuffer();
        aid_sb.append(asrcRequestCallback.getBody().getAid());
        String file = aid_sb.toString() + ".txt";
        String filePath = "/app/audio_text/" + file;
        ArrayList<Lattice> latticeList = new ArrayList<>();
        asrcRequestCallback.getBody().getLattices().forEach((lattices) -> {
            stringBuffer.append(lattices.getOnebest());
            JSONObject getJson_cn1best = JSON.parseObject(lattices.getJson_cn1best());
            Integer spk = lattices.getSpk();
            Lattice lattice = new Lattice();
            lattice.setRl(getJson_cn1best.getInteger("rl"));
            lattice.setSpk(spk);
            lattice.setText(lattices.getOnebest());
            latticeList.add(lattice);
        });
        r1.put(filePath, latticeList);
        // 将 aid 的值传给 aid_sb
        // 输出文件名为:  audio (aid的值).txt

//        System.out.println("全文结果："+stringBuffer);
//        log.info("全文结果：{}",stringBuffer);
//        System.out.println("lalala:" + stringBuffer);

        /**
         * stringBuffer.toString()为音频转写出的文字
         * 将文字输出到 回调地址参数中的文件中
         */
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(stringBuffer.toString().getBytes());
        fos.close();
        return stringBuffer.toString();
    }
}


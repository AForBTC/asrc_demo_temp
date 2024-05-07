package com.iflytek.asrc;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class demo {
    public static void main(String[] args) {
        // 定义文件夹路径
        String folderPath = "C:\\Users\\dihaichao\\Desktop\\济南淄博";

        // 调用方法遍历文件夹
        traverseFolder(folderPath);
    }

    public static void traverseFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，递归调用自身
                    traverseFolder(file.getAbsolutePath());
                } else {
                    // 如果是文件，执行接口调用
                    invokeAPI(file);
                }
            }
        }
    }

    public static void invokeAPI(File file) {
        try {
            // API 地址
            URL url = new URL("http://localhost:9099/speech/getSpeechText");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            // 设置请求体
            OutputStream outputStream = connection.getOutputStream();
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 发起请求
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 请求成功，处理返回结果
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 输出返回结果
                System.out.println("Response: " + response.toString());
            } else {
                // 请求失败，输出错误信息
                System.out.println("API request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

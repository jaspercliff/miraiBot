package com.jasper.miraiBot.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;

@Configuration
@ConfigurationProperties(prefix = "gpt")
public class GPTPlugin{
    private static final Logger logger = LoggerFactory.getLogger(GPTPlugin.class);
    private static  String trigger;
    private static String endPoint;
    private static String secret;



    @Bean
    public Listener<GroupMessageEvent> gptListener() {
        return GlobalEventChannel.INSTANCE.exceptionHandler(e -> {
            System.out.println("e = " + e);
        }).subscribeAlways(GroupMessageEvent.class, event -> {
            // 发送者qq
            long senderQQ = event.getSender().getId();
            String message = event.getMessage().contentToString();
            if (message.contains(trigger)) {
                String question = message.substring(message.indexOf(trigger) + 1);
                MessageChainBuilder reply = new MessageChainBuilder();

                CompletableFuture.runAsync(() -> {
                        String answer = generateResponse(question);
                        System.out.println(answer);
                        At atSender = new At(senderQQ);
                        reply.add(atSender);
                        reply.add(":");
                        reply.add(answer);
                        // 发送回复消息
                        event.getGroup().sendMessage(answer);
                });
            }
        });
    }

    public static String generateResponse(String prompt){
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1081))).
                build();

        // 构建请求体 JSON
        String jsonBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \""+prompt+"\" }]}";


        // 构建请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Request request = new Request.Builder()
                .url(endPoint)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + secret)
                .build();
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // 处理响应，这里简单地返回生成的文本
                return response.body().string();
            } else {
                // 处理错误情况
                return "Error: " + response.code() + " - " + response.message();
            }
        } catch (IOException e) {
            logger.error("An error occurred:"+e);
            return "Error: " + e.getMessage();
        }
    }

    public  String getTrigger() {
        return trigger;
    }

    public  void setTrigger(String trigger) {
        GPTPlugin.trigger = trigger;
    }

    public  String getEndPoint() {
        return endPoint;
    }

    public  void setEndPoint(String endPoint) {
        GPTPlugin.endPoint = endPoint;
    }

    public  String getSecret() {
        return secret;
    }

    public  void setSecret(String secret) {
        GPTPlugin.secret = secret;
    }

    public static String parseContent(String jsonResponse) {
        try {
            // 使用 Jackson 进行解析
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            // 提取 content 字段
            String content = jsonNode
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            return content;
        } catch (Exception e) {
            // 处理解析异常
            e.printStackTrace();
            return "Error parsing response.";
        }
    }
}

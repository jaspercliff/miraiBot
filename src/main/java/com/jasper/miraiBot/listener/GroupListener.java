package com.jasper.miraiBot.listener;

import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class GroupListener {
    @Bean
    public Listener<GroupMessageEvent>  groupMessageEventListener(){
        return GlobalEventChannel.INSTANCE.exceptionHandler(e->{
            System.out.println("e = " + e);
        }).subscribeAlways(GroupMessageEvent.class, event->{
            MessageChain messages = event.getMessage();
            System.out.println("messages = " + messages);
//            event.getSubject().sendMessage("hahaha");
        });
    }
}

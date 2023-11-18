package com.jasper.miraiBot.listener;

import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GroupListener {
    @Bean
    public Listener<GroupMessageEvent>  groupMessageEventListener(){
        return GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event->{
            MessageChain messages = event.getMessage();
            System.out.println("messages = " + messages);
//            event.getSubject().sendMessage("1ghhjknlkm;！");
        });
    }
}

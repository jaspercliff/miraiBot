package com.jasper.miraiBot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StartBot implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Bot bot = BotFactory.INSTANCE.newBot(196161605L, BotAuthorization.byQRCode(), configuration->{
            configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
            configuration.setWorkingDir(new File("D:\\java\\miraiQQBot\\workingDir"));
        });

//        创建 Bot 后不会自动登录，需要手动调用其 login() 方法。只需要调用一次 login() 即可，Bot 掉线时会自动重连
        bot.login();
//        主线程等待bot加入完成继续运行
        new Thread(bot::join).start();
    }
}
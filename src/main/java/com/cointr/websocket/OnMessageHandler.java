package com.cointr.websocket;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static org.thymeleaf.util.StringUtils.substring;

@Component
@RequiredArgsConstructor
public class OnMessageHandler {

    @Async
    @EventListener
    public void onMessage(OnMessageEvent event) {
        JsonObject jsonObject = new Gson().fromJson(event.getMessage(), JsonObject.class);
    }
}

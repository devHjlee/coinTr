package com.cointr.websocket;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.service.CoinService;
import com.google.gson.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.thymeleaf.util.StringUtils.substring;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketClient {
    private final CoinService coinService;
    private final OkHttpClient client = new OkHttpClient();
    private WebSocket ws = null;
    private enum WsStatus{
        START,STOP
    }
    WsStatus status = WsStatus.STOP;

    //todo : 으음.. 여러웹소켓은...?
    @PostConstruct
    public void connect() throws InterruptedException {
        if(status.equals(WsStatus.START)) {
            return;
        }
        status = WsStatus.START;
        //List<String> markets = coinCodeRepository.findMarket();
        String[] markets = new String[]{"KRW-BTC","KRW-NEO","KRW-STX","KRW-XLM","KRW-HUNT","KRW-MOC","KRW-FCT2","KRW-ETH"};
        //String[] markets = new String[]{"KRW-BTC"};
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();
        for (String market : markets) {
            codesObj.add(market);
        }
        root.add(new JsonObject());
        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
        type.addProperty("type", "ticker");
        type.add("codes", codesObj);
        root.add(type);

         Request request = new Request.Builder()
                .url("wss://api.upbit.com/websocket/v1")
                .addHeader("options", root.toString())
                 .build();
        log.info(root.toString());
        ws = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
                log.info("WebSocket Open!!!");
                webSocket.send(Objects.requireNonNull(webSocket.request().header("options")));
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                JsonObject jsonObject = new Gson().fromJson(bytes.string(StandardCharsets.UTF_8), JsonObject.class);
                jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                TradeInfoDto tradeInfoDto = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                        .create()
                        .fromJson(jsonObject, TradeInfoDto.class);

                coinService.insertTradeInfo(tradeInfoDto);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) { }

            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                log.info("Connection closed: " + reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, okhttp3.Response response) {
                // Error occurred
                status = WsStatus.STOP;
                log.info("Error: " + t.getMessage());
            }
        });

        client.dispatcher().executorService().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void disconnect() {
        status = WsStatus.STOP;
        ws.close(1000, "Goodbye!");
    }

//    static void coinSave(ByteString bytes, TickerRepository tickerRepository, TradeRepository tradeRepository) {
//        JsonObject jsonObject = new Gson().fromJson(bytes.string(StandardCharsets.UTF_8), JsonObject.class);
//
//        if(!"stream_type".equals(jsonObject.get("stream_type").getAsString())) {
//            if("ticker".equals(jsonObject.get("type").getAsString())) {
//                Ticker ticker = new GsonBuilder()
//                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                        .create()
//                        .fromJson(bytes.string(StandardCharsets.UTF_8), Ticker.class);
//                tickerRepository.save(ticker);
//            }else{
//                Trade trade = new GsonBuilder()
//                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                        .create()
//                        .fromJson(bytes.string(StandardCharsets.UTF_8), Trade.class);
//                tradeRepository.save(trade);
//            }
//        }
//
//    }

}


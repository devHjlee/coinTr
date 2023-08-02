package com.cointr.websocket;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayTradeInfoService;
import com.cointr.upbit.service.FifteenTradeInfoService;
import com.google.gson.*;
import com.neovisionaries.ws.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NvWebSocket {
    private final CoinService coinService;
    private final DayTradeInfoService dayTradeInfoService;
    private final FifteenTradeInfoService fifteenTradeInfoService;
    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
    private static final int TIMEOUT = 5000;
    private enum WsStatus{
        START,STOP
    }
    WsStatus status = WsStatus.STOP;
    WebSocket ws = null;

    @PostConstruct
    public void connect() throws IOException, WebSocketException {
        if(status.equals(WsStatus.START)) {
            return;
        }

        status = WsStatus.START;
        List<CoinDto> markets = coinService.selectCoin();
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();

        for (CoinDto market : markets) {
            codesObj.add(market.getMarket());
        }

        root.add(new JsonObject());
        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
        type.addProperty("type", "ticker");
        type.addProperty("isOnlySnapshot", false);
        type.addProperty("isOnlyRealtime", true);
        type.add("codes", codesObj);
        root.add(type);

        ws = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter() {

                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {

                        //todo 병렬처리 시간 체크
                        long startTime = System.currentTimeMillis();

                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                        TradeInfoDto tradeInfoDto = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, TradeInfoDto.class);
                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);

                        long endTime = System.currentTimeMillis();
                        long durationInMillis = endTime - startTime;
                        double durationInSeconds = (double) durationInMillis / 1000.0;

                        System.out.println("메소드 실행 시간: " + durationInSeconds + "초");
                    }
                    // A text message arrived from the server.
                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(message);
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
        ws.sendText(root.toString());
    }
}
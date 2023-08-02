package com.cointr.websocket;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.service.CoinService;
import com.google.gson.*;
import com.neovisionaries.ws.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NvWebSocket {
    private final CoinService coinService;
    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
    private static final int TIMEOUT = 5000;
    private enum WsStatus{
        START,STOP
    }
    WsStatus status = WsStatus.STOP;
    WebSocket ws = null;

    @PostConstruct
    public void connect() throws IOException, WebSocketException, IOException, WebSocketException {
        if(status.equals(WsStatus.START)) {
            return;
        }
        status = WsStatus.START;
        List<CoinDto> markets = coinService.selectCoins();
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();

        for (CoinDto market : markets) {
            codesObj.add(market.getMarket());
        }

        root.add(new JsonObject());
        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
        type.addProperty("type", "ticker");
        type.add("codes", codesObj);
        root.add(type);
        log.info(root.toString());
        System.out.println("Start get data from upbit");
        ws = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter() {

                    // binary message arrived from the server
                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                        String str = new String(binary);
                        System.out.println(str);
                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                        TradeInfoDto tradeInfoDto = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, TradeInfoDto.class);
                        coinService.updateTechnicalIndicator(tradeInfoDto);
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

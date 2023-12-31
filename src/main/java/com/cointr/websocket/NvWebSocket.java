package com.cointr.websocket;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayPriceInfoService;
import com.cointr.upbit.service.MinutePriceInfoService;
import com.google.gson.*;
import com.neovisionaries.ws.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NvWebSocket {
    private final CoinService coinService;
    private final DayPriceInfoService dayPriceInfoService;
    private final MinutePriceInfoService minutePriceInfoService;
    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
    private static final int TIMEOUT = 5000;
    WebSocket ws = null;

    public void connect() throws WebSocketException, IOException, InterruptedException {
        List<CoinDto> markets = coinService.findAllCoin();
        int coinCnt = (int)Math.ceil(markets.size()/10.0);
        for(int i = 1; i <= coinCnt;i++) {
            Thread.sleep(1000);
            int str = i-1;
            if(i == 1) {
                tradeConnect(markets.subList(0,i*10),i);
            }else if(i == coinCnt) {
                tradeConnect(markets.subList(str*10,markets.size()),i);
            }else {
                tradeConnect(markets.subList(str*10,str*10+10),i);
            }

        }
    }

    public void tradeConnect(List<CoinDto> coinDtoList,int socketNum) throws IOException, WebSocketException {
        log.info("Connect:"+socketNum);
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();

        for (CoinDto market : coinDtoList) {
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
                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                        PriceInfoDto priceInfoDtoDay = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, PriceInfoDto.class);
                        PriceInfoDto priceInfoDtoMinute = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, PriceInfoDto.class);

                        minutePriceInfoService.updateTechnicalIndicator(priceInfoDtoMinute,"60");
                        dayPriceInfoService.updateTechnicalIndicator(priceInfoDtoDay);
                    }

                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(message);
                    }

                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                               boolean closedByServer) throws WebSocketException, IOException {
                        tradeConnect(coinDtoList,socketNum);
                    }
                    public void onError(WebSocket websocket, WebSocketException cause) throws WebSocketException, IOException {
                        tradeConnect(coinDtoList,socketNum);
                        log.info("Error::"+cause.toString());
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
        ws.sendText(root.toString());
    }
}
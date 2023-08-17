package com.cointr.websocket;

import com.cointr.telegram.TelegramMessageProcessor;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.dto.VolumeInfoDto;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayTradeInfoService;
import com.cointr.upbit.service.FifteenTradeInfoService;
import com.google.gson.*;
import com.neovisionaries.ws.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NvWebSocket {
    private final CoinService coinService;
    private final DayTradeInfoService dayTradeInfoService;
    private final FifteenTradeInfoService fifteenTradeInfoService;
    private final TelegramMessageProcessor telegramMessageProcessor;
    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
    private static final int TIMEOUT = 5000;
    private enum WsStatus{
        START,STOP
    }
    WsStatus status = WsStatus.STOP;
    WebSocket ws = null;

    //@PostConstruct
    public void connect() throws WebSocketException, IOException, InterruptedException {
        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin();
//        int coinCnt = (int)Math.ceil(markets.size()/10.0);
//        for(int i = 1; i <= coinCnt;i++) {
//            Thread.sleep(1000);
//            int str = i-1;
//            if(i == 1) {
//                tradeConnect(markets.subList(0,i*10),i);
//                volumeConnect(markets.subList(0,i*10),i);
//            }else if(i == coinCnt) {
//                tradeConnect(markets.subList(str*10,markets.size()),i);
//                volumeConnect(markets.subList(str*10,markets.size()),i);
//            }else {
//                tradeConnect(markets.subList(str*10,str*10+10),i);
//                volumeConnect(markets.subList(str*10,str*10+10),i);
//            }
//
//        }

        //TEST
        CoinDto coin = new CoinDto();
        coin.setMarket("KRW-ATOM");
        List<CoinDto> testCoin = new ArrayList<>();
        testCoin.add(coin);
        tradeConnect(testCoin,0);
        volumeConnect(testCoin,0);
    }

    public void tradeConnect(List<CoinDto> coinDtoList,int socketNum) throws IOException, WebSocketException {
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
                        //log.info("Socket : "+socketNum+":"+socketNum+":"+socketNum+":"+socketNum+":"+socketNum+":"+socketNum);
                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                        TradeInfoDto tradeInfoDto = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, TradeInfoDto.class);
                        log.info(jsonObject.toString());
                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
                    }

                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(message);
                    }

                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                               boolean closedByServer) throws WebSocketException, IOException {
                        log.info("Disconnect");
                        StringBuilder message = new StringBuilder(socketNum + "번 연결이 끊어졌습니다.");
                        for(CoinDto coin : coinDtoList) {
                            message.append("|");
                            message.append(coin.getMarket());
                        }
                        telegramMessageProcessor.sendMessage("-1001813916001", message.toString());
                        tradeConnect(coinDtoList,socketNum);
                    }
                    public void onError(WebSocket websocket, WebSocketException cause) throws WebSocketException, IOException {
                        log.info("Error::"+cause.toString());
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
        ws.sendText(root.toString());
    }

    public void volumeConnect(List<CoinDto> coinDtoList,int socketNum) throws IOException, WebSocketException {
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();

        for (CoinDto market : coinDtoList) {
            codesObj.add(market.getMarket());
        }

        root.add(new JsonObject());
        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
        type.addProperty("type", "trade");
        type.addProperty("isOnlySnapshot", false);
        type.addProperty("isOnlyRealtime", true);
        type.add("codes", codesObj);
        root.add(type);

        ws = new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter() {

                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                        //log.info("Socket : "+socketNum+":"+socketNum+":"+socketNum+":"+socketNum+":"+socketNum+":"+socketNum);
                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
                        VolumeInfoDto volumeInfoDto = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                                .create()
                                .fromJson(jsonObject, VolumeInfoDto.class);
                        //log.info(jsonObject.toString());

                        fifteenTradeInfoService.updateTradeVolume(volumeInfoDto);
                    }

                    public void onTextMessage(WebSocket websocket, String message) {
                        System.out.println(message);
                    }

                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                               boolean closedByServer) throws WebSocketException, IOException {
                        log.info("volumeConnect Disconnect");
                        StringBuilder message = new StringBuilder(socketNum + "번 연결이 끊어졌습니다.");
                        for(CoinDto coin : coinDtoList) {
                            message.append("|");
                            message.append(coin.getMarket());
                        }
                        telegramMessageProcessor.sendMessage("-1001813916001", message.toString());
                        volumeConnect(coinDtoList,socketNum);
                    }
                    public void onError(WebSocket websocket, WebSocketException cause) throws WebSocketException, IOException {
                        log.info("Error::"+cause.toString());
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();
        ws.sendText(root.toString());
    }
}
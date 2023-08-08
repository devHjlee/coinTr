package com.cointr.websocket;

import com.cointr.telegram.service.TelegramBotService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NvWebSocket {
    private final CoinService coinService;
    private final DayTradeInfoService dayTradeInfoService;
    private final FifteenTradeInfoService fifteenTradeInfoService;
    private final TelegramBotService telegramBotService;
    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
    private static final int TIMEOUT = 5000;
    private enum WsStatus{
        START,STOP
    }
    WsStatus status = WsStatus.STOP;
    WebSocket ws = null;
    // 멤버 변수로 CompletableFuture를 선언
    //private CompletableFuture<Void> future;
    @PostConstruct
    public void connect() throws WebSocketException, IOException, InterruptedException {
        status = WsStatus.START;
        List<CoinDto> markets = coinService.findAllCoin();
        int coinCnt = (int)Math.ceil(markets.size()/10.0);
        for(int i = 1; i <= coinCnt;i++) {
            Thread.sleep(1000);
            int str = i-1;
            if(i == 1) {
                webSocketConnet(markets.subList(0,i*10),i);
            }else if(i == coinCnt) {
                webSocketConnet(markets.subList(str*10,markets.size()),i);
            }else {
                webSocketConnet(markets.subList(str*10,str*10+10),i);
            }

        }//0,10 ,10,20 ,20,30, 30,40, 40,50,50,60

        //TEST
//        CoinDto coin = new CoinDto();
//        coin.setMarket("KRW-ELF");
//        List<CoinDto> testCoin = new ArrayList<>();
//        testCoin.add(coin);
//        webSocketConnet(testCoin,0);
    }

    public void webSocketConnet(List<CoinDto> coinDtoList,int socketNum) throws IOException, WebSocketException {
        JsonArray root = new JsonArray();
        JsonObject type = new JsonObject();
        JsonArray codesObj = new JsonArray();

        for (CoinDto market : coinDtoList) {
            codesObj.add(market.getMarket());
        }

        root.add(new JsonObject());
        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
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
                        telegramBotService.sendMessage("6171495764", message.toString());
                        webSocketConnet(coinDtoList,socketNum);
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
//package com.cointr.websocket;
//
//import com.cointr.upbit.dto.CoinDto;
//import com.cointr.upbit.dto.TradeInfoDto;
//import com.cointr.upbit.service.CoinService;
//import com.cointr.upbit.service.DayTradeInfoService;
//import com.cointr.upbit.service.FifteenTradeInfoService;
//import com.google.gson.*;
//import com.neovisionaries.ws.client.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class NvWebSocket {
//    private final CoinService coinService;
//    private final DayTradeInfoService dayTradeInfoService;
//    private final FifteenTradeInfoService fifteenTradeInfoService;
//    private static final String SERVER = "wss://api.upbit.com/websocket/v1";
//    private static final int TIMEOUT = 5000;
//    private enum WsStatus{
//        START,STOP
//    }
//    WsStatus status = WsStatus.STOP;
//    WebSocket ws = null;
//    WebSocket ws2 = null;
//    WebSocket ws3 = null;
//    WebSocket ws4 = null;
//    WebSocket ws5 = null;
//
//    // 멤버 변수로 CompletableFuture를 선언
//    private CompletableFuture<Void> future;
//    @PostConstruct
//    public void connect() throws IOException, WebSocketException {
//
//
//        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin().subList(0,20);
//        JsonArray root = new JsonArray();
//        JsonObject type = new JsonObject();
//        JsonArray codesObj = new JsonArray();
//
//        //ORG
//        for (CoinDto market : markets) {
//            codesObj.add(market.getMarket());
//        }
//        //TEST
//        //codesObj.add("KRW-RFR");
//        root.add(new JsonObject());
//        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
//        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
//        type.add("codes", codesObj);
//        root.add(type);
//
//        ws = new WebSocketFactory()
//                .setConnectionTimeout(TIMEOUT)
//                .createSocket(SERVER)
//                .addListener(new WebSocketAdapter() {
//
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//
//                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
//                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
//                        TradeInfoDto tradeInfoDto = new GsonBuilder()
//                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                                .create()
//                                .fromJson(jsonObject, TradeInfoDto.class);
//
//                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                    }
//
//                    public void onTextMessage(WebSocket websocket, String message) {
//                        System.out.println(message);
//                    }
//                })
//                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
//                .connect();
//        ws.sendText(root.toString());
//    }
//    @PostConstruct
//    public void connect2() throws IOException, WebSocketException {
//
//
//        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin().subList(20,40);
//        JsonArray root = new JsonArray();
//        JsonObject type = new JsonObject();
//        JsonArray codesObj = new JsonArray();
//
//        //ORG
//        for (CoinDto market : markets) {
//            codesObj.add(market.getMarket());
//        }
//        //TEST
//        //codesObj.add("KRW-BTC");
//        root.add(new JsonObject());
//        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
//        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
//        type.add("codes", codesObj);
//        root.add(type);
//
//        ws2 = new WebSocketFactory()
//                .setConnectionTimeout(TIMEOUT)
//                .createSocket(SERVER)
//                .addListener(new WebSocketAdapter() {
//
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//
//                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
//                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
//                        TradeInfoDto tradeInfoDto = new GsonBuilder()
//                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                                .create()
//                                .fromJson(jsonObject, TradeInfoDto.class);
//
//                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                    }
//
//                    public void onTextMessage(WebSocket websocket, String message) {
//                        System.out.println(message);
//                    }
//                })
//                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
//                .connect();
//        ws2.sendText(root.toString());
//    }
//    @PostConstruct
//    public void connect3() throws IOException, WebSocketException {
//
//        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin().subList(40,60);
//        JsonArray root = new JsonArray();
//        JsonObject type = new JsonObject();
//        JsonArray codesObj = new JsonArray();
//
//        //ORG
//        for (CoinDto market : markets) {
//            codesObj.add(market.getMarket());
//        }
//        //TEST
//        //codesObj.add("KRW-BTG");
//        root.add(new JsonObject());
//        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
//        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
//        type.add("codes", codesObj);
//        root.add(type);
//
//        ws3 = new WebSocketFactory()
//                .setConnectionTimeout(TIMEOUT)
//                .createSocket(SERVER)
//                .addListener(new WebSocketAdapter() {
//
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//
//                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
//                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
//                        TradeInfoDto tradeInfoDto = new GsonBuilder()
//                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                                .create()
//                                .fromJson(jsonObject, TradeInfoDto.class);
//
//                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                    }
//
//                    public void onTextMessage(WebSocket websocket, String message) {
//                        System.out.println(message);
//                    }
//                })
//                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
//                .connect();
//        ws3.sendText(root.toString());
//    }
//    @PostConstruct
//    public void connect4() throws IOException, WebSocketException {
//
//        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin().subList(60,80);
//        JsonArray root = new JsonArray();
//        JsonObject type = new JsonObject();
//        JsonArray codesObj = new JsonArray();
//
//        //ORG
//        for (CoinDto market : markets) {
//            codesObj.add(market.getMarket());
//        }
//        //TEST
//        //codesObj.add("KRW-BTG");
//        root.add(new JsonObject());
//        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
//        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
//        type.add("codes", codesObj);
//        root.add(type);
//
//        ws4 = new WebSocketFactory()
//                .setConnectionTimeout(TIMEOUT)
//                .createSocket(SERVER)
//                .addListener(new WebSocketAdapter() {
//
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//
//                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
//                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
//                        TradeInfoDto tradeInfoDto = new GsonBuilder()
//                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                                .create()
//                                .fromJson(jsonObject, TradeInfoDto.class);
//
//                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                    }
//
//                    public void onTextMessage(WebSocket websocket, String message) {
//                        System.out.println(message);
//                    }
//                })
//                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
//                .connect();
//        ws4.sendText(root.toString());
//    }
//    @PostConstruct
//    public void connect5() throws IOException, WebSocketException {
//
//        status = WsStatus.START;
//        List<CoinDto> markets = coinService.findAllCoin().subList(80,117);
//        JsonArray root = new JsonArray();
//        JsonObject type = new JsonObject();
//        JsonArray codesObj = new JsonArray();
//
//        //ORG
//        for (CoinDto market : markets) {
//            codesObj.add(market.getMarket());
//        }
//        //TEST
//        //codesObj.add("KRW-BTG");
//        root.add(new JsonObject());
//        root.get(0).getAsJsonObject().addProperty("ticket", UUID.randomUUID().toString());
//        type.addProperty("type", "ticker");
//        type.addProperty("isOnlySnapshot", false);
//        type.addProperty("isOnlyRealtime", true);
//        type.add("codes", codesObj);
//        root.add(type);
//
//        ws5 = new WebSocketFactory()
//                .setConnectionTimeout(TIMEOUT)
//                .createSocket(SERVER)
//                .addListener(new WebSocketAdapter() {
//
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//
//                        JsonObject jsonObject = new Gson().fromJson(new String(binary), JsonObject.class);
//                        jsonObject.addProperty("market",jsonObject.get("code").getAsString());
//                        TradeInfoDto tradeInfoDto = new GsonBuilder()
//                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
//                                .create()
//                                .fromJson(jsonObject, TradeInfoDto.class);
//
//                        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                        fifteenTradeInfoService.updateTechnicalIndicator(tradeInfoDto);
//                    }
//
//                    public void onTextMessage(WebSocket websocket, String message) {
//                        System.out.println(message);
//                    }
//                })
//                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
//                .connect();
//        ws5.sendText(root.toString());
//    }
//
//}
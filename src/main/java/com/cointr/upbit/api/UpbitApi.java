package com.cointr.upbit.api;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.indicators.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class UpbitApi {

    public List<CoinDto> getCoinList() {
        RestTemplate restTemplate = new RestTemplate();
        Type listType = new TypeToken<ArrayList<CoinDto>>(){}.getType();
        String url = "https://api.upbit.com/v1/market/all";

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        JsonArray jsonArray = new GsonBuilder().create().fromJson(responseEntity.getBody(),JsonArray.class);

        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                .create()
                .fromJson(jsonArray, listType);

    }

    /**
     * UpbitApi 를 통해 코인에 대한 캔들 정보 수신
     * @param market
     * @return List<TradeInfoDto>
     */
    public List<TradeInfoDto> getCandle(String market, String type,int candleTime) {
        RestTemplate restTemplate = new RestTemplate();
        Type listType = new TypeToken<ArrayList<TradeInfoDto>>(){}.getType();
        String url = "";

        if("day".equals(type)) {
            url = "https://api.upbit.com/v1/candles/days?market="+market+"&count=200";
        }else if ("minutes".equals(type)) {
            url = "https://api.upbit.com/v1/candles/minutes/"+candleTime+"?market="+market+"&count=200";
        }

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        JsonArray jsonArray = new GsonBuilder().create().fromJson(responseEntity.getBody(),JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if("day".equals(type)) {
                jsonObject.addProperty("trade_date", jsonObject.get("candle_date_time_utc").getAsString().replaceAll("-", "").substring(0, 8));
            }else if ("minutes".equals(type)) {
                jsonObject.addProperty("trade_date", jsonObject.get("candle_date_time_utc").getAsString().replaceAll("[^0-9]", "").substring(0, 12));
            }
            jsonObject.addProperty("acc_trade_price",jsonObject.get("candle_acc_trade_price").getAsString());
            jsonObject.addProperty("acc_trade_volume",jsonObject.get("candle_acc_trade_volume").getAsString());
        });

        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                .create()
                .fromJson(jsonArray, listType);
    }

    /**
     * CommodityChannelIndex 계산
     * @param tradeInfoDtoList
     */
    public void getCCI(List<TradeInfoDto> tradeInfoDtoList) {
        CommodityChannelIndex cci = new CommodityChannelIndex();
        try {
            cci.calculate(tradeInfoDtoList,20);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 볼린저밴드 계산
     * @param tradeInfoDtoList
     */
    public void getBollingerBand(List<TradeInfoDto> tradeInfoDtoList) {
        BollingerBand bollingerBand= new BollingerBand();
        bollingerBand.calculate(tradeInfoDtoList,20,2);

    }
    /**
     * RSI 계산
     * @param tradeInfoDtoList
     * @return double
     */
    public void getRSI(List<TradeInfoDto> tradeInfoDtoList){
        RelativeStrengthIndex relativeStrengthIndex = new RelativeStrengthIndex();

        try {
            relativeStrengthIndex.calculate(tradeInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * MACD 계산
     * @param tradeInfoDtoList
     */
    public void getMACD(List<TradeInfoDto> tradeInfoDtoList) {
        MovingAverageConvergenceDivergence macd = new MovingAverageConvergenceDivergence();
        try {
            macd.calculate(tradeInfoDtoList,12,26,9);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ADX 계산
     * @param tradeInfoDtoList
     */
    public void getADX(List<TradeInfoDto> tradeInfoDtoList) {
        AverageDirectionalIndex adx = new AverageDirectionalIndex();
        try {
            adx.calculate(tradeInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ParabolicSar 계산
     * @param tradeInfoDtoList
     */
    public void getPSar(List<TradeInfoDto> tradeInfoDtoList) {
        ParabolicSar pSar = new ParabolicSar();
        try {
            pSar.calculate(tradeInfoDtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Aroon 계산
     * @param tradeInfoDtoList
     */
    public void getAroon(List<TradeInfoDto> tradeInfoDtoList) {
        Aroon aroon = new Aroon();
        try {
            aroon.calculateAroonOscillator(tradeInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stochastic 계산
     * @param tradeInfoDtoList
     */
    public void getStochastics(List<TradeInfoDto> tradeInfoDtoList) {
        StochasticsOscilator stochasticsOscilator = new StochasticsOscilator();
        int n = 5; // Fast %K를 계산하는 데 사용되는 기간
        int m = 3; // Slow %K를 계산하는 데 사용되는 기간
        int t = 3; // Slow %D를 계산하는 데 사용되는 기간
        for (int i = 0; i < tradeInfoDtoList.size(); i++) {
            double fastK = stochasticsOscilator.getStochasticFastK(tradeInfoDtoList, i, n);
            double fastD = stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m);
            double slowK = stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m);
            double slowD = stochasticsOscilator.getStochasticSlowD(tradeInfoDtoList, i, t);
            tradeInfoDtoList.get(i).setFastK(fastK);
            tradeInfoDtoList.get(i).setFastD(fastD);
            tradeInfoDtoList.get(i).setSlowK(slowK);
            tradeInfoDtoList.get(i).setSlowD(slowD);
        }
    }
}
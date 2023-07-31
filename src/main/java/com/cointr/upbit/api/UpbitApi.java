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
    public List<TradeInfoDto> getCandle(String market) {
        RestTemplate restTemplate = new RestTemplate();
        Type listType = new TypeToken<ArrayList<TradeInfoDto>>(){}.getType();
        String url = "https://api.upbit.com/v1/candles/days?market="+market+"&count=200";

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        JsonArray jsonArray = new GsonBuilder().create().fromJson(responseEntity.getBody(),JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            jsonObject.addProperty("trade_date",jsonObject.get("candle_date_time_utc").getAsString().replaceAll("-", "").substring(0, 8));
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
}
package com.cointr.upbit.util;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UpbitApi {

    public List<CoinDto> coinSaveAll() {
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
            jsonObject.addProperty("acc_trade_volume",jsonObject.get("candle_acc_trade_volume").getAsString());
        });

        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                .create()
                .fromJson(jsonArray, listType);
    }

    /**
     * 코인에 대한 RSI 계산
     * @param tradeInfoDtos
     * @return double
     */
    public void getRis(List<TradeInfoDto> tradeInfoDtos){
        RelativeStrengthIndex relativeStrengthIndex = new RelativeStrengthIndex();

        try {
            relativeStrengthIndex.calculate(tradeInfoDtos,14);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(relativeStrengthIndex.toString());

    }

    /**
     * 코인에 대한 MACD 계산
     * @param tradeInfoDtos
     */
    public void getMACD(List<TradeInfoDto> tradeInfoDtos) {
        MovingAverageConvergenceDivergence macd = new MovingAverageConvergenceDivergence();
        try {
            macd.calculate(tradeInfoDtos,12,26,9);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
package com.cointr.upbit.util;

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
        log.info(jsonArray.toString());
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
    public double getRis(List<TradeInfoDto> tradeInfoDtos){
        double zero = 0;
        double rsi = 0;
        List<Double> upList = new ArrayList<>();  // 상승 리스트
        List<Double> downList = new ArrayList<>();  // 하락 리스트
        for (int i = 0; i < tradeInfoDtos.size() - 1; i++) {
            // 최근 종가 - 전일 종가 = gap 값이 양수일 경우 상승했다는 뜻 / 음수일 경우 하락이라는 뜻
            double gapByTradePrice = tradeInfoDtos.get(i + 1).getTradePrice() - tradeInfoDtos.get(i).getTradePrice();
            if (gapByTradePrice > 0) {  // 종가가 전일 종가보다 상승일 경우
                upList.add(gapByTradePrice);
                downList.add(zero);
            } else if (gapByTradePrice < 0) {  // 종가가 전일 종가보다 하락일 경우
                downList.add(gapByTradePrice * -1);  // 음수를 양수로 변환해준다.
                upList.add(zero);
            } else {  // 상승, 하락이 없을 경우 종가 - 전일 종가 = gap은 0이므로 0값을 넣어줍니다.
                upList.add(zero);
                downList.add(zero);
            }
        }

        double day = 14;  // 가중치를 위한 기준 일자 (보통 14일 기준)
        double a = (double) 1 / (1 + (day - 1));  // 지수 이동 평균의 정식 공식은 a = 2 / 1 + day 이지만 업비트에서 사용하는 수식은 a = 1 / (1 + (day - 1))

        // rsi 계산
        double au = CollectionUtils.isEmpty(upList)?0:getRsiExpMoveAvg(upList,a);
        double ad = CollectionUtils.isEmpty(downList)?0:getRsiExpMoveAvg(downList,a);
        double rs = au / ad;
        rsi = 100 - (100 / (1 + rs));
        log.info("RSI:"+rsi);
        return rsi;
    }

    private double getRsiExpMoveAvg(List<Double> indexList, double a) {
        double ema = 0;  // 하락 값의 지수이동평균
        ema = indexList.get(0);
        if (indexList.size() > 1) {
            for (int i = 1; i < indexList.size(); i++) {
                ema = (indexList.get(i) * a) + (ema * (1 - a));
            }
        }

        return ema;
    }

    /**
     * 코인에 대한 MACD 계산
     * @param tradeInfoDtos
     */
    public void getMACD(List<TradeInfoDto> tradeInfoDtos) {
        MovingAverageConvergenceDivergence macd = new MovingAverageConvergenceDivergence();
        tradeInfoDtos.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        try {
            macd.calculate(tradeInfoDtos,12,26,9);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
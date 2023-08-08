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
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
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
    public void calculateIndicators(List<TradeInfoDto> tradeInfoDtoList) {
        try {
            tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
            getMACD(tradeInfoDtoList);
            getRSI(tradeInfoDtoList);
            getCCI(tradeInfoDtoList);
            getBollingerBand(tradeInfoDtoList);
            getADX(tradeInfoDtoList);
            getPSar(tradeInfoDtoList);
            getAroon(tradeInfoDtoList);
            getStochastics(tradeInfoDtoList);
            tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        }catch (Exception e) {
            log.info("calculateIndicators Exception :"+e.getMessage());
        }
    }

    /**
     * CommodityChannelIndex 계산
     * @param tradeInfoDtoList
     */
    private void getCCI(List<TradeInfoDto> tradeInfoDtoList) {
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
    private void getBollingerBand(List<TradeInfoDto> tradeInfoDtoList) {
        BollingerBand bollingerBand= new BollingerBand();
        bollingerBand.calculate(tradeInfoDtoList,20,2);

    }
    /**
     * RSI 계산
     * @param tradeInfoDtoList
     * @return double
     */
    private void getRSI(List<TradeInfoDto> tradeInfoDtoList){
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
    private void getMACD(List<TradeInfoDto> tradeInfoDtoList) {
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
    private void getADX(List<TradeInfoDto> tradeInfoDtoList) {
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
    private void getPSar(List<TradeInfoDto> tradeInfoDtoList) {
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
    private void getAroon(List<TradeInfoDto> tradeInfoDtoList) {
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
    private void getStochastics(List<TradeInfoDto> tradeInfoDtoList) {
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        StochasticsOscilator stochasticsOscilator = new StochasticsOscilator();
        int n = 5; // Fast %K를 계산하는 데 사용되는 기간
        int m = 3; // Slow %K를 계산하는 데 사용되는 기간
        int t = 3; // Slow %D를 계산하는 데 사용되는 기간
        for (int i = 0; i < tradeInfoDtoList.size(); i++) {
            double fastK = (Double.isNaN(stochasticsOscilator.getStochasticFastK(tradeInfoDtoList, i, n))) ? 0.0 : stochasticsOscilator.getStochasticFastK(tradeInfoDtoList, i, n);
            double fastD = (Double.isNaN(stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m))) ? 0.0 : stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m);
            double slowK = (Double.isNaN(stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m))) ? 0.0 : stochasticsOscilator.getStochasticSlowK(tradeInfoDtoList, i, m);
            double slowD = (Double.isNaN(stochasticsOscilator.getStochasticSlowD(tradeInfoDtoList, i, t))) ? 0.0 : stochasticsOscilator.getStochasticSlowD(tradeInfoDtoList, i, t);
            tradeInfoDtoList.get(i).setFastK(fastK);
            tradeInfoDtoList.get(i).setFastD(fastD);
            tradeInfoDtoList.get(i).setSlowK(slowK);
            tradeInfoDtoList.get(i).setSlowD(slowD);
        }
    }

    public boolean evaluateCondition(String condition, TradeInfoDto data) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(data);
        Expression expression = parser.parseExpression(condition);
        return expression.getValue(context, Boolean.class);
    }
}
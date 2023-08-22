package com.cointr.upbit.api;

import com.cointr.telegram.TelegramMessageProcessor;
import com.cointr.upbit.dto.*;
import com.cointr.upbit.indicators.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UpbitApi {
    private final TelegramMessageProcessor telegramMessageProcessor;

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
    public List<PriceInfoDto> getCandle(String market, String type, String candleTime) {
        RestTemplate restTemplate = new RestTemplate();
        Type listType = new TypeToken<ArrayList<PriceInfoDto>>(){}.getType();
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

    public void calculateIndicators(List<PriceInfoDto> priceInfoDtoList) {
        try {
            priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate));
            getMACD(priceInfoDtoList);
            getRSI(priceInfoDtoList);
            getCCI(priceInfoDtoList);
            getBollingerBand(priceInfoDtoList);
            getADX(priceInfoDtoList);
            getPSar(priceInfoDtoList);
            getAroon(priceInfoDtoList);
            getStochastics(priceInfoDtoList);
            getSMA(priceInfoDtoList);
            priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());
        }catch (Exception e) {
            log.info("calculateIndicators Exception :"+e.getMessage());
        }
    }

    /**
     * CommodityChannelIndex 계산
     * @param priceInfoDtoList
     */
    private void getCCI(List<PriceInfoDto> priceInfoDtoList) {
        CommodityChannelIndex cci = new CommodityChannelIndex();
        try {
            cci.calculate(priceInfoDtoList,20);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 볼린저밴드 계산
     * @param priceInfoDtoList
     */
    private void getBollingerBand(List<PriceInfoDto> priceInfoDtoList) {
        BollingerBand bollingerBand= new BollingerBand();
        bollingerBand.calculate(priceInfoDtoList,20,2);

    }
    /**
     * RSI 계산
     * @param priceInfoDtoList
     * @return double
     */
    private void getRSI(List<PriceInfoDto> priceInfoDtoList){
        RelativeStrengthIndex relativeStrengthIndex = new RelativeStrengthIndex();

        try {
            relativeStrengthIndex.calculate(priceInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * MACD 계산
     * @param priceInfoDtoList
     */
    private void getMACD(List<PriceInfoDto> priceInfoDtoList) {
        MovingAverageConvergenceDivergence macd = new MovingAverageConvergenceDivergence();
        try {
            macd.calculate(priceInfoDtoList,12,26,9);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ADX 계산
     * @param priceInfoDtoList
     */
    private void getADX(List<PriceInfoDto> priceInfoDtoList) {
        AverageDirectionalIndex adx = new AverageDirectionalIndex();
        try {
            adx.calculate(priceInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ParabolicSar 계산
     * @param priceInfoDtoList
     */
    private void getPSar(List<PriceInfoDto> priceInfoDtoList) {
        ParabolicSar pSar = new ParabolicSar();
        try {
            pSar.calculate(priceInfoDtoList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Aroon 계산
     * @param priceInfoDtoList
     */
    private void getAroon(List<PriceInfoDto> priceInfoDtoList) {
        Aroon aroon = new Aroon();
        try {
            aroon.calculateAroonOscillator(priceInfoDtoList,14);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stochastic 계산
     * @param priceInfoDtoList
     */
    private void getStochastics(List<PriceInfoDto> priceInfoDtoList) {
        //priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate)); //0822 lhj
        StochasticsOscilator stochasticsOscilator = new StochasticsOscilator();
        int n = 5; // Fast %K를 계산하는 데 사용되는 기간
        int m = 3; // Slow %K를 계산하는 데 사용되는 기간
        int t = 3; // Slow %D를 계산하는 데 사용되는 기간
        for (int i = 0; i < priceInfoDtoList.size(); i++) {
            double fastK = (Double.isNaN(stochasticsOscilator.getStochasticFastK(priceInfoDtoList, i, n))) ? 0.0 : stochasticsOscilator.getStochasticFastK(priceInfoDtoList, i, n);
            double fastD = (Double.isNaN(stochasticsOscilator.getStochasticSlowK(priceInfoDtoList, i, m))) ? 0.0 : stochasticsOscilator.getStochasticSlowK(priceInfoDtoList, i, m);
            double slowK = (Double.isNaN(stochasticsOscilator.getStochasticSlowK(priceInfoDtoList, i, m))) ? 0.0 : stochasticsOscilator.getStochasticSlowK(priceInfoDtoList, i, m);
            double slowD = (Double.isNaN(stochasticsOscilator.getStochasticSlowD(priceInfoDtoList, i, t))) ? 0.0 : stochasticsOscilator.getStochasticSlowD(priceInfoDtoList, i, t);
            priceInfoDtoList.get(i).setFastK(fastK);
            priceInfoDtoList.get(i).setFastD(fastD);
            priceInfoDtoList.get(i).setSlowK(slowK);
            priceInfoDtoList.get(i).setSlowD(slowD);
        }
    }

    private void getSMA(List<PriceInfoDto> priceInfoDtoList) {
        double[] prices = priceInfoDtoList.stream()
                .mapToDouble(PriceInfoDto::getTradePrice)
                .toArray();
        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage();
        double[] sma5 = new double[priceInfoDtoList.size()];
        double[] sma60 = new double[priceInfoDtoList.size()];
        try {
            sma5 = simpleMovingAverage.calculate(prices,5).getSMA();
            sma60 = simpleMovingAverage.calculate(prices,60).getSMA();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < priceInfoDtoList.size(); i++) {
            PriceInfoDto priceInfoDto = priceInfoDtoList.get(i);
            priceInfoDto.setSma5(sma5[i]);
            priceInfoDto.setSma60(sma60[i]);
        }

    }

    //CCI 가 내리는 추세면 사지말자 && 100넘기면 금지 &&
    //
    public boolean myCondition(List<PriceInfoDto> priceInfoDtoList) {
        //log.info("myCondition");
        return (priceInfoDtoList.get(2).getMacdSignal() > priceInfoDtoList.get(2).getMacd())
                && (priceInfoDtoList.get(1).getMacdSignal() > priceInfoDtoList.get(1).getMacd())
                && (priceInfoDtoList.get(0).getMacdSignal() <= priceInfoDtoList.get(0).getMacd())

                && priceInfoDtoList.get(0).getRsi() < 70

                && priceInfoDtoList.get(0).getBbAvg() <= priceInfoDtoList.get(0).getTradePrice()

                && ((priceInfoDtoList.get(0).getAccBidVolume() / priceInfoDtoList.get(0).getAccAskVolume()) * 100 >= 60)

                && (priceInfoDtoList.get(0).getAccTradePrice() > 3000000000.0)

                && !("Y").equals(priceInfoDtoList.get(0).getTypeA());
    }

    public void evaluateCondition(List<ConditionDto> conditionDtoList, PriceInfoDto data, String candleType) {
        if (conditionDtoList.size() > 0) {

            SpelExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext(data);
            for (ConditionDto conditionDto : conditionDtoList) {
                if (conditionDto.getCandleType().equals(candleType)) {
                    Expression expression = parser.parseExpression(conditionDto.getCondition());
                    StringBuilder message = new StringBuilder();
                    if (Boolean.TRUE.equals(expression.getValue(context, Boolean.class))) {
                        if ("A".equals(conditionDto.getConditionType()) && !data.getTypeA().equals("Y")) {
                            data.setTypeA("Y");
                            message.append("Coin :").append(data.getMarket()).append("\n");
                            message.append("알림조건 :").append(conditionDto.getCondition()).append("\n");
                            message.append("-정보-").append("\n");
                            message.append("RSI :").append(data.getRsi()).append("\n");
                            message.append("MACD :").append(data.getMacd()).append("\n");
                            message.append("ADX :").append(data.getAdx()).append("\n");
                            message.append("CCI :").append(data.getCci()).append("\n");


                            telegramMessageProcessor.sendMessage("-1001813916001", String.valueOf(message));
                        } else if ("B".equals(conditionDto.getConditionType()) && !data.getTypeB().equals("Y")) {
                            data.setTypeB("Y");
                            message.append("Coin :").append(data.getMarket()).append("\n");
                            message.append("알림조건 :").append(conditionDto.getCondition()).append("\n");
                            message.append("-정보-").append("\n");
                            message.append("RSI :").append(data.getRsi()).append("\n");
                            message.append("MACD :").append(data.getMacd()).append("\n");
                            message.append("ADX :").append(data.getAdx()).append("\n");
                            message.append("CCI :").append(data.getCci()).append("\n");

                            telegramMessageProcessor.sendMessage("-1001813916001", String.valueOf(message));
                        } else if ("C".equals(conditionDto.getConditionType()) && !data.getTypeC().equals("Y")) {
                            data.setTypeC("Y");
                            message.append("Coin :").append(data.getMarket()).append("\n");
                            message.append("알림조건 :").append(conditionDto.getCondition()).append("\n");
                            message.append("-정보-").append("\n");
                            message.append("RSI :").append(data.getRsi()).append("\n");
                            message.append("MACD :").append(data.getMacd()).append("\n");
                            message.append("ADX :").append(data.getAdx()).append("\n");
                            message.append("CCI :").append(data.getCci()).append("\n");

                            telegramMessageProcessor.sendMessage("-1001813916001", String.valueOf(message));
                        }
                    }
                }
            }
        }
    }


    public void evaluateConditionPrice(VolConditionDto volConditionDto, VolumeInfoDto volumeInfoDto) {
        if (volConditionDto != null && (volumeInfoDto.getAskPrice()+volumeInfoDto.getBidPrice() > volConditionDto.getConditionPrice()) && !("Y").equals(volumeInfoDto.getAlarmYn())) {
            volumeInfoDto.setAlarmYn("Y");
            String message = "Coin :" + volumeInfoDto.getMarket() + "\n" +
                    "매수금액:" + volumeInfoDto.getBidPrice() + " | 매도금액 :" + volumeInfoDto.getAskPrice() +
                    "\n 총 거래금액 :" + (volumeInfoDto.getAskPrice() + volumeInfoDto.getBidPrice());
            telegramMessageProcessor.sendMessage("-1001813916001", message);
        }
    }
}
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
            System.out.println(jsonObject.get("trade_price").getAsString());
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
        double au = CollectionUtils.isEmpty(upList)?0:getExpMoveAvg(upList,a);
        double ad = CollectionUtils.isEmpty(downList)?0:getExpMoveAvg(downList,a);
        double rs = au / ad;
        rsi = 100 - (100 / (1 + rs));
        log.info("RSI:"+rsi);
        return rsi;
    }

    private double getExpMoveAvg(List<Double> indexList, double a) {
        double ema = 0;  // 하락 값의 지수이동평균
        if (!CollectionUtils.isEmpty(indexList)) {
            ema = indexList.get(0);
            if (indexList.size() > 1) {
                for (int i = 1; i < indexList.size(); i++) {
                    ema = (indexList.get(i) * a) + (ema * (1 - a));
                }
            }
        }
        return ema;
    }

    public void getMACD(List<TradeInfoDto> tradeInfoDtos) {
//        List<Double> prices = List.of(
//                100.0, 101.0, 105.0, 102.0, 110.0, 112.0, 115.0, 120.0, 122.0, 118.0,
//                125.0, 130.0, 135.0, 140.0, 142.0, 138.0, 145.0, 147.0, 150.0, 155.0
//        );
        //MA12 = (종가1+~+12) / 12
        //MA26 = (종가1+~+26) / 26
        //MACD 라인 = MA12-MA26
        //MACD 시그널라인 = 9일이동편균선 = MA9 = (종가1+~+9)/9
        List<Double> prices = tradeInfoDtos.stream().map(TradeInfoDto::getTradePrice)
                                            .collect(Collectors.toList());
        int shortTerm = 12;
        int longTerm = 26;
        int signalPeriod = 9;

        // MACD 계산
        List<Double> macdResult = calculateMACD(prices, shortTerm, longTerm, signalPeriod);
        System.out.println("MACD: " + macdResult.get(0));
        System.out.println("Signal Line: " + macdResult.get(1));
        System.out.println("MACD Histogram: " + macdResult.get(2));

    }


    public static List<Double> calculateMACD(List<Double> prices, int shortTerm, int longTerm, int signalPeriod) {
        List<Double> macdResult = new ArrayList<>();

        // 단기 지수 이동 평균 계산
        List<Double> shortEMA = calculateEWMA(prices, shortTerm);

        // 장기 지수 이동 평균 계산
        List<Double> longEMA = calculateEWMA(prices, longTerm);

        // MACD Line 계산
        List<Double> macdLine = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            macdLine.add(shortEMA.get(i) - longEMA.get(i));
        }

        // Signal Line 계산
        List<Double> signalLine = calculateEWMA(macdLine, signalPeriod);

        // MACD Histogram 계산
        List<Double> macdHistogram = new ArrayList<>();
        for (int i = 0; i < macdLine.size(); i++) {
             macdHistogram.add(macdLine.get(i) - signalLine.get(i));
        }

        // MACD Line, Signal Line, MACD Histogram을 결과 리스트에 추가
        macdResult.add(macdLine.get(macdLine.size() - 1));
        macdResult.add(signalLine.get(signalLine.size() - 1));
        macdResult.add(macdHistogram.get(macdHistogram.size() - 1));

        return macdResult;
    }

    public static List<Double> calculateEWMA(List<Double> prices, int period) {
        List<Double> ema = new ArrayList<>();
        double smoothing = 2.0 / (period + 1);
        double currentEma = prices.get(0);

        for (int i = 1; i < period+1; i++) {
            double currentPrice = prices.get(i);
            currentEma = currentPrice * smoothing + currentEma * (1 - smoothing);
            ema.add(currentEma);
        }

        return ema;
    }
//    private double[] calculateMACD(List<Double> prices) {
//        int shortTerm = 12;
//        int longTerm = 26;
//        int signalPeriod = 9;
//
//        double[] macdResult = new double[3];
//
//        // 단기 지수 이동 평균 계산
//        double shortEMA = calculateEMA(prices, shortTerm);
//
//        // 장기 지수 이동 평균 계산
//        double longEMA = calculateEMA(prices, longTerm);
//
//        // MACD Line 계산
//        double macdLine = shortEMA - longEMA;
//        macdResult[0] = macdLine;
//
//        // 시그널 라인 계산
//        double signalLine = calculateEMA(prices.subList(longTerm - shortTerm, prices.size()), signalPeriod);
//        macdResult[1] = signalLine;
//
//        // MACD Histogram 계산
//        double histogram = macdLine - signalLine;
//        macdResult[2] = histogram;
//
//        return macdResult;
//    }
//
//    private double calculateEMA(List<Double> prices, int period) {
//        double smoothing = 2.0 / (period + 1);
//        double ema = prices.get(0);
//
//        for (int i = 1; i < period; i++) {
//            ema = prices.get(i) * smoothing + ema * (1 - smoothing);
//        }
//
//        return ema;
//    }
}
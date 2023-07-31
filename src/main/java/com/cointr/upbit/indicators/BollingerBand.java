package com.cointr.upbit.indicators;

import com.cointr.upbit.dto.TradeInfoDto;

import java.util.List;

public class BollingerBand {

    public void calculate(List<TradeInfoDto> tradeInfoDtoList, int windowSize, double standardDeviations) {
        double[] prices = tradeInfoDtoList.stream().mapToDouble(TradeInfoDto::getTradePrice).toArray();

        // 볼린저 밴드 계산
        double[] movingAverages = calculateMovingAverages(prices, windowSize);
        double[] upperBands = calculateUpperBands(movingAverages, prices, windowSize, standardDeviations);
        double[] lowerBands = calculateLowerBands(movingAverages, prices, windowSize, standardDeviations);

        for(int i = 0; i < tradeInfoDtoList.size(); i++) {
            tradeInfoDtoList.get(i).setBbAvg(movingAverages[i]);
            tradeInfoDtoList.get(i).setBbUp(upperBands[i]);
            tradeInfoDtoList.get(i).setBbDown(lowerBands[i]);
        }
    }

    // 이동평균선 계산 함수
    private static double[] calculateMovingAverages(double[] prices, int windowSize) {
        double[] movingAverages = new double[prices.length];
        for (int i = windowSize - 1; i < prices.length; i++) {
            double sum = 0;
            for (int j = i; j > i - windowSize; j--) {
                sum += prices[j];
            }
            movingAverages[i] = sum / windowSize;
        }
        return movingAverages;
    }

    // 상단 볼린저 밴드 계산 함수
    private static double[] calculateUpperBands(double[] movingAverages, double[] prices, int windowSize, double standardDeviations) {
        double[] upperBands = new double[prices.length];
        for (int i = windowSize - 1; i < prices.length; i++) {
            double sum = 0;
            for (int j = i; j > i - windowSize; j--) {
                sum += Math.pow(prices[j] - movingAverages[i], 2);
            }
            double standardDeviation = Math.sqrt(sum / windowSize);
            upperBands[i] = movingAverages[i] + (standardDeviations * standardDeviation);
        }
        return upperBands;
    }

    // 하단 볼린저 밴드 계산 함수
    private static double[] calculateLowerBands(double[] movingAverages, double[] prices, int windowSize, double standardDeviations) {
        double[] lowerBands = new double[prices.length];
        for (int i = windowSize - 1; i < prices.length; i++) {
            double sum = 0;
            for (int j = i; j > i - windowSize; j--) {
                sum += Math.pow(prices[j] - movingAverages[i], 2);
            }
            double standardDeviation = Math.sqrt(sum / windowSize);
            lowerBands[i] = movingAverages[i] - (standardDeviations * standardDeviation);
        }
        return lowerBands;
    }
}

package com.cointr.upbit.indicators;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.util.NumberFormatter;

import java.util.List;

/**
 * Commodity Channel Index
 */
public class CommodityChannelIndex {

    private double[] cci;

    public void calculate(List<TradeInfoDto> tradeInfoDtoList, int range) throws Exception {
        double[] close = tradeInfoDtoList.stream()
                .mapToDouble(TradeInfoDto::getTradePrice)
                .toArray();
        double[] high = tradeInfoDtoList.stream()
                .mapToDouble(TradeInfoDto::getHighPrice)
                .toArray();
        double[] low = tradeInfoDtoList.stream()
                .mapToDouble(TradeInfoDto::getLowPrice)
                .toArray();

        TypicalPrice typicalPrice = new TypicalPrice();
        double[] tp = typicalPrice.calculate(high, low, close).getTypicalPrice();

        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage();
        double[] sma = simpleMovingAverage.calculate(tp, range).getSMA();

        this.cci = new double[high.length];

        double[] meanDev = new double[high.length];

        double sum = 0;
        double meanDeviation = 0;

        for (int i = (range - 1); i < close.length; i++) {

            sum = 0;
            meanDeviation = 0;

            for (int j = (i - range + 1); j < (i + 1); j++) {
                sum += Math.abs(tp[j] - sma[i]);
            }

            meanDeviation = sum / range;

            meanDev[i] = meanDeviation;

            if (meanDeviation > 0) {
                this.cci[i] = NumberFormatter.round((tp[i] - sma[i]) / (0.015 * meanDeviation));
            }
            tradeInfoDtoList.get(i).setCci(this.cci[i]);
        }

    }

    public double[] getCCI() {
        return this.cci;
    }

}
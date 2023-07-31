package com.cointr.upbit.util;


import com.cointr.upbit.dto.TradeInfoDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Moving Average Convergence/Divergence Oscillator
 */
public class MovingAverageConvergenceDivergence {

    private static final int CROSSOVER_NONE = 0;
    private static final int CROSSOVER_POSITIVE = 1;
    private static final int CROSSOVER_NEGATIVE = -1;

    private double[] prices;
    private double[] macd;
    private double[] signal;

    private double[] diff;
    private int[] crossover;

    public void calculate(List<TradeInfoDto> tradeInfoDtoList, int fastPeriod, int slowPeriod,
                          int signalPeriod) throws Exception {
        double[] prices = tradeInfoDtoList.stream()
                .mapToDouble(TradeInfoDto::getTradePrice)
                .toArray();
        this.prices = prices;
        this.macd = new double[prices.length];
        this.signal = new double[prices.length];

        this.diff = new double[prices.length];
        this.crossover = new int[prices.length];

        ExponentialMovingAverage emaShort = new ExponentialMovingAverage();
        emaShort.calculate(prices, fastPeriod).getEMA();

        ExponentialMovingAverage emaLong = new ExponentialMovingAverage();
        emaLong.calculate(prices, slowPeriod).getEMA();

        for (int i = slowPeriod - 1; i < this.prices.length; i++) {
            this.macd[i] = emaShort.getEMA()[i] - emaLong.getEMA()[i];
            tradeInfoDtoList.get(i).setMacd(this.macd[i]);
            tradeInfoDtoList.get(i).setMacdEmaShort(emaShort.getEMA()[i]);
            tradeInfoDtoList.get(i).setMacdEmaLong(emaLong.getEMA()[i]);
        }

        ExponentialMovingAverage signalEma = new ExponentialMovingAverage();
        this.signal = signalEma.calculate(this.macd, signalPeriod).getEMA();

        for (int i = 0; i < this.macd.length; i++) {
            this.diff[i] = this.macd[i] - this.signal[i];

            if (this.diff[i] > 0 && this.diff[i - 1] < 0) {
                this.crossover[i] = MovingAverageConvergenceDivergence.CROSSOVER_POSITIVE;
            } else if (this.diff[i] < 0 && this.diff[i - 1] > 0) {
                this.crossover[i] = MovingAverageConvergenceDivergence.CROSSOVER_NEGATIVE;
            } else {
                this.crossover[i] = MovingAverageConvergenceDivergence.CROSSOVER_NONE;
            }
            tradeInfoDtoList.get(i).setMacdSignal(this.signal[i]);
            tradeInfoDtoList.get(i).setMacdSignalHistogram(this.crossover[i]);
        }

    }

    public double[] getMACD() {
        return this.macd;
    }

    public double[] getSignal() {
        return this.signal;
    }

    public double[] getDiff() {
        return this.diff;
    }

    public int[] getCrossover() {
        return this.crossover;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.prices.length; i++) {
            sb.append(String.format("%02.2f", this.prices[i]));
            sb.append(" ");
            sb.append(String.format("%02.2f", this.macd[i]));
            sb.append(" ");
            sb.append(String.format("%02.2f", this.signal[i]));
            sb.append(" ");
            sb.append(String.format("%02.2f", this.diff[i]));
            sb.append(" ");
            sb.append(String.format("%d", this.crossover[i]));
            sb.append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }
}

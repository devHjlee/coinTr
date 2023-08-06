//package com.cointr.upbit.service;
//
//import com.cointr.upbit.dto.TradeInfoDto;
//
//import java.util.Arrays;
//
//public class RSI {
//
//    public static void main(String[] args) {
//        testa a = new testa();
//        a.test();
//
//    }
//    static class testa{
//        private double[] prices;
//        private int period;
//
//        private double[] change;
//
//        private double[] gain;
//        private double[] loss;
//
//        private double[] rs;
//        private double[] rsi;
//
//        private double[] avgGain;
//        private double[] avgLoss;
//    public void test() {
//        int period = 14;
//        double[] prices = {
//                20.9, 21.4, 22.3, 22.1, 21.8, 21.5, 21.4, 21.4, 21.3, 21.3, 21.3, 21.8, 21.7, 21.7, 21.9,
//                21.5, 21.4, 21.3, 21.0, 20.6, 20.3, 20.6, 20.5, 20.6, 20.7, 21.0, 20.7, 20.6, 20.7, 20.7,
//                20.6, 20.7, 20.6, 20.4, 20.5, 20.4, 20.5, 20.5, 20.5, 20.5, 20.4, 20.2, 20.4, 20.3, 20.3,
//                20.2, 20.2, 20.2, 20.3, 19.9, 20.1, 20.4, 20.2, 19.9, 20.0, 19.9, 19.1, 19.6, 19.9, 19.9,
//                20.1, 20.0, 20.1, 20.1, 19.9, 20.0, 20.1, 20.4, 20.4, 20.4, 19.9, 19.0, 19.0, 18.3, 18.3,
//                18.5, 18.6, 18.9, 18.8, 18.6, 18.7, 18.6, 18.7, 18.4, 18.5, 18.5, 18.6, 18.6, 18.6, 18.4,
//                18.5, 18.6, 18.5, 18.4, 18.7, 18.6, 18.6, 18.5, 18.6, 18.5, 18.5, 18.3, 18.3, 18.2, 18.3,
//                18.3, 18.5, 18.5, 18.4, 18.8, 18.7, 18.6, 19.1, 19.0, 19.2, 19.7, 20.1, 20.1, 19.5, 19.4,
//                19.1, 18.8, 18.8, 18.7, 18.6, 18.5, 18.4, 18.5, 18.4, 18.4, 18.4, 18.5, 18.5, 18.5, 18.5,
//                18.5, 18.5, 18.4, 18.4, 18.5, 18.5, 18.4, 18.5, 18.5, 18.6, 18.5, 18.5, 18.5, 18.5, 18.5,
//                18.6, 18.7, 18.6, 18.7, 18.6, 18.5, 18.8, 18.7, 18.8, 18.9, 18.8, 18.8, 18.7, 18.3, 18.4,
//                18.4, 18.4, 18.3, 18.9, 19.3, 19.2, 19.1, 19.3, 19.4, 19.2, 19.1, 18.9, 19.1, 19.0, 19.1,
//                19.2, 19.1, 18.9, 19.4, 19.3, 19.3, 19.7, 19.7, 19.6, 19.4, 19.4, 19.4, 19.4, 19.2, 19.3,
//                19.1, 19.2, 19.2, 19.2, 19.1
//        };
//
//
//        this.prices = prices;
//        this.period = period;
//
//        this.change = new double[prices.length];
//
//        this.gain = new double[prices.length];
//        this.loss = new double[prices.length];
//
//        this.avgGain = new double[prices.length];
//        this.avgLoss = new double[prices.length];
//
//        this.rs = new double[prices.length];
//        this.rsi = new double[prices.length];
//
//        for(int i=0; i<this.prices.length; i++) {
//
//            if(i>0) {
//                this.change[i] = this.prices[i] - this.prices[i-1];
//                if(this.change[i] > 0) {
//                    this.gain[i] = this.change[i];
//                } else if(this.change[i] < 0) {
//                    this.loss[i] = Math.abs(this.change[i]);
//                }
//            }
//
//            if(i==this.period) {
//                this.avgGain[i] = Arrays.stream(Arrays.copyOfRange(this.gain, 0, this.period)).sum() / period;
//                this.avgLoss[i] = Arrays.stream(Arrays.copyOfRange(this.loss, 0, this.period)).sum() / period;
//            } else if(i>=this.period) {
//                this.avgGain[i] = (this.avgGain[i-1] * 13 + this.gain[i]) / this.period;
//                this.avgLoss[i] = (this.avgLoss[i-1] * 13 + this.loss[i]) / this.period;
//            }
//
//            if(i>=this.period) {
//
//                // RS = Average Gain / Average Loss
//                this.rs[i] = (this.avgGain[i] / this.avgLoss[i]);
//
//                //               100
//                // RSI = 100 - --------
//                //              1 + RS
//                this.rsi[i] = 100 - ( 100 / (1+this.rs[i]));
//            }//52.63 52.69 rs가 더 작다  100- 20 100- 10
//
//
//        }
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < this.prices.length; i++) {
//            sb.append(String.format("%02.2f", this.prices[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.change[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.gain[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.loss[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.avgGain[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.avgLoss[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.rs[i]));
//            sb.append(" ");
//            sb.append(String.format("%02.2f", this.rsi[i]));
//            sb.append("\n");
//        }
//
//        System.out.println(sb.toString());
//    }
//
//    }
//}

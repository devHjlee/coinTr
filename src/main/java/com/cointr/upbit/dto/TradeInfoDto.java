package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;


@Getter
@Setter
@ToString
public class TradeInfoDto {

    @Comment("코인 코드")
    String market;

    @Comment("거래 일자")
    String tradeDate;

    @Comment("거래 시간")
    String tradeTime;

    @Comment("현재가")
    double tradePrice;

    @Comment("시작가")
    double openingPrice;

    @Comment("고가")
    double highPrice;

    @Comment("저가")
    double lowPrice;

    @Comment("누적 거래대금(UTC 0시 기준)")
    double accTradePrice;

    @Comment("누적 거래량(UTC 0시 기준)")
    double accTradeVolume;

    @Comment("누적 매도량(웹 소켓만 가능)")
    double accAskVolume;

    @Comment("누적 매수량(웹 소켓만 가능)")
    double accBidVolume;

    @Comment("매수매도구분")
    String askBid;

    @Comment("거래량")
    double tradeVolume;

    @Comment("매도 거래대금")
    double askPrice;
    @Comment("분별 매도량")
    double askVolume;
    @Comment("매수 거래대금")
    double bidPrice;
    @Comment("분별 매수량")
    double bidVolume;

    @Comment("CCI")
    double cci;

    @Comment("볼린저밴드 평균")
    double bbAvg;

    @Comment("볼린저밴드 UP")
    double bbUp;

    @Comment("볼린저밴드 DOWN")
    double bbDown;

    @Comment("RSI")
    double rsi;

    @Comment("MACD")
    double macd;

    @Comment("MACD_EMA_SHORT")
    double macdEmaShort;

    @Comment("MACD_EMA_LONG")
    double macdEmaLong;

    @Comment("MACD_SIGNAL")
    double macdSignal;

    @Comment("MACD_HISTOGRAM")
    double macdSignalHistogram;

    @Comment("ADX")
    double adx;

    @Comment("PARABOLIC_SAR")
    double pSar;

    @Comment("AROON_UP")
    double aroonUp;

    @Comment("AROON_DOWN")
    double aroonDown;

    @Comment("AROON_OSCILLATOR")
    double aroonOscillator;

    @Comment("Stochastic FastK")
    double fastK;

    @Comment("Stochastic FastD")
    double fastD;

    @Comment("Stochastic SlowK")
    double slowK;

    @Comment("Stochastic SlowK")
    double slowD;

    @Comment("Type A")
    String typeA = "N";

    @Comment("Type B")
    String typeB = "N";

    @Comment("Type C")
    String typeC = "N";
}

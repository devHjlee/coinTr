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

    @Comment("매수 일자")
    String BuyDate;

    @Comment("매수가")
    double BuyPrice;

    @Comment("매도 일자")
    String SellDate;

    @Comment("매도가")
    double SellPrice;

    @Comment("매도완료")
    String SellYn = "N";

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

    @Comment("Stochastic FastK")
    double fastK;

    @Comment("Stochastic FastD")
    double fastD;

    @Comment("Stochastic SlowK")
    double slowK;

    @Comment("Stochastic SlowK")
    double slowD;

}

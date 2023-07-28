package com.cointr.upbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Setter
public class TradeInfoDto {

    @Comment("코인 코드")
    String market;

    @Comment("거래 일자")
    String tradeDate;

    @Comment("현재가")
    double tradePrice;

    @Comment("누적 거래량(UTC 0시 기준)")
    double accTradeVolume;

    @Comment("누적 매도량(웹 소켓만 가능)")
    double accAskVolume;

    @Comment("누적 매수량(웹 소켓만 가능)")
    double accBidVolume;

}

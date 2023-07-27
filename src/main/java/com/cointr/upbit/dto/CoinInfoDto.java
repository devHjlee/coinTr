package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class CoinInfoDto {
    @Comment("마켓 코드")
    String market;
    @Comment("거래 일자")
    String tradeDate;
    @Comment("현재가")
    Long tradePrice;
    @Comment("누적 거래량(UTC 0시 기준)")
    Long accTradeVolume;
    @Comment("누적 매도량(웹 소켓만 가능)")
    Long accAskVolume;
    @Comment("누적 매수량(웹 소켓만 가능)")
    Long accBidVolume;

}

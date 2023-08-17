package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@ToString
public class VolumeInfoDto {
    @Comment("코인 코드")
    String market;

    @Comment("거래 일자")
    String tradeDate;

    @Comment("거래 시간")
    String tradeTime;

    @Comment("현재가")
    double tradePrice;

    @Comment("거래량")
    double tradeVolume;

    @Comment("매수매도구분")
    String askBid;

    @Comment("매도 거래대금")
    double askPrice;

    @Comment("분별 매도량")
    double askVolume;

    @Comment("매수 거래대금")
    double bidPrice;

    @Comment("분별 매수량")
    double bidVolume;

    @Comment("체결번호")
    String sequentialId;

    @Comment("알림여부")
    String alarmYn;
}

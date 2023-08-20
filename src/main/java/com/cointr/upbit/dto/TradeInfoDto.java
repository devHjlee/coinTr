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
}

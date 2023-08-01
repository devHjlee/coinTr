package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class CoinIndex {

    @Comment("코인 코드")
    String market;

    @Comment("RSI")
    double rsi;

    @Comment("MACD")
    double macd;

}

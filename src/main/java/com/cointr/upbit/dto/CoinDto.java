package com.cointr.upbit.dto;

import com.cointr.upbit.domain.Coin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class CoinDto {
    @Comment("마켓 코드")
    String market;
    @Comment("한글명")
    String koreanName;

    public Coin toEntity(){
        return Coin.builder().id(market).koreaName(koreanName).build();
    }
}

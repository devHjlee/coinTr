package com.cointr.upbit.dto;

import com.cointr.upbit.domain.Coin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinDto {
    String market;
    String koreanName;

    public Coin toEntity(){
        return Coin.builder().id(market).koreaName(koreanName).build();
    }
}

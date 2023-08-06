package com.cointr.upbit.dto;

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

}

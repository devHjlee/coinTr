package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ConditionDto implements Serializable {

    @Comment("캔들 타입")
    String candleType;

    @Comment("조건 타입")
    String conditionType;

    @Comment("조건")
    String condition;
}

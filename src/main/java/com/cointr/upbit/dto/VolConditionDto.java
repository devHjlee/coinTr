package com.cointr.upbit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class VolConditionDto implements Serializable {
    @Comment("거래대금 조건")
    double conditionPrice;
}

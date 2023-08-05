package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FifteenTradeInfoRepository {

    List<TradeInfoDto> selectTradeInfo(String market);
    int insertBulkTradeInfo(List<TradeInfoDto> tradeInfoDtos);

}

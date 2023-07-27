package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoinRepository {
    List<CoinDto> findAll();

    int insertTradeInfo(TradeInfoDto tradeInfoDto);

    int updateTradeInfo(TradeInfoDto tradeInfoDto);
}

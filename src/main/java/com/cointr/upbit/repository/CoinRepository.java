package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoinRepository {
    List<CoinDto> findAll();

    List<TradeInfoDto> selectTradeInfo(String market);

    int insertTradeInfo(TradeInfoDto tradeInfoDto);

    void insertBulkTradeInfo(List<TradeInfoDto> tradeInfoDtos);

    void deleteTradeInfo();

    int updateTradeInfo(TradeInfoDto tradeInfoDto);
}

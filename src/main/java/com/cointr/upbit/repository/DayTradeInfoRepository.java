package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DayTradeInfoRepository {

    List<TradeInfoDto> selectTradeInfo(String market);

    void insertBulkTradeInfo(List<TradeInfoDto> tradeInfoDtos);


    //todo api 테스트를 위한 임시
    List<TradeInfoDto> selectTradeVolumes();
}

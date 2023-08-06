package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FifteenTradeInfoRepository {
    private final RedisTemplate<String, List<TradeInfoDto>> redisTemplate;

    public List<TradeInfoDto> findTradeInfo(String market){
        return redisTemplate.opsForValue().get(market+"_FIF");
    }

    public void saveTradeInfo(String market, List<TradeInfoDto> tradeInfoDtoList) {
        redisTemplate.opsForValue().set(market+"_FIF",tradeInfoDtoList);
    }
}

package com.cointr.upbit.repository;

import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.dto.TradeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeInfoRepository {

    private final RedisTemplate<String, TradeInfoDto> redisTemplate;

    public List<TradeInfoDto> findTradeInfo(String market) {
        return redisTemplate.opsForList().range(market,0,0);
    }

    public void insertBuyInfo(String market, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().leftPush(market, tradeInfoDto);
    }

    public void updateSellInfo(String market, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().set(market, 0, tradeInfoDto);
    }
}

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

    public List<TradeInfoDto> findTradeInfo(String key) {
        return redisTemplate.opsForList().range(key,0,0);
    }

    public void insertBuyInfo(String key, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().leftPush(key, tradeInfoDto);
    }

    public void updateSellInfo(String key, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().set(key, 0, tradeInfoDto);
    }
}

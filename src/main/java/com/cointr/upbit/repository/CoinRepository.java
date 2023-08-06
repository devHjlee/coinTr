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
public class CoinRepository {
    private final RedisTemplate<String, List<CoinDto>> redisTemplate;

    public void coinSaveAll(List<CoinDto> coinDtoList) {
        redisTemplate.opsForValue().set("coin",coinDtoList);
    };

    public List<CoinDto> findAllCoin() {
        return redisTemplate.opsForValue().get("coin");
    }
}

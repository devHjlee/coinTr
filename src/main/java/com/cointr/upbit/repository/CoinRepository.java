package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class CoinRepository {
    private final RedisTemplate<String, CoinDto> redisTemplate;

    public void coinSaveAll(List<CoinDto> coinDtoList) {
        redisTemplate.delete("coin");
        for(CoinDto coinDto : coinDtoList) {
            redisTemplate.opsForList().rightPush("coin", coinDto);
        }
    };
    public List<CoinDto> findAllCoin() {
        return redisTemplate.opsForList().range("coin", 0, -1);
    }
}

package com.cointr.upbit.service;

import com.cointr.upbit.dto.TradeInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, List<TradeInfoDto>> redisTemplate;

    public void saveDataToRedis(String key, List<TradeInfoDto> data) {
        redisTemplate.opsForValue().set(key, data);
    }
    public List<TradeInfoDto> getDataFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

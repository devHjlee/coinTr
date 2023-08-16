package com.cointr.upbit.repository;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.dto.VolumeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeInfoRepository {
    private final RedisTemplate<String, TradeInfoDto> redisTemplate;
    private final RedisTemplate<String, TradeInfoDto> redisTemplateObject;

    private final RedisTemplate<String, VolumeInfoDto> redisTemplateVolume;

    public List<TradeInfoDto> findTradeInfo(String market,int startIdx, int endIdx){
        return redisTemplate.opsForList().range(market,startIdx,endIdx);
    }

    public void updateTradeInfo(String market, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().set(market,0,tradeInfoDto);
    }

    public void insertTradeInfo(String market, TradeInfoDto tradeInfoDto) {
        redisTemplate.opsForList().leftPush(market,tradeInfoDto);
    }

    public void saveAllTradeInfo(String market, List<TradeInfoDto> tradeInfoDtoList) {
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        redisTemplateObject.opsForList().rightPushAll(market,tradeInfoDtoList);
    }

    public List<VolumeInfoDto> findVolumeInfo(String market,int startIdx, int endIdx){
        return redisTemplateVolume.opsForList().range(market,startIdx,endIdx);
    }

    public void updateVolumeInfo(String market, VolumeInfoDto volumeInfoDto) {
        redisTemplateVolume.opsForList().set(market,0,volumeInfoDto);
    }

    public void insertVolumeInfo(String market, VolumeInfoDto volumeInfoDto) {
        redisTemplateVolume.opsForList().leftPush(market,volumeInfoDto);
    }
}

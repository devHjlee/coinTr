package com.cointr.upbit.repository;

import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.dto.VolumeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PriceInfoRepository {
    private final RedisTemplate<String, PriceInfoDto> redisTemplate;
    private final RedisTemplate<String, PriceInfoDto> redisTemplateObject;

    private final RedisTemplate<String, VolumeInfoDto> redisTemplateVolume;

    public List<PriceInfoDto> findTradeInfo(String market, int startIdx, int endIdx){
        return redisTemplate.opsForList().range(market,startIdx,endIdx);
    }

    public void updateTradeInfo(String market, PriceInfoDto priceInfoDto) {
        redisTemplate.opsForList().set(market,0, priceInfoDto);
    }

    public void insertTradeInfo(String market, PriceInfoDto priceInfoDto) {
        redisTemplate.opsForList().leftPush(market, priceInfoDto);
    }

    public void saveAllTradeInfo(String market, List<PriceInfoDto> priceInfoDtoList) {
        priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());
        redisTemplateObject.opsForList().rightPushAll(market, priceInfoDtoList);
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

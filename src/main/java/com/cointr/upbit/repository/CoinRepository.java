package com.cointr.upbit.repository;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.ConditionDto;
import com.cointr.upbit.dto.VolConditionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CoinRepository {
    private final RedisTemplate<String, CoinDto> redisTemplate;
    private final RedisTemplate<String, ConditionDto> redisTemplateCondition;

    public void coinSaveAll(List<CoinDto> coinDtoList) {
        //redisTemplate.opsForValue().set("coin",coinDtoList);
        redisTemplate.delete("coin");
        for(CoinDto coinDto : coinDtoList) {
            redisTemplate.opsForList().rightPush("coin", coinDto);
        }
    };

    public List<CoinDto> findAllCoin() {
        return redisTemplate.opsForList().range("coin", 0, -1);
    }

    public void saveCondition(ConditionDto conditionDto) {
        redisTemplateCondition.opsForHash().put("condition",conditionDto.getCandleType()+conditionDto.getConditionType(),conditionDto);
    }

    public List<ConditionDto> findCondition() {
        List<ConditionDto> rs = new ArrayList<>();
        Map<Object, Object> conditionMap = redisTemplateCondition.opsForHash().entries("condition");
        for(Map.Entry<Object,Object> conditionDtoEntry : conditionMap.entrySet()) {
            rs.add((ConditionDto) conditionDtoEntry.getValue());
        }
        return rs;
    }

    public void saveConditionPrice(VolConditionDto volConditionDto) {
        redisTemplateCondition.opsForHash().put("conditionPrice","conditionPrice",volConditionDto);
    }

    public VolConditionDto findVolCondition() {
        return (VolConditionDto) redisTemplateCondition.opsForHash().get("conditionPrice","conditionPrice");
    }
}

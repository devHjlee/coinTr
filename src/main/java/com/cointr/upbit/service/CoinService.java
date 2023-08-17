package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.ConditionDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.dto.VolConditionDto;
import com.cointr.upbit.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final UpbitApi upbitApi;
    private final CoinRepository coinRepository;

    /**
     * 전체 코인 목록 저장(원화만)
     */
    public void coinSaveAll() {

        List<CoinDto> coinDtoList = upbitApi.getCoinList();
        coinDtoList.stream().filter(x-> !x.getMarket().contains("KRW-")).collect(Collectors.toList()).forEach(coinDtoList::remove);

        coinRepository.coinSaveAll(coinDtoList);
    }

    public List<CoinDto> findAllCoin() {
        return coinRepository.findAllCoin();
    }

    public void saveCondition(ConditionDto conditionDto) {
        coinRepository.saveCondition(conditionDto);
    }
    
    public List<ConditionDto> findCondition() {
        return coinRepository.findCondition();
    }

    public void saveConditionPrice(VolConditionDto volConditionDto) {
        coinRepository.saveConditionPrice(volConditionDto);
    }

    public VolConditionDto findVolCondition() {
        return coinRepository.findVolCondition();
    }
}

package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
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
    private final DayTradeInfoService dayTradeInfoService;
    private final FifteenTradeInfoService fifteenTradeInfoService;

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

    /**
     * 저장된 조건에 맞춰 알림 전송
     */
    public void searchCondition() {
        String b = "rsi>50 && adx >10";
        List<CoinDto> coinDtoList = coinRepository.findAllCoin();
        for(CoinDto coinDto : coinDtoList) {
            TradeInfoDto tradeInfoDto = dayTradeInfoService.findTradeInfo(coinDto.getMarket()).get(0);
            if(upbitApi.evaluateCondition(b,tradeInfoDto)) {
                System.out.println("Market :"+tradeInfoDto.getMarket() +" :RSI :"+tradeInfoDto.getRsi() + ":ADX:"+tradeInfoDto.getAdx());
            }

        }

    }

}

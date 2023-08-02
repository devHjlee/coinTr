package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        List<CoinDto> coinDtos = upbitApi.getCoinList();
        coinDtos.stream().filter(x-> !x.getMarket().contains("KRW-")).collect(Collectors.toList()).forEach(coinDtos::remove);

        coinRepository.insertBulkCoin(coinDtos);
    }

    public List<CoinDto> selectCoin() {
        return coinRepository.findAll();
    }
}

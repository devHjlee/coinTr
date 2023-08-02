package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.repository.CoinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FifteenTradeInfoServiceTest {
    @Autowired
    private FifteenTradeInfoService fifteenTradeInfoService;
    @Autowired
    private CoinRepository coinRepository;
    @Test
    void fifteenCandleSave() {
        List<CoinDto> coinDtoList = coinRepository.findAll();
        for(CoinDto coinDto : coinDtoList) {
            //fifteenTradeInfoService.fifteenCandleSave(coinDto.getMarket());
        }
        fifteenTradeInfoService.fifteenCandleSave("KRW-EOS");
    }
}
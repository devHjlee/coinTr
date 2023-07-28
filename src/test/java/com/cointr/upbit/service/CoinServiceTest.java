package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.CoinIndex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinServiceTest {
    @Autowired
    private CoinService coinService;

    @Test
    void selectCoins() {
        List<CoinDto> coinDtoList = coinService.selectCoins();
        for(CoinDto coinDto : coinDtoList) {
            System.out.println(coinDto.getMarket());
        }
    }

    @Test
    void getRSI() {
        CoinIndex coinIndex = coinService.getRSI("KRW-BTC");
        System.out.println(coinIndex.getRsi());
        System.out.println("dlgudwo");
    }

    @Test
    void dayCandleSave() {
        coinService.dayCandleSave("KRW-BTC");
    }
}
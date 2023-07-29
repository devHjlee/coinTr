package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.CoinIndex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//todo : 코드 다시 작성
@SpringBootTest
class CoinServiceTest {
    @Autowired
    private CoinService coinService;

    @Test
    void 전체코인목록() {
        List<CoinDto> coinDtoList = coinService.selectCoins();
        for(CoinDto coinDto : coinDtoList) {
            System.out.println(coinDto.getMarket());
        }
    }

    @Test
    void 코인별RSI() {
        CoinIndex coinIndex = coinService.getRSI("KRW-KNC");
        System.out.println(coinIndex.getRsi());
        System.out.println("dlgudwo");
    }

    @Test
    void 코인별MACD() {
        coinService.getMACD("KRW-KNC");
    }

    @Test
    void 일봉캔들_저장() {
        coinService.dayCandleSave("KRW-KNC");
    }
}
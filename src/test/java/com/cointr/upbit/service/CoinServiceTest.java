package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
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
}
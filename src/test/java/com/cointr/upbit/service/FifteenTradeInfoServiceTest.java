package com.cointr.upbit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FifteenTradeInfoServiceTest {
    @Autowired
    private FifteenTradeInfoService fifteenTradeInfoService;

    @Test
    void fifteenCandleSave() {
        fifteenTradeInfoService.fifteenCandleSave("KRW-BTG");
    }
}
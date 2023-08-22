package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import com.cointr.upbit.repository.PriceInfoRepository;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//todo : 코드 다시 작성
@SpringBootTest
class DayPriceInfoServiceTest {
    @Autowired
    private DayPriceInfoService dayPriceInfoService;
    @Autowired
    private PriceInfoRepository priceInfoRepository;
    @Autowired
    private MinutePriceInfoService minutePriceInfoService;
    @Autowired
    private CoinRepository coinRepository;
    @Autowired
    private CoinService coinService;

    @Autowired
    private UpbitApi upbitApi;
    @Test
    void 코인전체저장() {
//        List<PriceInfoDto> priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-SEI",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-AAVE",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-CRO",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
    }
}
package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FifteenTradeInfoServiceTest {
    @Autowired
    private FifteenTradeInfoService fifteenTradeInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CoinRepository coinRepository;
    @Test
    void fifteenCandleSave() {
        List<CoinDto> coinDtoList = coinRepository.findAll();
        for(CoinDto coinDto : coinDtoList) {
            System.out.println("START:" + coinDto.getMarket());
            fifteenTradeInfoService.fifteenCandleSave(coinDto.getMarket());
        }
    }

    @Test
    void testRedis(){
        List<TradeInfoDto> rs = redisService.getDataFromRedis("KRW-BTC");
        rs.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        for(TradeInfoDto tradeInfoDto : rs) {
            System.out.println(tradeInfoDto.toString());
        }
    }
    @Test
    void 한개코인_15분봉저장() {
        fifteenTradeInfoService.fifteenCandleSave("KRW-JST");
    }
}
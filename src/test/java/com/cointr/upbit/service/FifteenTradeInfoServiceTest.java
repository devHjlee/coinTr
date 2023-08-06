package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;

@SpringBootTest
class FifteenTradeInfoServiceTest {
    @Autowired
    private FifteenTradeInfoService fifteenTradeInfoService;
    @Autowired
    private CoinRepository coinRepository;
    @Autowired
    private UpbitApi upbitApi;
    @Test
    void fifteenCandleSave() {
        List<CoinDto> coinDtoList = coinRepository.findAllCoin();
        for(CoinDto coinDto : coinDtoList) {
            fifteenTradeInfoService.fifteenCandleSave(coinDto.getMarket());
        }
    }

    @Test
    void 한개코인_15분봉저장() {
        fifteenTradeInfoService.fifteenCandleSave("KRW-RFR");
    }

    @Test
    void getRsi() {
        List<TradeInfoDto> rs = fifteenTradeInfoService.findTradeInfo("KRW-RFR");

        upbitApi.calculateIndicators(rs);
        rs.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        for(int i = 0; i < 5; i++) {
            System.out.println(rs.get(i).getRsi());
        }
    }
}
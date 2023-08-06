package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.DayTradeInfoRepository;
import com.cointr.upbit.api.UpbitApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayTradeInfoService {
    private final DayTradeInfoRepository dayTradeInfoRepository;
    private final UpbitApi upbitApi;

    public List<TradeInfoDto> findTradeInfo(String market) {
        return dayTradeInfoRepository.findTradeInfo(market);
    }
    /**
     * 코인에 대한 일별 거래내역 저장
     * @param market
     */
    public void dayCandleSave(String market) {

        try {
            Thread.sleep(50);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<TradeInfoDto> tradeInfoDtoList = upbitApi.getCandle(market,"day",0);
        if(tradeInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(tradeInfoDtoList);
            dayTradeInfoRepository.saveTradeInfo(market,tradeInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param tradeInfoDto
     */
    public void updateTechnicalIndicator(TradeInfoDto tradeInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.findTradeInfo(tradeInfoDto.getMarket());
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        if(tradeInfoDtoList.get(0).getTradeDate().equals(tradeInfoDto.getTradeDate())) {
            tradeInfoDtoList.set(0,tradeInfoDto);
        }else {
            tradeInfoDtoList.add(0,tradeInfoDto);
        }

        upbitApi.calculateIndicators(tradeInfoDtoList);
        //최상위 데이터 하나만 변경해야 하기에 desc 정렬
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        dayTradeInfoRepository.saveTradeInfo(tradeInfoDto.getMarket(),tradeInfoDtoList);
    }

}

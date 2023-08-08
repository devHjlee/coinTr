package com.cointr.upbit.service;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.TradeInfoRepository;
import com.cointr.upbit.api.UpbitApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayTradeInfoService {
    private final TradeInfoRepository tradeInfoRepository;
    private final UpbitApi upbitApi;

    public List<TradeInfoDto> findTradeInfo(String market,int startIdx, int endIdx) {
        return tradeInfoRepository.findTradeInfo("DAY_"+market,startIdx,endIdx);
    }
    /**
     * 코인에 대한 일별 거래내역 저장
     * @param market
     */
    public void dayCandleSave(String market) {
        String marketKey = "DAY_"+market;
        try {
            Thread.sleep(50);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<TradeInfoDto> tradeInfoDtoList = upbitApi.getCandle(market,"day",0);
        if(tradeInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(tradeInfoDtoList);
            tradeInfoRepository.saveAllTradeInfo(marketKey,tradeInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param tradeInfoDto
     */
    public void updateTechnicalIndicator(TradeInfoDto tradeInfoDto) {
        String marketKey = "DAY_"+tradeInfoDto.getMarket();
        List<TradeInfoDto> tradeInfoDtoList = tradeInfoRepository.findTradeInfo(marketKey,0,-1);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        if(tradeInfoDtoList.get(0).getTradeDate().equals(tradeInfoDto.getTradeDate())) {
            tradeInfoDtoList.set(0,tradeInfoDto);
            upbitApi.calculateIndicators(tradeInfoDtoList);
            tradeInfoRepository.updateTradeInfo(marketKey,tradeInfoDtoList.get(0));
        }else {
            tradeInfoDtoList.add(0,tradeInfoDto);
            upbitApi.calculateIndicators(tradeInfoDtoList);
            tradeInfoRepository.insertTradeInfo(marketKey,tradeInfoDtoList.get(0));
        }
    }

}

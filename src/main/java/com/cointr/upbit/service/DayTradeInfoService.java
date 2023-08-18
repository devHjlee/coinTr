package com.cointr.upbit.service;

import com.cointr.upbit.dto.PriceInfoDto;
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

    public List<PriceInfoDto> findTradeInfo(String market, int startIdx, int endIdx) {
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
        List<PriceInfoDto> priceInfoDtoList = upbitApi.getCandle(market,"day",0);
        if(priceInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(priceInfoDtoList);
            tradeInfoRepository.saveAllTradeInfo(marketKey, priceInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param priceInfoDto
     */
    public void updateTechnicalIndicator(PriceInfoDto priceInfoDto) {
        String marketKey = "DAY_"+ priceInfoDto.getMarket();
        List<PriceInfoDto> priceInfoDtoList = tradeInfoRepository.findTradeInfo(marketKey,0,-1);
        priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());

        if(priceInfoDtoList.get(0).getTradeDate().equals(priceInfoDto.getTradeDate())) {
            priceInfoDtoList.set(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            tradeInfoRepository.updateTradeInfo(marketKey, priceInfoDtoList.get(0));
        }else {
            priceInfoDtoList.add(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            tradeInfoRepository.insertTradeInfo(marketKey, priceInfoDtoList.get(0));
        }
    }

}

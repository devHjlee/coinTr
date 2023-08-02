package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.FifteenTradeInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FifteenTradeInfoService {
    private final FifteenTradeInfoRepository fifteenTradeInfoRepository;
    private final UpbitApi upbitApi;

    /**
     * 코인에 대한 일별 거래내역 저장
     * @param market
     */
    public void fifteenCandleSave(String market) {

        try {
            Thread.sleep(50);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<TradeInfoDto> tradeInfoDtoList = upbitApi.getCandle(market,"minutes",15);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        //todo 공통으로 만들어라
        if(tradeInfoDtoList.size() > 26) {
            upbitApi.getMACD(tradeInfoDtoList);
            upbitApi.getRSI(tradeInfoDtoList);
            upbitApi.getCCI(tradeInfoDtoList);
            upbitApi.getBollingerBand(tradeInfoDtoList);
            upbitApi.getADX(tradeInfoDtoList);
            upbitApi.getPSar(tradeInfoDtoList);
            upbitApi.getAroon(tradeInfoDtoList);
            upbitApi.getStochastics(tradeInfoDtoList);
            fifteenTradeInfoRepository.insertBulkTradeInfo(tradeInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param tradeInfoDto
     */
    public void updateTechnicalIndicator(TradeInfoDto tradeInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = fifteenTradeInfoRepository.selectTradeInfo(tradeInfoDto.getMarket());

        int convTime =  Integer.parseInt(tradeInfoDto.getTradeTime().substring(2,4));
        String tradeTime = "";
        if (convTime >= 0 && convTime < 15) {
            tradeTime = "00";
        } else if (convTime >= 15 && convTime < 30) {
            tradeTime = "15";
        } else if (convTime >= 30 && convTime < 45) {
            tradeTime = "30";
        } else {
            tradeTime = "45";
        }
        tradeInfoDto.setTradeDate(tradeInfoDto.getTradeDate()+tradeInfoDto.getTradeTime().substring(0,2)+tradeTime);

        log.info("tradeDate :" + tradeInfoDto.getTradeTime());

        if(tradeInfoDtoList.get(0).getTradeDate().equals(tradeInfoDto.getTradeDate())) {
            tradeInfoDtoList.set(0,tradeInfoDto);
        }else {
            tradeInfoDtoList.add(0,tradeInfoDto);
        }

        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        upbitApi.getRSI(tradeInfoDtoList);
        upbitApi.getMACD(tradeInfoDtoList);
        upbitApi.getCCI(tradeInfoDtoList);
        upbitApi.getBollingerBand(tradeInfoDtoList);
        upbitApi.getADX(tradeInfoDtoList);
        upbitApi.getPSar(tradeInfoDtoList);
        upbitApi.getAroon(tradeInfoDtoList);
        upbitApi.getStochastics(tradeInfoDtoList);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        fifteenTradeInfoRepository.insertBulkTradeInfo(tradeInfoDtoList.subList(0,1));
    }

}

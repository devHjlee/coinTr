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
     * 코인에 대한 15분별거래내역 저장
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

        if(tradeInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(tradeInfoDtoList);
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

        //todo 로우 하이 계산해서 넣어줘야한다 : 검증필요
        //같은 시간대에 데이터가 존재 시 고가,저가 금액 조정
        if(tradeInfoDtoList.get(0).getTradeDate().equals(tradeInfoDto.getTradeDate())) {
            //이전 데이터 값의 하이랑 비교해서 높으면 하이 변경, 로우 비교해서 낮으면 로우변경
            if(tradeInfoDtoList.get(0).getHighPrice() < tradeInfoDto.getTradePrice()) {
                tradeInfoDto.setHighPrice(tradeInfoDto.getTradePrice());
            }
            if(tradeInfoDtoList.get(0).getLowPrice() > tradeInfoDto.getTradePrice()) {
                tradeInfoDto.setLowPrice(tradeInfoDto.getTradePrice());
            }
            tradeInfoDtoList.set(0,tradeInfoDto);
        }else {
            // 새로운 데이터일 경우 시작,고가,저가에 현재가 입력
            tradeInfoDto.setHighPrice(tradeInfoDto.getTradePrice());
            tradeInfoDto.setLowPrice(tradeInfoDto.getTradePrice());
            tradeInfoDto.setOpeningPrice(tradeInfoDto.getTradePrice());
            tradeInfoDtoList.add(0,tradeInfoDto);
        }

        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        upbitApi.calculateIndicators(tradeInfoDtoList);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        fifteenTradeInfoRepository.insertBulkTradeInfo(tradeInfoDtoList.subList(0,1));
    }

}

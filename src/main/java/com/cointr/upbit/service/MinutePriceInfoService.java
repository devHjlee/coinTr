package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.repository.PriceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinutePriceInfoService {
    private final PriceInfoRepository priceInfoRepository;
    private final UpbitApi upbitApi;

    /**
     * 코인에 대한 분별 거래내역 저장
     * @param market
     */
    public void minuteCandleSave(String market, String minute) {
        String marketKey = minute+"_"+market;
        try {
            Thread.sleep(80);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<PriceInfoDto> priceInfoDtoList = upbitApi.getCandle(market,"minutes",minute);
        if(priceInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(priceInfoDtoList);
            priceInfoRepository.saveAllTradeInfo(marketKey, priceInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param priceInfoDto
     */
    public void updateTechnicalIndicator(PriceInfoDto priceInfoDto, String minute) {

        String marketKey = minute+"_"+ priceInfoDto.getMarket();
        List<PriceInfoDto> priceInfoDtoList = priceInfoRepository.findTradeInfo(marketKey,0,200);
        getTradeDate(priceInfoDto,minute);

        if (priceInfoDtoList.get(0).getTradeDate().equals(priceInfoDto.getTradeDate())) {
            if (priceInfoDtoList.get(0).getHighPrice() < priceInfoDto.getTradePrice()) {
                priceInfoDto.setHighPrice(priceInfoDto.getTradePrice());
            } else {
                priceInfoDto.setHighPrice(priceInfoDtoList.get(0).getHighPrice());
            }
            if (priceInfoDtoList.get(0).getLowPrice() > priceInfoDto.getTradePrice()) {
                priceInfoDto.setLowPrice(priceInfoDto.getTradePrice());
            } else {
                priceInfoDto.setLowPrice(priceInfoDtoList.get(0).getLowPrice());
            }
            priceInfoDto.setOpeningPrice(priceInfoDtoList.get(0).getOpeningPrice());
            priceInfoDto.setTypeA(priceInfoDtoList.get(0).getTypeA());

            priceInfoDtoList.set(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            priceInfoRepository.updateTradeInfo(marketKey, priceInfoDtoList.get(0));

        } else {
            // 새로운 데이터일 경우 시작,고가,저가에 현재가 입력
            priceInfoDto.setHighPrice(priceInfoDto.getTradePrice());
            priceInfoDto.setLowPrice(priceInfoDto.getTradePrice());
            priceInfoDto.setOpeningPrice(priceInfoDto.getTradePrice());

            priceInfoDtoList.add(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            priceInfoRepository.insertTradeInfo(marketKey, priceInfoDtoList.get(0));
        }

    }

    private void getTradeDate(PriceInfoDto priceInfoDto, String minute) {
        if("15".equals(minute)) {
            int convTime = Integer.parseInt(priceInfoDto.getTradeTime().substring(2, 4));
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
            priceInfoDto.setTradeDate(priceInfoDto.getTradeDate() + priceInfoDto.getTradeTime().substring(0, 2) + tradeTime);

        }else if("60".equals(minute)) {

            priceInfoDto.setTradeDate(priceInfoDto.getTradeDate() + priceInfoDto.getTradeTime().substring(0, 2) + "00");

        }else if("240".equals(minute)) {
            int convTime = Integer.parseInt(priceInfoDto.getTradeTime().substring(0, 2));
            String tradeTime = "";
            if(convTime >= 0 && convTime < 4) {
                tradeTime = "0000";
            }else if(convTime >= 4 && convTime < 8) {
                tradeTime = "0400";
            }else if(convTime >= 8 && convTime < 12) {
                tradeTime = "0800";
            }else if(convTime >= 12 && convTime < 16) {
                tradeTime = "1200";
            }else if(convTime >= 16 && convTime < 20) {
                tradeTime = "1600";
            }else {
                tradeTime = "2000";
            }
            priceInfoDto.setTradeDate(priceInfoDto.getTradeDate() + tradeTime);
        }
    }
}

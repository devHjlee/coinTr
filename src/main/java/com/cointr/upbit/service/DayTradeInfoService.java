package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.DayTradeInfoRepository;
import com.cointr.upbit.api.UpbitApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DayTradeInfoService {
    private final DayTradeInfoRepository dayTradeInfoRepository;
    private final UpbitApi upbitApi;

    /**
     * 전체 코인 목록 저장(원화만)
     */
    public void coinSaveAll() {

        List<CoinDto> coinDtos = upbitApi.getCoinList();
        coinDtos.stream().filter(x-> !x.getMarket().contains("KRW-")).collect(Collectors.toList()).forEach(coinDtos::remove);

        dayTradeInfoRepository.insertBulkCoin(coinDtos);
    }

    /**
     * 전체 코인 목록 조회
     * @return CoinDto
     */
    public List<CoinDto> selectCoins() {
        return dayTradeInfoRepository.findAll();
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
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        if(tradeInfoDtoList.size() > 26) {
            upbitApi.getMACD(tradeInfoDtoList);
            upbitApi.getRSI(tradeInfoDtoList);
            upbitApi.getCCI(tradeInfoDtoList);
            upbitApi.getBollingerBand(tradeInfoDtoList);
            upbitApi.getADX(tradeInfoDtoList);
            upbitApi.getPSar(tradeInfoDtoList);
            upbitApi.getAroon(tradeInfoDtoList);
            upbitApi.getStochastics(tradeInfoDtoList);
            dayTradeInfoRepository.insertBulkTradeInfo(tradeInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param tradeInfoDto
     */
    public void updateTechnicalIndicator(TradeInfoDto tradeInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(tradeInfoDto.getMarket());

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

        dayTradeInfoRepository.insertBulkTradeInfo(tradeInfoDtoList.subList(0,1));
    }

    /**
     * 코인에 대한 볼린저밴드
     * @param market
     */
    public void getBollingerBand(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getBollingerBand(tradeInfoDtoList);
    }
    /**
     * 코인에 대한 RSI
     * @param market
     * @return CoinIndex
     */
    public void getRSI(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getRSI(tradeInfoDtoList);
    }

    /**
     * 코인에 대한 MACD
     * @param market
     */
    public void getMACD(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getMACD(tradeInfoDtoList);
    }

    /**
     * 코인에 대한 ADX
     * @param market
     */
    public void getADX(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getMACD(tradeInfoDtoList);
    }

    /**
     * 코인에 대한 PSAR
     * @param market
     */
    public void getPSAR(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getPSar(tradeInfoDtoList);
    }

    public void getAroon(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        upbitApi.getAroon(tradeInfoDtoList);
    }

    public void getStochastics(String market) {
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoRepository.selectTradeInfo(market);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        upbitApi.getStochastics(tradeInfoDtoList);
    }

    /**
     * 웹소켓을 통해 받은 정보를 Trade_info 테이블에 저장
     * @param tradeInfoDto
     * @return
     */
    public int insertTradeInfo(TradeInfoDto tradeInfoDto) {
        return dayTradeInfoRepository.insertTradeInfo(tradeInfoDto);
    }


    //todo api 테스트를 위한 임시
    public List<TradeInfoDto> getIndicators(String market) {
        return dayTradeInfoRepository.selectTradeInfo(market);
    }
    public List<TradeInfoDto> getVolumes() {
        return dayTradeInfoRepository.selectTradeVolumes();
    }
}

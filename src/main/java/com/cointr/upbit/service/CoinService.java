package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
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
public class CoinService {
    private final CoinRepository coinRepository;
    private final UpbitApi upbitApi;

    /**
     * 전체 코인 목록 저장(원화만)
     */
    public void coinSaveAll() {

        List<CoinDto> coinDtos = upbitApi.getCoinList();
        coinDtos.stream().filter(x-> !x.getMarket().contains("KRW-")).collect(Collectors.toList()).forEach(coinDtos::remove);

        coinRepository.insertBulkCoin(coinDtos);
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
        List<TradeInfoDto> tradeInfoDtos = upbitApi.getCandle(market);
        tradeInfoDtos.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        if(tradeInfoDtos.size() > 26) {
            upbitApi.getMACD(tradeInfoDtos);
            upbitApi.getRSI(tradeInfoDtos);
            upbitApi.getCCI(tradeInfoDtos);
            upbitApi.getBollingerBand(tradeInfoDtos);
            coinRepository.insertBulkTradeInfo(tradeInfoDtos);
        }

    }

    /**
     * 기술적지표 업데이트
     * @param tradeInfoDto
     */
    public void updateTechnicalIndicator(TradeInfoDto tradeInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = coinRepository.selectTradeInfo(tradeInfoDto.getMarket());
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
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        coinRepository.insertBulkTradeInfo(tradeInfoDtoList.subList(0,1));
    }

    public void getBollingerBand(String market) {
        List<TradeInfoDto> tradeInfoDtos = coinRepository.selectTradeInfo(market);
        upbitApi.getBollingerBand(tradeInfoDtos);
    }
    /**
     * 코인에 대한 RSI
     * @param market
     * @return CoinIndex
     */
    public void getRSI(String market) {
        List<TradeInfoDto> tradeInfoDtos = coinRepository.selectTradeInfo(market);
        upbitApi.getRSI(tradeInfoDtos);
    }

    public void getMACD(String market) {
        List<TradeInfoDto> tradeInfoDtos = coinRepository.selectTradeInfo(market);
        upbitApi.getMACD(tradeInfoDtos);
    }

    /**
     * 전체 코인 목록 조회
     * @return CoinDto
     */
    public List<CoinDto> selectCoins() {
        return coinRepository.findAll();
    }

    /**
     * 웹소켓을 통해 받은 정보를 Trade_info 테이블에 저장
     * @param tradeInfoDto
     * @return
     */
    public int insertTradeInfo(TradeInfoDto tradeInfoDto) {
        return coinRepository.insertTradeInfo(tradeInfoDto);
    }
}

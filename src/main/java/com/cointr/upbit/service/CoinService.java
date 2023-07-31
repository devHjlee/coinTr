package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.CoinIndex;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import com.cointr.upbit.util.UpbitApi;

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

        List<CoinDto> coinDtos = upbitApi.coinSaveAll();
        coinDtos.stream().filter(x-> !x.getMarket().contains("KRW-")).collect(Collectors.toList()).forEach(coinDtos::remove);

        coinRepository.insertBulkCoin(coinDtos);
    }
    /**
     * 코인에 대한 일별 거래내역 저장
     * @param market
     */
    public void dayCandleSave(String market) {

        try {
            Thread.sleep(80);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<TradeInfoDto> tradeInfoDtos = upbitApi.getCandle(market);
        tradeInfoDtos.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        if(tradeInfoDtos.size() > 100) {
            upbitApi.getMACD(tradeInfoDtos);
            upbitApi.getRis(tradeInfoDtos);
            coinRepository.insertBulkTradeInfo(tradeInfoDtos);
        }

    }

    /**
     * 코인에 대한 RSI
     * @param market
     * @return CoinIndex
     */
    public void getRSI(String market) {
        List<TradeInfoDto> tradeInfoDtos = coinRepository.selectTradeInfo(market);
        upbitApi.getRis(tradeInfoDtos);
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

package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.CoinIndex;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import com.cointr.upbit.util.UpbitApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository coinRepository;
    private final UpbitApi upbitApi;

    /**
     * 코인에 대한 일별 거래내역 저장
     * @param market
     */
    public void dayCandleSave(String market) {

        List<TradeInfoDto> tradeInfoDtos = upbitApi.getCandle(market);
        coinRepository.insertBulkTradeInfo(tradeInfoDtos);

    }

    /**
     * 코인에 대한 RSI
     * @param market
     * @return CoinIndex
     */
    public CoinIndex getRSI(String market) {
        List<TradeInfoDto> tradeInfoDtos = coinRepository.selectTradeInfo(market);
        CoinIndex coinIndex = new CoinIndex();
        coinIndex.setMarket(market);
        coinIndex.setRsi(upbitApi.getRis(tradeInfoDtos));

        return coinIndex;
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

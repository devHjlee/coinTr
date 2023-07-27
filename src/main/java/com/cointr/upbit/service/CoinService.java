package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinRepository coinRepository;

    public List<CoinDto> selectCoins() {
        return coinRepository.findAll();
    }

    public int insertTradeInfo(TradeInfoDto tradeInfoDto) {
        return coinRepository.insertTradeInfo(tradeInfoDto);
    }
}

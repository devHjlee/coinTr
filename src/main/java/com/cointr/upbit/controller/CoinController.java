package com.cointr.upbit.controller;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;

    @GetMapping("/indicatros")
    public List<TradeInfoDto> getIndicators(String market) {
        return coinService.getIndicators(market);
    }
    @GetMapping("/volumes")
    public List<TradeInfoDto> getVolumes() {
        return coinService.getVolumes();
    }
}

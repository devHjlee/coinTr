package com.cointr.upbit.controller;

import com.cointr.upbit.dto.*;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayPriceInfoService;
import com.cointr.upbit.service.MinutePriceInfoService;

import com.cointr.websocket.NvWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;
    private final MinutePriceInfoService minutePriceInfoService;
    private final DayPriceInfoService dayPriceInfoService;
    private final NvWebSocket nvWebSocket;

    @GetMapping("/start")
    public Map<String,String> start() {
        Map<String,String> result = new HashMap<>();
        try {
            coinService.coinSaveAll();
            List<CoinDto> coinDtoList = coinService.findAllCoin();
            for (CoinDto coinDto : coinDtoList) {
                minutePriceInfoService.minuteCandleSave(coinDto.getMarket(),"60");
                dayPriceInfoService.dayCandleSave(coinDto.getMarket());
            }
            nvWebSocket.connect();
        } catch (Exception e) {
            result.put("status","fail");
            return result;
        }
        result.put("status","success");
        return result;
    }
}

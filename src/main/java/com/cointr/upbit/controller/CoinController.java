package com.cointr.upbit.controller;

import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayTradeInfoService;
import com.cointr.upbit.service.FifteenTradeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    @GetMapping("/prices")
    public Map<String,Object> getPrices(@RequestParam String market) {
        Map<String,Object> resultMap = new HashMap<>();

        List<TradeInfoDto> tradeInfoDtoList = coinService.getTradePrice(market);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        List<Object[]> prices = new ArrayList<>();
        List<Object[]> rsi = new ArrayList<>();
        Object[] titles = new Object[]{"time","rsi"};
        rsi.add(titles);
        for (TradeInfoDto tradeInfoDto : tradeInfoDtoList) {
            Object[] priceData = new Object[]{tradeInfoDto.getTradeDate().substring(8), tradeInfoDto.getLowPrice(), tradeInfoDto.getTradePrice(),tradeInfoDto.getOpeningPrice(),tradeInfoDto.getHighPrice()};
            Object[] rsiData = new Object[]{tradeInfoDto.getTradeDate().substring(8),tradeInfoDto.getRsi()};
            prices.add(priceData);
            rsi.add(rsiData);
        }
        resultMap.put("prices",prices);
        resultMap.put("rsi",rsi);
        resultMap.put("currentPrice",tradeInfoDtoList.get(19));

        return resultMap;
    }

}

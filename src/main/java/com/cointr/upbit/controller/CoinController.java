package com.cointr.upbit.controller;

import com.cointr.scheduler.CoinScheduledTask;
import com.cointr.upbit.dto.CoinDto;
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
    private final CoinScheduledTask coinScheduledTask;
    private final CoinService coinService;
    private final FifteenTradeInfoService fifteenTradeInfoService;
    private final DayTradeInfoService dayTradeInfoService;

    @GetMapping("/coins")
    public List<CoinDto> getCoins() {
        return coinService.findAllCoin();
    }

    @GetMapping("/shced")
    public void startSched() {
        coinScheduledTask.scheduleTask();
    }

    @GetMapping("/prices")
    public Map<String,Object> getPrices(@RequestParam String market) {
        Map<String,Object> resultMap = new HashMap<>();

        List<TradeInfoDto> tradeInfoDtoList = fifteenTradeInfoService.findTradeInfo(market);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        List<TradeInfoDto> rs = tradeInfoDtoList.subList(0,20);
        rs.sort(Comparator.comparing(TradeInfoDto::getTradeDate));
        List<Object[]> prices = new ArrayList<>();
        List<Object[]> rsi = new ArrayList<>();
        Object[] titles = new Object[]{"time","rsi"};
        rsi.add(titles);
        for (TradeInfoDto tradeInfoDto : rs) {
            Object[] priceData = new Object[]{tradeInfoDto.getTradeDate().substring(8), tradeInfoDto.getLowPrice(), tradeInfoDto.getTradePrice(),tradeInfoDto.getOpeningPrice(),tradeInfoDto.getHighPrice()};
            Object[] rsiData = new Object[]{tradeInfoDto.getTradeDate().substring(8),tradeInfoDto.getRsi()};
            prices.add(priceData);
            rsi.add(rsiData);
        }
        resultMap.put("prices",prices);
        resultMap.put("rsi",rsi);
        resultMap.put("currentPrice",rs.get(19));


        return resultMap;
    }

    @GetMapping("/prices2")
    public List<TradeInfoDto> getPrices2(@RequestParam String market) {

        List<TradeInfoDto> tradeInfoDtoList = fifteenTradeInfoService.findTradeInfo(market);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        return tradeInfoDtoList.subList(0,20);
    }

    @GetMapping("/prices3")
    public List<TradeInfoDto> getPrices3(@RequestParam String market) {

        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoService.findTradeInfo(market);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        return tradeInfoDtoList.subList(0,20);
    }
}

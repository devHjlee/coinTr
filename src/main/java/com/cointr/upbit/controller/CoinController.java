package com.cointr.upbit.controller;

import com.cointr.scheduler.CoinScheduledTask;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.ConditionDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.dto.VolConditionDto;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayTradeInfoService;
import com.cointr.upbit.service.FifteenTradeInfoService;

import com.cointr.websocket.NvWebSocket;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * todo : ErrorCode, RestControllerAdvice 추가 필요
 */

@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
public class CoinController {
    private final CoinScheduledTask coinScheduledTask;
    private final CoinService coinService;
    private final FifteenTradeInfoService fifteenTradeInfoService;
    private final DayTradeInfoService dayTradeInfoService;
    private final NvWebSocket nvWebSocket;

    @GetMapping("/coins")
    public List<CoinDto> getCoins() {
        return coinService.findAllCoin();
    }

    @GetMapping("/start")
    public String start() {
        try {
            //List<ConditionDto> conditionDtoList = coinService.findCondition();
            //if(conditionDtoList.size() < 1) {
            //  return "조건식 먼저 추가해주세요.";
            //}

//            coinService.coinSaveAll();
//            List<CoinDto> coinDtoList = coinService.findAllCoin();
//            for (CoinDto coinDto : coinDtoList) {
//                dayTradeInfoService.dayCandleSave(coinDto.getMarket());
//                fifteenTradeInfoService.minuteCandleSave(coinDto.getMarket());
//            }
            nvWebSocket.connect();
        } catch (Exception e) {
            return "FAIL";
        }

        return "SUCCESS";
    }

    @GetMapping("/shced")
    public void startSched() {
        coinScheduledTask.scheduleTask();
    }

    @GetMapping("/prices")
    public Map<String,Object> getPrices(@RequestParam String market) {
        String coin = "";
        Map<String,Object> resultMap = new HashMap<>();
        List<CoinDto> coinDtoList = coinService.findAllCoin();
        Optional<String> filteredMarket = coinDtoList.stream()
                .filter(vo -> vo.getKoreanName().contains(market))
                .map(CoinDto::getMarket)
                .findFirst();

        if (filteredMarket.isEmpty()) {
            resultMap.put("ERROR","찾을 수 없습니다.");
            return resultMap;
        } else {
            coin = filteredMarket.get();
        }



        List<TradeInfoDto> tradeInfoDtoList = fifteenTradeInfoService.findTradeInfo(coin,0,-1);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());
        List<TradeInfoDto> rs = tradeInfoDtoList.subList(0,30);
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

        return resultMap;
    }

    @GetMapping("/prices2")
    public List<TradeInfoDto> getPrices2(@RequestParam String market) {
        List<CoinDto> coinDtoList = coinService.findAllCoin();
        String coin= "";
        Optional<String> filteredMarket = coinDtoList.stream()
                .filter(vo -> vo.getKoreanName().contains(market))
                .map(CoinDto::getMarket)
                .findFirst();

        if (filteredMarket.isEmpty()) {
            return null;
        } else {
            coin = filteredMarket.get();
        }
        List<TradeInfoDto> tradeInfoDtoList = fifteenTradeInfoService.findTradeInfo(coin,0,-1);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        return tradeInfoDtoList.subList(0,20);
    }

    @GetMapping("/prices3")
    public List<TradeInfoDto> getPrices3(@RequestParam String market) {
        List<CoinDto> coinDtoList = coinService.findAllCoin();
        String coin= "";
        Optional<String> filteredMarket = coinDtoList.stream()
                .filter(vo -> vo.getKoreanName().contains(market))
                .map(CoinDto::getMarket)
                .findFirst();

        if (filteredMarket.isEmpty()) {
            return null;
        } else {
            coin = filteredMarket.get();
        }
        List<TradeInfoDto> tradeInfoDtoList = dayTradeInfoService.findTradeInfo(coin,0,-1);
        tradeInfoDtoList.sort(Comparator.comparing(TradeInfoDto::getTradeDate).reversed());

        return tradeInfoDtoList.subList(0,20);
    }

    @PostMapping("/conditionPrice")
    public List<ConditionDto> saveconditionPrice(@RequestBody VolConditionDto volConditionDto) {
        coinService.saveConditionPrice(volConditionDto);
        return coinService.findCondition();
    }

    @PostMapping("/condition")
    public List<ConditionDto> saveCondition(@RequestBody ConditionDto conditionDto) {
        coinService.saveCondition(conditionDto);
        return coinService.findCondition();
    }
}

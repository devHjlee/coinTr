package com.cointr.upbit.controller;

import com.cointr.scheduler.CoinScheduledTask;
import com.cointr.upbit.dto.*;
import com.cointr.upbit.service.CoinService;
import com.cointr.upbit.service.DayPriceInfoService;
import com.cointr.upbit.service.MinutePriceInfoService;

import com.cointr.upbit.service.TradeInfoService;
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
    private final TradeInfoService tradeInfoService;
    private final MinutePriceInfoService minutePriceInfoService;
    private final DayPriceInfoService dayPriceInfoService;
    private final NvWebSocket nvWebSocket;

    @GetMapping("/coins")
    public List<CoinDto> getCoins() {
        return coinService.findAllCoin();
    }

    @GetMapping("/start")
    public Map<String,String> start() {
        Map<String,String> result = new HashMap<>();
        try {

            coinService.coinSaveAll();
            List<CoinDto> coinDtoList = coinService.findAllCoin();
            for (CoinDto coinDto : coinDtoList) {
//                dayPriceInfoService.dayCandleSave(coinDto.getMarket());
                minutePriceInfoService.minuteCandleSave(coinDto.getMarket(),"240");
                minutePriceInfoService.minuteCandleSave(coinDto.getMarket(),"60");
            }
//            minutePriceInfoService.minuteCandleSave("KRW-XRP","15");
//            minutePriceInfoService.minuteCandleSave("KRW-XRP","60");
            nvWebSocket.connect();
        } catch (Exception e) {
            result.put("status","fail");
            return result;
        }
        result.put("status","success");
        return result;
    }

    @GetMapping("/coinInfo")
    public Map<String,Object> getCoinInfo(@RequestParam String market) {
        String coin= "";
        Map<String,Object> rs = new HashMap<>();
        Optional<String> filteredMarket = coinService.findAllCoin().stream()
                .filter(vo -> vo.getKoreanName().contains(market))
                .map(CoinDto::getMarket)
                .findFirst();

        if (filteredMarket.isPresent()) {
            coin = filteredMarket.get();
            rs.put("minute",minutePriceInfoService.findPriceInfo(coin,0,-1).subList(0,20));
            rs.put("day",dayPriceInfoService.findTradeInfo(coin,0,-1).subList(0,20));
        }

        rs.put("trade",tradeInfoService.tradeList());

        return rs;
    }

//    @GetMapping("/shced")
//    public void startSched() {
//        coinScheduledTask.scheduleTask();
//    }
//
//    @GetMapping("/prices")
//    public Map<String,Object> getPrices(@RequestParam String market) {
//        String coin = "";
//        Map<String,Object> resultMap = new HashMap<>();
//        List<CoinDto> coinDtoList = coinService.findAllCoin();
//        Optional<String> filteredMarket = coinDtoList.stream()
//                .filter(vo -> vo.getKoreanName().contains(market))
//                .map(CoinDto::getMarket)
//                .findFirst();
//
//        if (filteredMarket.isEmpty()) {
//            resultMap.put("ERROR","찾을 수 없습니다.");
//            return resultMap;
//        } else {
//            coin = filteredMarket.get();
//        }
//
//
//
//        List<PriceInfoDto> priceInfoDtoList = minutePriceInfoService.findTradeInfo(coin,0,-1);
//        priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());
//        List<PriceInfoDto> rs = priceInfoDtoList.subList(0,30);
//        rs.sort(Comparator.comparing(PriceInfoDto::getTradeDate));
//        List<Object[]> prices = new ArrayList<>();
//        List<Object[]> rsi = new ArrayList<>();
//        Object[] titles = new Object[]{"time","rsi"};
//        rsi.add(titles);
//        for (PriceInfoDto priceInfoDto : rs) {
//            Object[] priceData = new Object[]{priceInfoDto.getTradeDate().substring(8), priceInfoDto.getLowPrice(), priceInfoDto.getTradePrice(), priceInfoDto.getOpeningPrice(), priceInfoDto.getHighPrice()};
//            Object[] rsiData = new Object[]{priceInfoDto.getTradeDate().substring(8), priceInfoDto.getRsi()};
//            prices.add(priceData);
//            rsi.add(rsiData);
//        }
//        resultMap.put("prices",prices);
//        resultMap.put("rsi",rsi);
//
//        return resultMap;
//    }
//


}

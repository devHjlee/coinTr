package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import com.cointr.upbit.repository.PriceInfoRepository;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//todo : 코드 다시 작성
@SpringBootTest
class DayPriceInfoServiceTest {
    @Autowired
    private DayPriceInfoService dayPriceInfoService;
    @Autowired
    private PriceInfoRepository priceInfoRepository;
    @Autowired
    private MinutePriceInfoService minutePriceInfoService;
    @Autowired
    private CoinRepository coinRepository;
    @Autowired
    private CoinService coinService;

    @Autowired
    private UpbitApi upbitApi;
    @Test
    void 코인전체저장() {
//        minutePriceInfoService.minuteCandleSave("KRW-XTZ","60");
//        List<PriceInfoDto> priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-XTZ",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-AAVE",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-CRO",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//        List<PriceInfoDto> rs = new ArrayList<>();


//        rs.add(priceInfoDtoList.get(30));
//        rs.add(priceInfoDtoList.get(31));
//        rs.add(priceInfoDtoList.get(32));
//        rs.add(priceInfoDtoList.get(33));
//        rs.add(priceInfoDtoList.get(34));
//        rs.add(priceInfoDtoList.get(35));
//        rs.add(priceInfoDtoList.get(36));
//        rs.add(priceInfoDtoList.get(37));
//        rs.add(priceInfoDtoList.get(38));
//        rs.get(0).setTradePrice(935);
        //rs.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());


        //System.out.println(smaCondition(rs));
    }

    public boolean smaCondition(List<PriceInfoDto> priceInfoDtoList) {
        boolean compare = true;

        if(priceInfoDtoList.get(0).getSma5() >= priceInfoDtoList.get(0).getTradePrice()) return false;

        if(priceInfoDtoList.get(1).getHighPrice() >= priceInfoDtoList.get(0).getTradePrice()) return false;

        //1 종가는 5선보다 위
        //if(priceInfoDtoList.get(1).getSma5() >= priceInfoDtoList.get(1).getTradePrice()) return false;

        // 1의 종가는 2의 종가보다 위
        //if(priceInfoDtoList.get(2).getTradePrice() > priceInfoDtoList.get(1).getTradePrice()) return false;

        //120 > 60 > 5 선
        for(int i = 7; i > 0; i--) {
            System.out.println(priceInfoDtoList.get(i).getSma120() + " ::" + priceInfoDtoList.get(i).getSma60() + "::"+priceInfoDtoList.get(i).getSma5() );
            if(!(priceInfoDtoList.get(i).getSma120() > priceInfoDtoList.get(i).getSma60() && priceInfoDtoList.get(i).getSma60() > priceInfoDtoList.get(i).getSma5())) {
//                compare = false;
//                break;
                return false;
            }
        }

        //8~2 우하향
        for(int i = 7; i > 2; i--) {
            if (!(priceInfoDtoList.get(i).getSma120() > priceInfoDtoList.get(i - 1).getSma120()
                    && priceInfoDtoList.get(i).getSma60() > priceInfoDtoList.get(i - 1).getSma60()
                    && priceInfoDtoList.get(i).getSma5() > priceInfoDtoList.get(i - 1).getSma5())) {
//                compare = false;
//                break;
                return false;
            }
        }
        //5선 기준 8~2 까지 종가가 아래
        for(int i = 7; i > 1; i--) {

            if(!(priceInfoDtoList.get(i).getSma5() >= priceInfoDtoList.get(i).getTradePrice())) {
//                compare = false;
//                break;
                return false;
            }
        }

        return compare;
    }

}
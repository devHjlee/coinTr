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
        List<PriceInfoDto> priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-LINK",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-AAVE",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
//
//        priceInfoDtoList = priceInfoRepository.findTradeInfo("60_KRW-CRO",0,-1);
//        System.out.println(upbitApi.smaCondition(priceInfoDtoList));
        List<PriceInfoDto> rs = new ArrayList<>();

        priceInfoDtoList.get(20).setSma120(11015);
        priceInfoDtoList.get(19).setSma120(11014);
        priceInfoDtoList.get(18).setSma120(11013);
        priceInfoDtoList.get(17).setSma120(11012);
        priceInfoDtoList.get(16).setSma120(11011);
        priceInfoDtoList.get(15).setSma120(11010);
        priceInfoDtoList.get(14).setSma120(11009);
        priceInfoDtoList.get(13).setSma120(11008);
        priceInfoDtoList.get(12).setSma120(11008);

        rs.add(priceInfoDtoList.get(20));
        rs.add(priceInfoDtoList.get(19));
        rs.add(priceInfoDtoList.get(18));
        rs.add(priceInfoDtoList.get(17));
        rs.add(priceInfoDtoList.get(16));
        rs.add(priceInfoDtoList.get(15));
        rs.add(priceInfoDtoList.get(14));
        rs.add(priceInfoDtoList.get(13));
        rs.add(priceInfoDtoList.get(12));
        rs.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());


        System.out.println(smaCondition(rs));
    }

    public boolean smaCondition(List<PriceInfoDto> priceInfoDtoList) {
        boolean compare = true;

        if(priceInfoDtoList.get(0).getSma5() >= priceInfoDtoList.get(0).getTradePrice()) return false;

        //1 종가는 5선보다 위
        if(priceInfoDtoList.get(1).getSma5() >= priceInfoDtoList.get(1).getTradePrice()) return false;

        // 1의 종가는 2의 종가보다 위
        if(priceInfoDtoList.get(2).getTradePrice() > priceInfoDtoList.get(1).getTradePrice()) return false;

        //120 > 60 > 5 선
        for(int i = 8; i > 0; i--) {
            System.out.println(priceInfoDtoList.get(i).getSma120() + " ::" + priceInfoDtoList.get(i).getSma60() + "::"+priceInfoDtoList.get(i).getSma5() );
            if(!(priceInfoDtoList.get(i).getSma120() > priceInfoDtoList.get(i).getSma60() && priceInfoDtoList.get(i).getSma60() > priceInfoDtoList.get(i).getSma5())) {
                compare = false;
                break;
            }
        }

        //8~2 우하향
        for(int i = 8; i > 2; i--) {
            if (!(priceInfoDtoList.get(i).getSma120() > priceInfoDtoList.get(i - 1).getSma120()
                    && priceInfoDtoList.get(i).getSma60() > priceInfoDtoList.get(i - 1).getSma60()
                    && priceInfoDtoList.get(i).getSma5() > priceInfoDtoList.get(i - 1).getSma5())) {
                compare = false;
                break;
            }
        }
        //5선 기준 8~2 까지 종가가 아래
        for(int i = 8; i > 1; i--) {

            if(!(priceInfoDtoList.get(i).getSma5() >= priceInfoDtoList.get(i).getTradePrice())) {
                compare = false;
                break;
            }
        }

        return compare;
    }
}
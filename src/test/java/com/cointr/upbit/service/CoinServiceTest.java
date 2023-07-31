package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
import com.cointr.upbit.dto.CoinIndex;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
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

import static org.junit.jupiter.api.Assertions.*;

//todo : 코드 다시 작성
@SpringBootTest
class CoinServiceTest {
    @Autowired
    private CoinService coinService;

    @Test
    void 코인전체저장() {
        coinService.coinSaveAll();
    }

    @Test
    void 전체코인목록() {
        List<CoinDto> coinDtoList = coinService.selectCoins();
        for(CoinDto coinDto : coinDtoList) {
            System.out.println(coinDto.getMarket());
        }
    }

    @Test
    void 코인_RSI() {
        coinService.getRSI("KRW-BTG");
    }

    @Test
    void 코인_MACD() {
        coinService.getMACD("KRW-BTG");
    }

    @Test
    void 코인_볼린저밴드() {
        coinService.getBollingerBand("KRW-BTG");
    }

    @Test
    void 코인_ADX() {
        coinService.getBollingerBand("KRW-BTG");
    }

    @Test
    void 코인_PSAR() {
        coinService.getPSAR("KRW-BTG");
    }

    @Test
    void 코인_일봉캔들_저장() {
        coinService.dayCandleSave("KRW-BTG");
    }

    @Test
    void updateTechnicalIndicator() {
        String jsonData = "[\n" +
                "    {\n" +
                "        \"market\": \"KRW-BTC\",\n" +
                "        \"candle_date_time_utc\": \"2023-08-01T00:00:00\",\n" +
                "        \"candle_date_time_kst\": \"2023-07-31T09:00:00\",\n" +
                "        \"opening_price\": 37850000.00000000,\n" +
                "        \"high_price\": 38139000.00000000,\n" +
                "        \"low_price\": 37819000.00000000,\n" +
                "        \"trade_price\": 38017000.00000000,\n" +
                "        \"timestamp\": 1690768642569,\n" +
                "        \"candle_acc_trade_price\": 12335596367.81773000,\n" +
                "        \"candle_acc_trade_volume\": 324.61644831,\n" +
                "        \"prev_closing_price\": 37850000.00000000,\n" +
                "        \"change_price\": 167000.00000000,\n" +
                "        \"change_rate\": 0.0044121532\n" +
                "    }\n" +
                "]";
        JsonArray jsonArray = new GsonBuilder().create().fromJson(jsonData,JsonArray.class);
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            jsonObject.addProperty("trade_date",jsonObject.get("candle_date_time_utc").getAsString().replaceAll("-", "").substring(0, 8));
            jsonObject.addProperty("acc_trade_price",jsonObject.get("candle_acc_trade_price").getAsString());
            jsonObject.addProperty("acc_trade_volume",jsonObject.get("candle_acc_trade_volume").getAsString());
        });
        Type listType = new TypeToken<ArrayList<TradeInfoDto>>(){}.getType();
        List<TradeInfoDto> tradeInfoDto= new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)//JSON CamleCase 로 변환
                .create()
                .fromJson(jsonArray, listType);
        coinService.updateTechnicalIndicator(tradeInfoDto.get(0));
    }

}
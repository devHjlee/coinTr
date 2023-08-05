package com.cointr.upbit.service;

import com.cointr.upbit.dto.CoinDto;
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
import java.util.List;

//todo : 코드 다시 작성
@SpringBootTest
class DayTradeInfoServiceTest {
    @Autowired
    private DayTradeInfoService dayTradeInfoService;
    @Autowired
    private CoinRepository coinRepository;
    @Test
    void 전체코인_일봉캔들_저장() {
        List<CoinDto> coinDtoList = coinRepository.findAll();
        for(CoinDto coinDto : coinDtoList) {
            dayTradeInfoService.dayCandleSave(coinDto.getMarket());
        }
    }
    @Test
    void 한개코인_일봉캔들_저장() {
        dayTradeInfoService.dayCandleSave("KRW-BTC");
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
        dayTradeInfoService.updateTechnicalIndicator(tradeInfoDto.get(0));
    }

}
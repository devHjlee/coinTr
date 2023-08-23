package com.cointr.upbit.service;

import com.cointr.telegram.TelegramMessageProcessor;
import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.TradeInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeInfoService {

    private final TradeInfoRepository tradeInfoRepository;
    private final TelegramMessageProcessor telegramMessageProcessor;
    private final UpbitApi upbitApi;

    public void condition(List<PriceInfoDto> priceInfoDtoList, String minute) {

        //BUY
        if("60".equals(minute)) {
            if(upbitApi.smaCondition(priceInfoDtoList)) buy(priceInfoDtoList.get(0), minute);
            //if(upbitApi.myCondition(priceInfoDtoList)) tradeInfoService.buy(priceInfoDtoList.get(0),minute);
        }

        if("120".equals(minute) && upbitApi.sma120Condition(priceInfoDtoList)) buy(priceInfoDtoList.get(0),minute);

        //SELL
        sell(priceInfoDtoList.get(0),minute);
        //upbitApi.evaluateCondition(conditionDtoList,tradeInfoDtoList.get(0),"m");
    }
    public void buy(PriceInfoDto priceInfoDto,String minute) {
        List<TradeInfoDto> tradeInfoDtoList = tradeInfoRepository.findTradeInfo("TR_"+minute+"_"+priceInfoDto.getMarket());
        if(!Objects.equals(minute, "1H")) {
            priceInfoDto.setTypeA("Y");
        }
        TradeInfoDto tradeInfoDto = new TradeInfoDto();
        tradeInfoDto.setMarket(priceInfoDto.getMarket());
        tradeInfoDto.setBuyDate(priceInfoDto.getTradeDate());
        tradeInfoDto.setBuyPrice(priceInfoDto.getTradePrice());

        if(tradeInfoDtoList.isEmpty()) {

            tradeInfoRepository.insertBuyInfo("TR_"+minute+"_"+tradeInfoDto.getMarket(),tradeInfoDto);
            String message = "구매 :" + priceInfoDto.getMarket() + "\n" +
                    "캔들 :"+minute + "\n" +
                    "가격 :" + priceInfoDto.getTradePrice() + "\n";
            telegramMessageProcessor.sendMessage("-1001813916001", message);

        }else if("Y".equals(tradeInfoDtoList.get(0).getSellYn())) {

            tradeInfoRepository.insertBuyInfo("TR_"+minute+"_"+tradeInfoDto.getMarket(),tradeInfoDto);
            String message = "구매 :" + priceInfoDto.getMarket() + "\n" +
                    "캔들 :"+minute + "\n" +
                    "가격 :" + priceInfoDto.getTradePrice() + "\n";
            telegramMessageProcessor.sendMessage("-1001813916001", message);
        }

    }

    public void sell(PriceInfoDto priceInfoDto, String minute) {
        List<TradeInfoDto> tradeInfoDtoList = tradeInfoRepository.findTradeInfo("TR_"+minute+"_"+priceInfoDto.getMarket());

        if(!tradeInfoDtoList.isEmpty() && "N".equals(tradeInfoDtoList.get(0).getSellYn())) {
            double per =  (priceInfoDto.getTradePrice() /tradeInfoDtoList.get(0).getBuyPrice()) * 100-100;
            if(per >= 1.5 || per <= -1.5) {
                tradeInfoDtoList.get(0).setMarket(priceInfoDto.getMarket());
                tradeInfoDtoList.get(0).setSellDate(priceInfoDto.getTradeDate());
                tradeInfoDtoList.get(0).setSellPrice(priceInfoDto.getTradePrice());
                tradeInfoDtoList.get(0).setSellYn("Y");
                tradeInfoRepository.updateSellInfo("TR_"+minute+"_"+tradeInfoDtoList.get(0).getMarket(), tradeInfoDtoList.get(0));
                String message = "판매 :" + priceInfoDto.getMarket() + "\n" +
                        "캔들 :"+minute + "\n" +
                        "가격 :" + priceInfoDto.getTradePrice() + "\n" +
                        "수익 :" + per;

                telegramMessageProcessor.sendMessage("-1001813916001", message);
            }
        }
    }
}

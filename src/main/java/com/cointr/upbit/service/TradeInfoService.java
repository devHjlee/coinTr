package com.cointr.upbit.service;

import com.cointr.telegram.TelegramMessageProcessor;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.dto.TradeInfoDto;
import com.cointr.upbit.repository.TradeInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeInfoService {

    private final TradeInfoRepository tradeInfoRepository;
    private final TelegramMessageProcessor telegramMessageProcessor;

    public void buy(PriceInfoDto priceInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = tradeInfoRepository.findTradeInfo(priceInfoDto.getMarket());
        TradeInfoDto tradeInfoDto = new TradeInfoDto();
        tradeInfoDto.setMarket(priceInfoDto.getMarket());
        tradeInfoDto.setBuyDate(priceInfoDto.getTradeDate());
        tradeInfoDto.setBuyPrice(priceInfoDto.getTradePrice());
        if(tradeInfoDtoList.isEmpty()) {

            tradeInfoRepository.insertBuyInfo(tradeInfoDto.getMarket(),tradeInfoDto);
            String message = "구매 :" + priceInfoDto.getMarket() + "\n" +
                    "-정보-" + "\n" +
                    "가격 :" + priceInfoDto.getTradePrice() + "\n";


            telegramMessageProcessor.sendMessage("-1001813916001", message);
        }else if("Y".equals(tradeInfoDtoList.get(0).getSellYn())) {

            tradeInfoRepository.insertBuyInfo(tradeInfoDto.getMarket(),tradeInfoDto);
            String message = "구매 :" + priceInfoDto.getMarket() + "\n" +
                    "-정보-" + "\n" +
                    "가격 :" + priceInfoDto.getTradePrice() + "\n";


            telegramMessageProcessor.sendMessage("-1001813916001", message);
        }

    }

    public void sell(PriceInfoDto priceInfoDto) {
        List<TradeInfoDto> tradeInfoDtoList = tradeInfoRepository.findTradeInfo(priceInfoDto.getMarket());
        if(!tradeInfoDtoList.isEmpty() && "N".equals(tradeInfoDtoList.get(0).getSellYn())) {
            double per =  (priceInfoDto.getTradePrice() /tradeInfoDtoList.get(0).getBuyPrice()) * 100-100;
            if(per >= 1.5 || per <= -1.5) {
                tradeInfoDtoList.get(0).setMarket(priceInfoDto.getMarket());
                tradeInfoDtoList.get(0).setSellDate(priceInfoDto.getTradeDate());
                tradeInfoDtoList.get(0).setSellPrice(priceInfoDto.getTradePrice());
                tradeInfoDtoList.get(0).setSellYn("Y");
                tradeInfoRepository.updateSellInfo(tradeInfoDtoList.get(0).getMarket(), tradeInfoDtoList.get(0));
                String message = "판매 :" + priceInfoDto.getMarket() + "\n" +
                        "-정보-" + "\n" +
                        "가격 :" + priceInfoDto.getTradePrice() + "\n" +
                        "수익 :" + per;

                telegramMessageProcessor.sendMessage("-1001813916001", message);
            }
        }
    }
}

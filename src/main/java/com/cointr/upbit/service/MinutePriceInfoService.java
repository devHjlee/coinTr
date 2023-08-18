package com.cointr.upbit.service;

import com.cointr.upbit.api.UpbitApi;
import com.cointr.upbit.dto.ConditionDto;
import com.cointr.upbit.dto.PriceInfoDto;
import com.cointr.upbit.dto.VolConditionDto;
import com.cointr.upbit.dto.VolumeInfoDto;
import com.cointr.upbit.repository.CoinRepository;
import com.cointr.upbit.repository.TradeInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinutePriceInfoService {
    private final TradeInfoRepository tradeInfoRepository;
    private final CoinRepository coinRepository;
    private final UpbitApi upbitApi;

    public List<PriceInfoDto> findTradeInfo(String market, int startIdx, int endIdx) {
        List<VolumeInfoDto> volumeInfoDtoList = tradeInfoRepository.findVolumeInfo("MTV_"+market,startIdx,endIdx);
        List<PriceInfoDto> priceInfoDtoList = tradeInfoRepository.findTradeInfo("MINUTE_"+market,startIdx,endIdx);

        Map<String, VolumeInfoDto> volumeMap = volumeInfoDtoList.stream()
                .collect(Collectors.toMap(VolumeInfoDto::getTradeDate, volume -> volume));

        return priceInfoDtoList.stream()
                .peek(trade -> {
                    if (volumeMap.containsKey(trade.getTradeDate())) {
                        VolumeInfoDto volume = volumeMap.get(trade.getTradeDate());
                        trade.setAskVolume(volume.getAskVolume());
                        trade.setAskPrice(volume.getAskPrice());
                        trade.setBidVolume(volume.getBidVolume());
                        trade.setBidPrice(volume.getBidPrice());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 코인에 대한 분별 거래내역 저장
     * @param market
     */
    public void minuteCandleSave(String market) {
        String marketKey = "MINUTE_"+market;
        try {
            Thread.sleep(80);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<PriceInfoDto> priceInfoDtoList = upbitApi.getCandle(market,"minutes",15);
        if(priceInfoDtoList.size() > 26) {
            upbitApi.calculateIndicators(priceInfoDtoList);
            tradeInfoRepository.saveAllTradeInfo(marketKey, priceInfoDtoList);
        }

    }

    /**
     * 웹소켓을 통해 받은 데이터를 기술적지표 계산 후 업데이트
     * @param priceInfoDto
     */
    public void updateTechnicalIndicator(PriceInfoDto priceInfoDto) {
        String marketKey = "MINUTE_"+ priceInfoDto.getMarket();
        List<ConditionDto> conditionDtoList = coinRepository.findCondition();
        List<PriceInfoDto> priceInfoDtoList = tradeInfoRepository.findTradeInfo(marketKey,0,-1);
        priceInfoDtoList.sort(Comparator.comparing(PriceInfoDto::getTradeDate).reversed());

        int convTime = Integer.parseInt(priceInfoDto.getTradeTime().substring(2, 4));
        String tradeTime = "";
        if (convTime >= 0 && convTime < 15) {
            tradeTime = "00";
        } else if (convTime >= 15 && convTime < 30) {
            tradeTime = "15";
        } else if (convTime >= 30 && convTime < 45) {
            tradeTime = "30";
        } else {
            tradeTime = "45";
        }
        priceInfoDto.setTradeDate(priceInfoDto.getTradeDate() + priceInfoDto.getTradeTime().substring(0, 2) + tradeTime);

        //같은 시간대에 데이터가 존재 시 고가,저가 금액 조정
        if (priceInfoDtoList.get(0).getTradeDate().equals(priceInfoDto.getTradeDate())) {
            //이전 데이터 값의 하이랑 비교해서 높으면 하이 변경, 로우 비교해서 낮으면 로우변경
            if (priceInfoDtoList.get(0).getHighPrice() < priceInfoDto.getTradePrice()) {
                priceInfoDto.setHighPrice(priceInfoDto.getTradePrice());
            } else {
                priceInfoDto.setHighPrice(priceInfoDtoList.get(0).getHighPrice());
            }
            if (priceInfoDtoList.get(0).getLowPrice() > priceInfoDto.getTradePrice()) {
                priceInfoDto.setLowPrice(priceInfoDto.getTradePrice());
            } else {
                priceInfoDto.setLowPrice(priceInfoDtoList.get(0).getLowPrice());
            }
            priceInfoDto.setOpeningPrice(priceInfoDtoList.get(0).getOpeningPrice());
            priceInfoDto.setTypeA(priceInfoDtoList.get(0).getTypeA());
            priceInfoDto.setTypeB(priceInfoDtoList.get(0).getTypeB());
            priceInfoDto.setTypeC(priceInfoDtoList.get(0).getTypeC());

            priceInfoDtoList.set(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            upbitApi.myCondition(priceInfoDtoList);
            //upbitApi.evaluateCondition(conditionDtoList,tradeInfoDtoList.get(0),"m");
            tradeInfoRepository.updateTradeInfo(marketKey, priceInfoDtoList.get(0));

        } else {
            // 새로운 데이터일 경우 시작,고가,저가에 현재가 입력
            priceInfoDto.setHighPrice(priceInfoDto.getTradePrice());
            priceInfoDto.setLowPrice(priceInfoDto.getTradePrice());
            priceInfoDto.setOpeningPrice(priceInfoDto.getTradePrice());

            priceInfoDtoList.add(0, priceInfoDto);
            upbitApi.calculateIndicators(priceInfoDtoList);
            upbitApi.myCondition(priceInfoDtoList);
            //upbitApi.evaluateCondition(conditionDtoList, priceInfoDtoList.get(0),"m");
            tradeInfoRepository.insertTradeInfo(marketKey, priceInfoDtoList.get(0));
        }
    }

    /**
     * @param volumeInfoDto
     */
    public void updateTradeVolume(VolumeInfoDto volumeInfoDto) {
        volumeInfoDto.setTradeDate(volumeInfoDto.getTradeDate().replaceAll("-",""));
        volumeInfoDto.setTradeTime(volumeInfoDto.getTradeTime().replaceAll(":",""));
        String marketKey = "MTV_"+volumeInfoDto.getMarket();
        List<VolumeInfoDto> volumeInfoDtoList = tradeInfoRepository.findVolumeInfo(marketKey,0,0);
        VolConditionDto volConditionDto = coinRepository.findVolCondition();

        if (volumeInfoDtoList.size() > 0 && !volumeInfoDtoList.get(0).getSequentialId().equals(volumeInfoDto.getSequentialId())) {
            int convTime = Integer.parseInt(volumeInfoDto.getTradeTime().substring(2, 4));
            String tradeTime = "";
            if (convTime >= 0 && convTime < 15) {
                tradeTime = "00";
            } else if (convTime >= 15 && convTime < 30) {
                tradeTime = "15";
            } else if (convTime >= 30 && convTime < 45) {
                tradeTime = "30";
            } else {
                tradeTime = "45";
            }
            volumeInfoDto.setTradeDate(volumeInfoDto.getTradeDate() + volumeInfoDto.getTradeTime().substring(0, 2) + tradeTime);

            //분봉별 매도 매수 거래량,거래대금
            if (volumeInfoDtoList.get(0).getTradeDate().equals(volumeInfoDto.getTradeDate())) {

                double totalAskPrice = volumeInfoDtoList.get(0).getAskPrice();
                double totalAskVolume = volumeInfoDtoList.get(0).getAskVolume();
                double totalBidPrice = volumeInfoDtoList.get(0).getBidPrice();
                double totalBidVolume = volumeInfoDtoList.get(0).getBidVolume();

                if (("ASK").equals(volumeInfoDto.getAskBid())) {

                    volumeInfoDto.setAskPrice(totalAskPrice + (volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice()));
                    volumeInfoDto.setAskVolume(totalAskVolume + volumeInfoDto.getTradeVolume());
                    volumeInfoDto.setBidPrice(totalBidPrice);
                    volumeInfoDto.setBidVolume(totalBidVolume);
                } else {
                    volumeInfoDto.setAskPrice(totalAskPrice);
                    volumeInfoDto.setAskVolume(totalAskVolume);
                    volumeInfoDto.setBidPrice(totalBidPrice + (volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice()));
                    volumeInfoDto.setBidVolume(totalBidVolume + volumeInfoDto.getTradeVolume());
                }
                upbitApi.evaluateConditionPrice(volConditionDto,volumeInfoDto);
                tradeInfoRepository.updateVolumeInfo(marketKey, volumeInfoDto);

            } else {
                // 새로운 데이터일 경우 시작,고가,저가에 현재가 입력
                if (("ASK").equals(volumeInfoDto.getAskBid())) {
                    volumeInfoDto.setAskPrice(volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice());
                    volumeInfoDto.setAskVolume(volumeInfoDto.getTradeVolume());
                    volumeInfoDto.setBidVolume(0);
                    volumeInfoDto.setBidPrice(0);
                } else {
                    volumeInfoDto.setAskPrice(0);
                    volumeInfoDto.setAskVolume(0);
                    volumeInfoDto.setBidPrice(volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice());
                    volumeInfoDto.setBidVolume(volumeInfoDto.getTradeVolume());
                }
                upbitApi.evaluateConditionPrice(volConditionDto,volumeInfoDto);
                tradeInfoRepository.insertVolumeInfo(marketKey, volumeInfoDto);
            }
        }else {
            if (("ASK").equals(volumeInfoDto.getAskBid())) {
                volumeInfoDto.setAskPrice(volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice());
                volumeInfoDto.setAskVolume(volumeInfoDto.getTradeVolume());
                volumeInfoDto.setBidVolume(0);
                volumeInfoDto.setBidPrice(0);
            } else {
                volumeInfoDto.setAskPrice(0);
                volumeInfoDto.setAskVolume(0);
                volumeInfoDto.setBidPrice(volumeInfoDto.getTradeVolume() * volumeInfoDto.getTradePrice());
                volumeInfoDto.setBidVolume(volumeInfoDto.getTradeVolume());
            }

            tradeInfoRepository.insertVolumeInfo(marketKey, volumeInfoDto);
        }
    }
}
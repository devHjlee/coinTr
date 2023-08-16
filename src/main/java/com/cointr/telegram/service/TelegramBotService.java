package com.cointr.telegram.service;

import com.cointr.upbit.service.DayTradeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//todo 굳이 서비스로 만들어야하나?
@Slf4j
@Service
public class TelegramBotService extends TelegramLongPollingBot {
    public TelegramBotService(DayTradeInfoService dayTradeInfoService) {
        super(new DefaultBotOptions(),"6019178496:AAHSxoXyh0OQwR37_BOuD-4cixHaO7_fTCY");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            log.info("chatID:"+chatId);
            String messageText = update.getMessage().getText();

            if(update.getMessage().getText().contains("KRW-")) {
                //CoinIndex coinIndex = coinService.getRSI(messageText);
                //sendMessage(chatId, messageText+"-RSI: " + coinIndex.getRsi());
            }else {
                sendMessage("-1001813916001", "Received: " + messageText);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "newsBot";
    }

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosing() {
        exe.shutdown();
    }
}


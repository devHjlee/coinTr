package com.cointr.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public class TelegramBotService extends TelegramLongPollingBot {
    public TelegramBotService() {
        super(new DefaultBotOptions(),"6019178496:AAHSxoXyh0OQwR37_BOuD-4cixHaO7_fTCY");
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            // 메시지를 받았을 때 원하는 작업을 수행하도록 구현합니다.
            // 예: 받은 메시지에 따라 다른 답변을 보내거나 기능을 수행합니다.

            sendMessage(chatId, "Received: " + messageText);
        }
    }

    @Override
    public String getBotUsername() {
        return "newsBot";
    }

    private void sendMessage(String chatId, String message) {
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


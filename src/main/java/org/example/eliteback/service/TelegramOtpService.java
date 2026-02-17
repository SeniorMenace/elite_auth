package org.example.eliteback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramOtpService {
    private static final Logger log = LoggerFactory.getLogger(TelegramOtpService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Async
    public void sendOtpTelegram(String chatId, String otp) {
        try {
            DefaultAbsSender sender = new DefaultAbsSender(new DefaultBotOptions()) {
                @Override
                public String getBotToken() {
                    return botToken;
                }
            };
            SendMessage message = new SendMessage(chatId,
                    "Your verification code is: " + otp + ". It expires in 10 minutes.");
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.warn("Failed to send OTP via Telegram to {}: {}", chatId, e.getMessage());
        }
    }
}

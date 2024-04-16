package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.constants.Constants;
import milansomyk.cocktailbot.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramClientService {
    TelegramClient telegramClient = new OkHttpTelegramClient("7175987391:AAFJnoow8hIKmXe0UhBhL0xm-LLJ4_6bwhM");

    public void sendMessage(String chatId,String messageText) {
        try {
            telegramClient.execute(new SendMessage(chatId,messageText));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages(List<SendMessage> messages) {
        try {
            for (SendMessage sendMessage : messages) {
                telegramClient.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void notifyAdmin(String messageText) {
        try {
            telegramClient.execute(new SendMessage(Constants.creatorId.toString(), messageText));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void notifyAllManagers(List<Long> managerIdList, String messageText) {
        try {
            for (Long aLong : managerIdList) {
                telegramClient.execute(new SendMessage(aLong.toString(), messageText));
            }
        } catch (TelegramApiException e) {
            log.error("Exception while trying notify all Role.MANAGER users!");
            notifyAdmin("Exception while trying notify all Role.MANAGER users!");
        }
    }
}

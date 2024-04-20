package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.constants.Constants;
import milansomyk.cocktailbot.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodSerializable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramClientService {
    private final UserService userService;
    @Value("${TOKEN}")
    private String token;
    TelegramClient telegramClient = new OkHttpTelegramClient("6774181756:AAEDUqvHUzt_yxNFij-mfvKLJ8RhwT50z9s");

    public void sendMessage(String chatId, String messageText) {
        try {
            telegramClient.execute(new SendMessage(chatId, messageText));
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

    public void sendCaption(EditMessageCaption editMessageCaption) {
        try {
            telegramClient.execute(editMessageCaption);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void telegramSend(BotApiMethodSerializable method){
        try {
            telegramClient.execute(method);
        } catch (TelegramApiException e){
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

    public void sendPhotos(List<SendPhoto> photos) {
        try {
            for (SendPhoto sendPhoto : photos) {
                telegramClient.execute(sendPhoto);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(SendPhoto photo) {
        try {

            telegramClient.execute(photo);

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

    public void notifyAllManagers(String messageText) {
        List<User> allManagers = userService.findAllManagers();
        if (allManagers==null) return;
        List<Long> managerIdList = allManagers.stream().map(User::getId).toList();
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

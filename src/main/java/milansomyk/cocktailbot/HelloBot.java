package milansomyk.cocktailbot;

import com.vdurmont.emoji.EmojiParser;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.service.UserService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelloBot implements LongPollingSingleThreadUpdateConsumer {
    private final UserService userService;
    TelegramClient telegramClient = new OkHttpTelegramClient("7175987391:AAFJnoow8hIKmXe0UhBhL0xm-LLJ4_6bwhM");
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText())  {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String lngCode = update.getMessage().getFrom().getLanguageCode();
            SendMessage message = null;
            List<SendMessage> messages = new ArrayList<>();
            switch (messageText){
                case "/start" : {
                    User foundUser = userService.getById(chatId);
                    if (!(foundUser==null)){
                        message = new SendMessage(chatId.toString(),"Ви вже авторизований користувач!");
                    }
                    messages.add(new SendMessage(chatId.toString(), "Привіт!"));
                    messages.add(new SendMessage(chatId.toString(), EmojiParser.parseToUnicode("Я телеграм бот, через який можна замовляти коктейлі! :cocktail: \nЯ знаходжусь лише на стадії розробки \uD83D\uDE0A, але вже можу показувати список усіх наявних коктейлів, можу приймати замовлення і зберігати список ваших улюблених напоїв! ❤\uFE0F")));
                    messages.add(new SendMessage(chatId.toString(),"Коротенько опишу команди, які я розумію: \n /menu - список усіх коктейлів; \n /help - список усіх команд"));
                    break;
                }
                case "/add-cocktail" : {}
                case "/cocktails" : {
                    message = new SendMessage(chatId.toString(),"Here is your cocktails!");
                    message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 1","Cocktail 2"))
                            .keyboardRow(new KeyboardRow("Cocktail 3","Cocktail 4")).build());
                    break;
                }
                default: message = new SendMessage(update.getMessage().getChatId().toString(), "Unknown command!");
            }
            try{
                if (!messages.isEmpty()) {
                    for (SendMessage sendMessage : messages) {
                        telegramClient.execute(sendMessage);
                    }
                }else{
                    telegramClient.execute(message);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

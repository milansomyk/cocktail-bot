package milansomyk.cocktailbot;

import lombok.extern.slf4j.Slf4j;
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

@Component
@Slf4j
public class HelloBot implements LongPollingSingleThreadUpdateConsumer {
    TelegramClient telegramClient = new OkHttpTelegramClient("7175987391:AAFJnoow8hIKmXe0UhBhL0xm-LLJ4_6bwhM");

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText())  {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            SendMessage message = null;

            switch (messageText){
                case "/start" : {

                    message = new SendMessage(chatId.toString(), "Hello!");
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
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

package milansomyk.cocktailbot;

import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Cocktail;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.service.CocktailService;
import milansomyk.cocktailbot.service.TelegramClientService;
import milansomyk.cocktailbot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelloBot implements LongPollingSingleThreadUpdateConsumer {
    private final UserService userService;
    private final CocktailService cocktailService;
    private final TelegramClientService telegramClientService;

    @PostConstruct
    public void init() {
        List<User> allManagers = userService.findAllManagers();
        if (allManagers == null) {
            log.error("Role.MANAGER users not found! Can`t invoke init() method!");
            telegramClientService.notifyAdmin("INFO!!! Role.Manager users not found!");
            return;
        }
        List<Long> managerIdList = allManagers.stream().map(User::getId).toList();
        telegramClientService.notifyAllManagers(managerIdList, "Телеграм бот знову працює! Зараз над ним проводяться роботи! Можете потестувати різні функції і надіслати фідбек @milansomyk");
    }

    @Override
    public void consume(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) {
            log.error("No text message received!");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        String lngCode = update.getMessage().getFrom().getLanguageCode();

        SendMessage message = null;
        List<SendMessage> messages = new ArrayList<>();
        org.telegram.telegrambots.meta.api.objects.User updateUser = update.getMessage().getFrom();

        if (messageText.contains("/addCocktail")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                log.error("Error when trying to find user with this id: {}", chatId);
                messages.add(new SendMessage(chatId.toString(), "Не авторизований користувач!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            ;
            if (foundUser.getRole() == Role.MANAGER || foundUser.getRole() == Role.ADMIN) {
                Cocktail cocktail = new Cocktail();
                cocktail = cocktailService.parseString(messageText);
                if (cocktail == null) {
                    log.error("Exception when parsing cocktail from string: {}", messageText);
                    messages.add(new SendMessage(chatId.toString(), "Не надано усієї інформації про коктейль або виникла інша помилка при додаванні!"));
                    telegramClientService.sendMessages(messages);
                    return;
                }
                telegramClientService.sendMessage(chatId.toString(),"Коктейль створено! Можете переглянути його в /menu");
                return;
            }
        }

        switch (messageText) {
            case "/start": {
                User foundUser = userService.getById(chatId);
                if (!(foundUser == null)) {
                    message = new SendMessage(chatId.toString(), "Ви вже авторизований користувач!");
                } else {
                    User user = new User(chatId, updateUser.getFirstName(), updateUser.getLastName(), updateUser.getUserName(), lngCode, Role.USER);
                    if (chatId == 288636429) {
                        user.setRole(Role.ADMIN);
                    }
                    userService.createUser(user);
                    messages.add(new SendMessage(chatId.toString(), "Привіт!"));
                    messages.add(new SendMessage(chatId.toString(), EmojiParser.parseToUnicode("Я телеграм бот, через який можна замовляти коктейлі! :cocktail: \nЯ знаходжусь лише на стадії розробки \uD83D\uDE0A, але вже можу показувати список усіх наявних коктейлів, можу приймати замовлення і зберігати список ваших улюблених напоїв! ❤\uFE0F")));
                    messages.add(new SendMessage(chatId.toString(), "Коротенько опишу команди, які я розумію: \n /menu - список усіх коктейлів; \n /help - список усіх команд;"));
                }
                break;
            }

            case "/cocktails": {
                message = new SendMessage(chatId.toString(), "Here is your cocktails!");
                message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 1", "Cocktail 2"))
                        .keyboardRow(new KeyboardRow("Cocktail 3", "Cocktail 4")).build());
                break;
            }
            default:
                message = new SendMessage(update.getMessage().getChatId().toString(), "Unknown command!");
        }
        if(messages.isEmpty()){
            telegramClientService.sendMessage(message);
        }else{
            telegramClientService.sendMessages(messages);
        }


    }
}

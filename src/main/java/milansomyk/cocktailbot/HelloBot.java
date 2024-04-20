package milansomyk.cocktailbot;

import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Cocktail;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.service.CocktailService;
import milansomyk.cocktailbot.service.TelegramClientService;
import milansomyk.cocktailbot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.toIntExact;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelloBot implements LongPollingSingleThreadUpdateConsumer {
    private final UserService userService;
    private final CocktailService cocktailService;
    private final TelegramClientService telegramClientService;
    public HashMap<String, List<Integer>> userOrder;

    @PostConstruct
    public void init() {
        telegramClientService.notifyAdmin("Телеграм бот знову працює!");
        telegramClientService.notifyAllManagers("Телеграм бот знову працює! Зараз над ним проводяться роботи! Можете потестувати різні функції і надіслати фідбек @milansomyk");
    }
    @Override
    public void consume(Update update) {
        System.out.println("update income!");
        if (update.hasMessage()) {
            handleIncomingMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleButtonClick(update);
        }
    }

    private void handleIncomingMessage(Message inputMessage) {
        if (!inputMessage.hasText()) {
            return;
        }
        SendMessage message;
        List<SendMessage> messages = new ArrayList<>();
        Long chatId = inputMessage.getChatId();
        if (inputMessage.getText().startsWith("/editCocktail")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                messages.add(new SendMessage(chatId.toString(), "Не авторизований користувач!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            if (!(foundUser.getRole() == Role.MANAGER || foundUser.getRole() == Role.ADMIN)) return;
            String[] text = inputMessage.getText().split(" ");
            if (text.length != 2) {
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Не дотримано форматування зміни коктейлю! Ось приклад, як це робити правильно: `/editCocktail 'назва коктейлю'`");
                sendMessage.setParseMode("MarkDown");
                telegramClientService.sendMessage(sendMessage);
                return;
            }
            Cocktail byCocktailName = cocktailService.getByCocktailName(text[1]);
            if (byCocktailName == null) {
                telegramClientService.sendMessage(chatId.toString(), "Коктейль не знайдено!");
                return;
            }
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(byCocktailName.toVisualGoodString())
                    .photo(new InputFile(byCocktailName.getPhotoId()))
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(
                                            new InlineKeyboardRow(
                                                    InlineKeyboardButton.builder().text("Оновити").callbackData("update_cocktail").build
                                                            (),
                                                    InlineKeyboardButton.builder().text("Не оновляти").callbackData("not_update_cocktail").build()
                                            ))
                                    .build())
                    .build();
            telegramClientService.sendPhoto(sendPhoto);
        }
        if (inputMessage.hasPhoto() && inputMessage.getCaption().contains("/addCocktail")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                messages.add(new SendMessage(chatId.toString(), "Не авторизований користувач!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            if (!(foundUser.getRole() == Role.MANAGER || foundUser.getRole() == Role.ADMIN)) return;
            List<PhotoSize> photos = inputMessage.getPhoto();
            String photoId = photos.stream().min(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getFileId)
                    .orElse("");
            Cocktail cocktail;
            cocktail = cocktailService.parseStringAndSave(inputMessage.getCaption(), photoId);

            if (cocktail == null) {
                log.error("Exception when parsing cocktail from string: {}", inputMessage.getCaption());
                messages.add(new SendMessage(chatId.toString(), "Не надано усієї інформації про коктейль або виникла інша помилка при додаванні!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            telegramClientService.sendMessage(chatId.toString(), "Коктейль створено! Можете переглянути його в /order");
            return;
        }
        if (inputMessage.getText().contains("/addManager")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                log.error("Error when trying to find user with this id: {}", chatId);
                messages.add(new SendMessage(chatId.toString(), "Не авторизований користувач!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            if (!(foundUser.getRole() == Role.ADMIN)) return;
            String[] usernameArray = inputMessage.getText().split(" ");
            List<String> usernameList = new ArrayList<>();
            for (int i = 1; i < usernameArray.length; i++) {
                usernameList.add(usernameArray[i]);
            }
            boolean isError = userService.updateUserToManagerByUsername(usernameList);
            if (isError) {
                message = new SendMessage(chatId.toString(), "Error when trying to update this users to Role.MANAGER!");
            } else {
                message = new SendMessage(chatId.toString(), "Users successfully promoted to Role.MANAGER!");
            }
            telegramClientService.sendMessage(message);
            return;
        }
        if (inputMessage.getText().equals("/allUsers")) {
            List<User> allUsers = userService.findAllUsers();
            if (allUsers.isEmpty()) {
                message = new SendMessage(chatId.toString(), "Error while trying to find all users!");
            } else {
                List<String> usernameList = allUsers.stream().map(User::getUsername).toList();
                StringBuilder stringBuilder = new StringBuilder();
                for (String username : usernameList) {
                    stringBuilder.append("`").append(username).append("` ");
                }
                SendMessage sendMessage = new SendMessage(chatId.toString(), stringBuilder.toString());
                sendMessage.setParseMode("MarkDown");
                message = sendMessage;
            }
            telegramClientService.sendMessage(message);
            return;
        }
        String lngCode = inputMessage.getFrom().getLanguageCode();
        org.telegram.telegrambots.meta.api.objects.User updateUser = inputMessage.getFrom();
        switch (inputMessage.getText()) {
            case "/start": {
                User foundUser = userService.getById(chatId);
                if (!(foundUser == null)) {
                    messages.add(new SendMessage(chatId.toString(), "Ви вже авторизований користувач!"));
                } else {
                    User user = new User(chatId, updateUser.getFirstName(), updateUser.getLastName(), updateUser.getUserName(), lngCode, Role.USER);
                    if (chatId == 288636429) {
                        user.setRole(Role.ADMIN);
                    }
                    userService.createUser(user);
                    SendMessage sendMessage = new SendMessage(chatId.toString(), EmojiParser.parseToUnicode("Привіт!\n\n" +
                            "Я телеграм бот, через який можна замовляти коктейлі! :cocktail: \n" +
                            "Я знаходжусь лише на стадії розробки \uD83D\uDE0A, але вже можу показувати список усіх наявних коктейлів, можу приймати замовлення і зберігати список ваших улюблених напоїв! ❤\uFE0F" +
                            "Натисніть кнопку 'Замовити', щоб отримати список коктейлів, які є у наявності у бармена!"));
                    sendMessage.setReplyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("Замовити коктейль").callbackData("order_cocktail").url("https://65060d6a08aa414cc1900a70--zippy-blancmange-90bd0e.netlify.app/?language=uk").build()))
                                    .build()
                    );
                    messages.add(sendMessage);
                }
                telegramClientService.sendMessages(messages);
                break;
            }
            case "/order": {
                chatId = inputMessage.getChatId();
                SendMessage sendMessage = new SendMessage(chatId.toString(), "Ось меню коктейлів:");
                List<Cocktail> allCocktails = cocktailService.getAllCocktails();
                List<KeyboardRow> keyboardRows = new ArrayList<>();
                List<SendPhoto> photos = new ArrayList<>();
                for (Cocktail cocktail : allCocktails) {
                    SendPhoto photo = SendPhoto.builder().chatId(chatId.toString()).photo(new InputFile(cocktail.getPhotoId())).caption(cocktail.toVisualGoodString()).build();
                    photos.add(photo);
                    keyboardRows.add(new KeyboardRow(cocktail.getName()));
                }
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                messages.add(sendMessage);
                telegramClientService.sendMessages(messages);
                telegramClientService.sendPhotos(photos);
                messages.clear();
                break;
            }
            case "/hide": {
                chatId = inputMessage.getChatId();
                SendMessage sendMessage = SendMessage.builder().chatId(chatId.toString()).text("Меню сховано!").replyMarkup(new ReplyKeyboardRemove(true)).build();
                telegramClientService.sendMessage(sendMessage);
                break;
            }
            default:
                message = new SendMessage(inputMessage.getChatId().toString(), "Unknown command!");
                telegramClientService.sendMessage(message);
                break;
        }
    }

    private void handleButtonClick(Update update) {
        String data = update.getCallbackQuery().getData();
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chat_id = update.getCallbackQuery().getMessage().getChatId();
        if (data.equals("update_cocktail")) {
            EditMessageCaption newCaption = EditMessageCaption.builder().chatId(chat_id).messageId(toIntExact(message_id)).caption("Оновлення скоро будуть доступні!").build();
            telegramClientService.sendCaption(newCaption);
        }
        if (data.equals("order_cocktail")) {
            EditMessageText newText = EditMessageText.builder().chatId(chat_id).messageId(toIntExact(message_id)).text("Ось коктейлі:").build();
            telegramClientService.telegramSend(newText);
        }
    }

    @PreDestroy
    public void onDestroy() {
        telegramClientService.notifyAdmin("Бот тимчасово вимкнено!");
        telegramClientService.notifyAllManagers("Бот тимчасово вимкнено!");
    }
}

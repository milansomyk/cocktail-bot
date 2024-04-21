package milansomyk.cocktailbot;

import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.constants.Role;
import milansomyk.cocktailbot.constants.Status;
import milansomyk.cocktailbot.entity.Cocktail;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.service.CocktailService;
import milansomyk.cocktailbot.service.TelegramClientService;
import milansomyk.cocktailbot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonWebApp;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

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
    public HashMap<String, Status> usersStatus = new HashMap<>();

    @PostConstruct
    public void init() {
        telegramClientService.notifyAdmin("Телеграм бот знову працює!");
        telegramClientService.notifyAllManagers("Телеграм бот знову працює! Зараз над ним проводяться роботи! Можете потестувати різні функції і надіслати фідбек @milansomyk");
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleIncomingMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleButtonClick(update);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            handleIncomingPhoto(update.getMessage());
        }
    }

    private void handleIncomingMessage(Message inputMessage) {
        if (!inputMessage.hasText()) {
            return;
        }
        SendMessage message;
        List<SendMessage> messages = new ArrayList<>();
        Long chatId = inputMessage.getChatId();
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
                    User user = new User(chatId, updateUser.getFirstName(), updateUser.getLastName(), updateUser.getUserName(), lngCode, 0, 0, null, Role.USER);
                    if (chatId == 288636429) {
                        user.setRole(Role.ADMIN);
                    }
                    userService.createUser(user);
                    SendMessage sendMessage = new SendMessage(chatId.toString(), EmojiParser.parseToUnicode("Привіт!\n\n" +
                            "Я телеграм бот, через який можна замовляти коктейлі! :cocktail: \n" +
                            "Я знаходжусь лише на стадії розробки \uD83D\uDE0A, але вже можу показувати список усіх наявних коктейлів, можу приймати замовлення і зберігати список ваших улюблених напоїв! ❤\uFE0F\n\n" +
                            "Натисніть кнопку *Продовжити*, щоб почати роботу зі мною!"));
                    sendMessage.setReplyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("Продовжити").callbackData("main_menu").build()))
                                    .build()
                    );
                    sendMessage.enableWebPagePreview();
                    sendMessage.setParseMode("MarkDown");
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
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (data.equals("main_menu")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) return;
            MenuButtonWebApp menuButtonWebApp = new MenuButtonWebApp("Меню коктейлів", new WebAppInfo("https://65060d6a08aa414cc1900a70--zippy-blancmange-90bd0e.netlify.app/?language=uk"));
            telegramClientService.sendMethod(SetChatMenuButton.builder().chatId(chatId).menuButton(menuButtonWebApp).build());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ваш аккаунт:\n");
            stringBuilder.append("*");
            if(foundUser.getFirstName()!=null){
                stringBuilder.append(foundUser.getFirstName()).append(" ");
            }
            if(foundUser.getLastName()!=null){
                stringBuilder.append(foundUser.getLastName()).append(" ");
            }
            stringBuilder.append("(").append(foundUser.getUsername()).append(")");
            stringBuilder.append("*");
            stringBuilder.append("\n\nЗаборгованість\uD83D\uDCB0:\n");
            stringBuilder.append(0).append(" грн");
            EditMessageText newText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(toIntExact(message_id))
                    .text(stringBuilder.toString())
                    .parseMode("MarkDown")
                    .build();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();

            if (foundUser.getRole() == Role.MANAGER | foundUser.getRole()==Role.ADMIN) {
                inlineKeyboardButtonList.add(InlineKeyboardButton.builder().text("Додати коктейль").callbackData("manager_add_cocktail").build());
                inlineKeyboardButtonList.add(InlineKeyboardButton.builder().text("Змінити коктейль").callbackData("manager_edit_cocktail").build());
            }
            if (foundUser.getRole() == Role.ADMIN) {
                inlineKeyboardButtonList.add(InlineKeyboardButton.builder().text("Усі користувачі").callbackData("admin_all_users").build());
            }
            newText.setReplyMarkup(
                    InlineKeyboardMarkup.builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(inlineKeyboardButtonList))
                            .build()
            );
            telegramClientService.telegramSend(newText);
        }
        if (data.equals("manager_edit_cocktail")) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                return;
            }
            MenuButtonWebApp menuButtonWebApp = new MenuButtonWebApp("Змінити коктейль", new WebAppInfo("https://65060d6a08aa414cc1900a70--zippy-blancmange-90bd0e.netlify.app/?language=uk"));
            telegramClientService.sendMethod(SetChatMenuButton.builder().chatId(chatId).menuButton(menuButtonWebApp).build());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Заміна коктейлю!\n\nЩоб змінити коктейль, його ціну або фото, перейдіть у Веб-інтерфейс за допомогою кнопки меню *Змінити коктейль*\nЯкщо ви натиснули помилково, або вже не бажаєте змінювати коктейль, то натисніть кнопку *Назад*");
            EditMessageText newText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(toIntExact(message_id))
                    .text(stringBuilder.toString())
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(
                                            new InlineKeyboardRow(InlineKeyboardButton.builder().text("Назад").callbackData("main_menu").build()))
                                    .build()
                    )
                    .parseMode("MarkDown")
                    .build();
            telegramClientService.telegramSend(newText);
        }
        if(data.equals("manager_add_cocktail")){
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                return;
            }
            telegramClientService.sendMethod(SetChatMenuButton.builder().chatId(chatId).menuButton(null).build());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Додавання коктейлю!\n\nЩоб додати коктейль, надішліть фото коктейлю, та у описі опишіть його згідно форматування:\n\n");
            stringBuilder.append("`<Назва коктейлю>\n<Інградієнти>\n<Ціна>`\n\n");
            stringBuilder.append("\nЯкщо ви натиснули помилково, або вже не бажаєте змінювати коктейль, то натисніть кнопку *Назад*");
            usersStatus.put(foundUser.getId().toString(),Status.WAIT_FOR_INFO);
            EditMessageText newText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(toIntExact(message_id))
                    .text(stringBuilder.toString())
                    .replyMarkup(
                            InlineKeyboardMarkup.builder()
                                    .keyboardRow(
                                            new InlineKeyboardRow(InlineKeyboardButton.builder().text("Назад").callbackData("main_menu").build()))
                                    .build()
                    )
                    .parseMode("MarkDown")
                    .build();
            telegramClientService.telegramSend(newText);
        }

    }

    private void handleIncomingPhoto(Message inputMessage) {
        Long chatId = inputMessage.getChatId();
        List<SendMessage> messages = new ArrayList<>();
        if (inputMessage.hasPhoto() && inputMessage.getCaption() != null) {
            User foundUser = userService.getById(chatId);
            if (foundUser == null) {
                messages.add(new SendMessage(chatId.toString(), "Не авторизований користувач!"));
                telegramClientService.sendMessages(messages);
                return;
            }
            if (!(foundUser.getRole() == Role.MANAGER || foundUser.getRole() == Role.ADMIN)) return;
            if(usersStatus.get(foundUser.getId().toString())!=Status.WAIT_FOR_INFO){
                DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(inputMessage.getMessageId()).build();
                telegramClientService.sendMethod(deleteMessage);
            }
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
    }

    @PreDestroy
    public void onDestroy() {
        telegramClientService.notifyAdmin("Бот тимчасово вимкнено!");
        telegramClientService.notifyAllManagers("Бот тимчасово вимкнено!");
    }
}

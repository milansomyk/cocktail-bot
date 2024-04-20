package milansomyk.cocktailbot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import milansomyk.cocktailbot.service.CocktailService;
import milansomyk.cocktailbot.service.TelegramClientService;
import milansomyk.cocktailbot.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Configuration
@RequiredArgsConstructor
public class BotConfig {
    private final UserService userService;
    private final CocktailService cocktailService;
    private final TelegramClientService telegramClientService;
    @Value("${TOKEN}")
    private String token;
    @PostConstruct
    protected void init(){
        try{
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(token, new HelloBot(userService,cocktailService,telegramClientService));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

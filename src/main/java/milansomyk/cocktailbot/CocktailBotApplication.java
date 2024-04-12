package milansomyk.cocktailbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class CocktailBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CocktailBotApplication.class, args);
		try {
			// Instantiate Telegram Bots API
			TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
			// Register your newly created AbilityBot
			botsApplication.registerBot(new HelloBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}

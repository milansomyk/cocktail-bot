package milansomyk.cocktailbot;

import milansomyk.cocktailbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class CocktailBotApplication {
	public static void main(String[] args){


		SpringApplication.run(CocktailBotApplication.class, args);
		try {
			TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
			botsApplication.registerBot("7175987391:AAFJnoow8hIKmXe0UhBhL0xm-LLJ4_6bwhM", new HelloBot());
		} catch (TelegramApiException e){
			e.printStackTrace();
		}
	}

}

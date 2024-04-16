package milansomyk.cocktailbot;

import jakarta.annotation.Resource;
import milansomyk.cocktailbot.repository.UserRepository;
import milansomyk.cocktailbot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class CocktailBotApplication {
	public static void main(String[] args){
		SpringApplication.run(CocktailBotApplication.class, args);
	}

}

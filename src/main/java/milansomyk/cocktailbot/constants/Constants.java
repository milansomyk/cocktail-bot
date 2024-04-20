package milansomyk.cocktailbot.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static Long creatorId = 288636429L;
    @Value("${BOT_TOKEN}")
    public static String botToken;
}

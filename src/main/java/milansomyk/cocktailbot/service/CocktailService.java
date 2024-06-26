package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Cocktail;
import milansomyk.cocktailbot.repository.CocktailRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CocktailService {
    private final CocktailRepository cocktailRepository;

    public Cocktail parseStringAndSave(String cocktailInfo, String photoId) {
        Cocktail cocktail = new Cocktail();
        String[] split = cocktailInfo.split("\n");

        if (split.length != 3) {
            log.info("Не дотримано форматування при додаванні коктейлів!");
            return null;
        }
        cocktail.setName(split[0]);
        cocktail.setIngredients(split[1]);
        String price = split[2];
        cocktail.setPrice(Double.valueOf(price));
        cocktail.setPhotoId(photoId);
        cocktail.setAvailable(false);
        try {
            cocktail = cocktailRepository.save(cocktail);
        }catch (Exception e){
            log.error("Exception while saving cocktail! Error: {}",e.getMessage());
            return null;
        }
        return cocktail;
    }
    public List<Cocktail> getAllCocktails(){
        List<Cocktail> cocktailList;
        try {
            cocktailList = cocktailRepository.findAll();
        } catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
        return cocktailList;
    }
    public Cocktail getByCocktailName(String cocktailName){
        Cocktail cocktail = null;
        try {
            cocktail = cocktailRepository.findCocktailByName(cocktailName).orElse(null);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return cocktail;
    }

}

package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Cocktail;
import milansomyk.cocktailbot.entity.Ingredient;
import milansomyk.cocktailbot.repository.CocktailRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CocktailService {
    private final CocktailRepository cocktailRepository;
    private final IngredientService ingredientService;

    public Cocktail parseStringAndSave(String cocktailInfo, String photoId) {
        Cocktail cocktail = new Cocktail();
        String[] split = cocktailInfo.split("\n");
        if (split.length != 4) {
            log.info("Exception while parsing cocktail info!");
            return null;
        }
        cocktail.setName(split[1]);

        String[] ingredients = split[2].split(",");
        List<Ingredient> ingredientList = ingredientService.parseAndSaveAll(ingredients);
        cocktail.setIngredients(ingredientList);
//        if(ingredientList == null){
//            return null;
//        }
        String price = split[3];
        cocktail.setPrice(Double.valueOf(price));
        cocktail.setPhotoId(photoId);
        try {
            cocktail = cocktailRepository.save(cocktail);
        }catch (Exception e){
            log.error("Exception while saving cocktail! Error: {}",e.getMessage());
            return null;
        }
        return cocktail;
    }
    public List<Cocktail> getAllCocktails(){
        List<Cocktail> cocktailList = new ArrayList<>();
        try {
            cocktailList = cocktailRepository.findAll();
        } catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
        return cocktailList;
    }

}

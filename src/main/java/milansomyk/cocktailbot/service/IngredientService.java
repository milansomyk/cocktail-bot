package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Ingredient;
import milansomyk.cocktailbot.repository.IngredientCustomRepository;
import milansomyk.cocktailbot.repository.IngredientRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientCustomRepository ingredientCustomRepository;
    public Ingredient findByName(String name){
        Ingredient foundIngredient = null;
        try{
            foundIngredient = ingredientRepository.findByName(name).orElse(null);
        }catch (Exception e){
            log.error("Exception while finding ingredient by name:{}; Error: {}",name,e.getMessage());
            return null;
        }
        if(foundIngredient==null){
            Ingredient createdIngredient = new Ingredient();
            createdIngredient.setName(name);
            try {
                foundIngredient = ingredientRepository.save(createdIngredient);
            } catch (Exception e){
                log.error("Exception while saving ingredient; Error:{}",e.getMessage());
                return null;
            }
        }
        return foundIngredient;
    }
    public List<Ingredient> parseAndSaveAll(String[] ingredientNames){
        if(ingredientNames==null){
            log.error("Exception! IngredientNames array is null!");
            return null;
        }
        List<Ingredient> ingredientList = new ArrayList<>();
        for (String ingredient : ingredientNames) {
            ingredientList.add(new Ingredient().setNameGetValue(StringUtils.deleteWhitespace(ingredient.toLowerCase())));
        }
//        try {
//            ingredientList = ingredientRepository.saveAll(ingredientList);
//        } catch (Exception e) {
//            log.error("Exception while saving all ingredients! Error: {}", e.getMessage());
//            return null;
//        }
        ingredientList = ingredientCustomRepository.replaceAllIngredients(ingredientList);
        if(ingredientList.isEmpty()){
            log.error("Saved List<Ingredient> is null! List:{}",ingredientList);
            return null;
        }
        return ingredientList;
    }
}

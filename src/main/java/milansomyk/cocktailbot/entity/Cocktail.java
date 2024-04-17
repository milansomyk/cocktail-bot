package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Cocktail {
    @Id
    @GeneratedValue
    Integer id;
    @Column(unique = true)
    String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cocktail_ingredients",
            joinColumns = @JoinColumn(name = "cocktail_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    List<Ingredient> ingredients;
    String photoId;
    Double price;
    public String toVisualGoodString(){
        StringBuilder result = new StringBuilder("**" + this.name + "**\n" + "Інградієнти:");
        for (int i = 0; i < ingredients.size(); i++) {
            if(i==ingredients.size()-1){
                result.append(" ").append(ingredients.get(i).getName());
            }
            result.append(" ").append(ingredients.get(i).getName()).append(",");
        }
        result.append("\nЦіна: ").append(price.toString()).append(" грн");
        return result.toString();
    }
}

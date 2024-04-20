package milansomyk.cocktailbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Cocktail {
    @Id
    @GeneratedValue
    Integer id;
    @Column(unique = true)
    String name;
    String ingredients;
    String photoId;
    Double price;
    public String toVisualGoodString(){
        StringBuilder result = new StringBuilder("**" + this.name + "**\n" + "Інградієнти: ").append(ingredients);
        result.append("\nЦіна: ").append(price.toString()).append(" грн");
        return result.toString();
    }
}

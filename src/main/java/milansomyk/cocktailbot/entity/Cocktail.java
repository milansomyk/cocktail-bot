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
    String ingredients;
    String photoId;
    Double price;
    public String toVisualGoodString(){
        StringBuilder result = new StringBuilder("**" + this.name + "**\n" + "Інградієнти: ").append(ingredients);
        result.append("\nЦіна: ").append(price.toString()).append(" грн");
        return result.toString();
    }
}

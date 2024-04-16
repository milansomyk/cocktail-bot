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
    @ManyToMany
    @JoinTable(
            name = "cocktail_ingredients",
            joinColumns = @JoinColumn(name = "cocktail_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    List<Ingredient> ingredients;
    Double price;

}

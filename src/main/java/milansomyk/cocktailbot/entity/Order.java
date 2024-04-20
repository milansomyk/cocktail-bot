package milansomyk.cocktailbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    Integer id;
    @OneToMany
    List<Cocktail> cocktailList;
}

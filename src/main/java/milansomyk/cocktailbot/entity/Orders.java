package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Orders {
    @Id
    @GeneratedValue
    Integer id;
    @OneToMany
    @JoinTable(
            name = "order_cocktails",
            joinColumns = @JoinColumn(name = "order_id"),foreignKey = @ForeignKey(name="FK_ORDER_COCKTAILS_ORDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "cocktail_id"),inverseForeignKey = @ForeignKey(name="FK_ORDER_COCKTAILS_COCKTAIL_ID")
    )
    List<Cocktail> cocktailList;
}

package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import milansomyk.cocktailbot.constants.Role;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class User {
    @Id
    Long id;
    String firstName;
    String lastName;
    String username;
    String lngCode;
    Integer borrowed;
    Integer discount;
    @OneToMany
    @JoinTable(
            name = "user_orders",
            joinColumns = @JoinColumn(name="user_id"),foreignKey = @ForeignKey(name = "FK_USER_ORDERS_USER_ID"),
            inverseJoinColumns = @JoinColumn(name="order_id"),inverseForeignKey = @ForeignKey(name = "FK_USER_ORDERS_ORDER_ID")
    )
    List<Orders> orders;
    @Enumerated(value = EnumType.STRING)
    Role role;

}

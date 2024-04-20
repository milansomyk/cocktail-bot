package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import milansomyk.cocktailbot.Role;

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
    List<Order> orders;
    @Enumerated(value = EnumType.STRING)
    Role role;

}

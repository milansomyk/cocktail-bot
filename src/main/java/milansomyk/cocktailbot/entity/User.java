package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import milansomyk.cocktailbot.Role;
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
    @Enumerated(value = EnumType.STRING)
    Role role;

}

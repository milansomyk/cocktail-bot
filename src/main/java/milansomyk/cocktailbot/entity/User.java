package milansomyk.cocktailbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    @GeneratedValue
    Long id;
    String firstName;
    String lastName;
    String username;
    String lngCode;
    Role role;

}

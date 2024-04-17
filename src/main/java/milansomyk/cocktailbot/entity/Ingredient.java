package milansomyk.cocktailbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLInsert;

import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SQLInsert(sql = "UPDATE ON DUPLICATE KEY")
public class Ingredient {
    @Id
    @GeneratedValue
    Integer id;
    @Column(unique = true)
    String name;
    public Ingredient setNameGetValue(String name){
        this.name = name;
        return this;
    }

}

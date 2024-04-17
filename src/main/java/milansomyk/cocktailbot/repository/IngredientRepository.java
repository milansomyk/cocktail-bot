package milansomyk.cocktailbot.repository;

import jakarta.persistence.EntityManager;
import milansomyk.cocktailbot.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient,Integer> {
    Optional<Ingredient> findByName(String name);
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert INTO ingredient VALUES :ingredients on duplicate key update ingredient.name = ingredient.name")
    void replaceAll(List<Ingredient> ingredients);
    List<Ingredient> findByNameIn(List<String> strings);
}

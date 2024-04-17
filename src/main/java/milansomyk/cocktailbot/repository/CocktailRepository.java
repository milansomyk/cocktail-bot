package milansomyk.cocktailbot.repository;

import milansomyk.cocktailbot.entity.Cocktail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CocktailRepository extends JpaRepository<Cocktail,Integer> {
    Optional<Cocktail> findCocktailByName(String cocktailName);

}

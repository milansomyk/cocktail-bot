package milansomyk.cocktailbot.repository;

import milansomyk.cocktailbot.entity.Cocktail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocktailRepository extends JpaRepository<Cocktail,Integer> {

}

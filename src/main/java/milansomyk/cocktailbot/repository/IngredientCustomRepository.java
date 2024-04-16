package milansomyk.cocktailbot.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IngredientCustomRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public List<Ingredient> replaceAllIngredients(List<Ingredient> ingredients){
        SqlParameterSource parameters = new MapSqlParameterSource("ingredients", ingredients);
        String REPLACE_QUERY = "REPLACE INTO ingredient values :ingredients";
        return namedParameterJdbcTemplate.query(REPLACE_QUERY, parameters, new BeanPropertyRowMapper<>(Ingredient.class));
    }
}

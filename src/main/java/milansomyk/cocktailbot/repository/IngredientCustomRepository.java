package milansomyk.cocktailbot.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

import java.sql.*;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IngredientCustomRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Ingredient> replaceAllIngredients(List<Ingredient> ingredients) {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cocktail_bot?createIfNotExists=true","root","root");
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO ingredient (id,name) " +
                            "VALUES (?,?) " +
                            "ON DUPLICATE KEY UPDATE" +
                            "ingredient.name = ingredient.name");
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1,1);
                ps.setString(2,ingredient.getName());
            }
            int euReturnValue = ps.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return null;
    }

}

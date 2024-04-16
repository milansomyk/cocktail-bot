package milansomyk.cocktailbot.repository;

import milansomyk.cocktailbot.Role;
import milansomyk.cocktailbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findAllByRole(Role role);
}

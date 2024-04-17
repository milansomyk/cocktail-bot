package milansomyk.cocktailbot.repository;

import milansomyk.cocktailbot.Role;
import milansomyk.cocktailbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findAllByRole(Role role);
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE user SET user.role = 'MANAGER'  WHERE user.username IN :usernameList")
    Integer updateUserToManagerByUsername(List<String> usernameList);
}

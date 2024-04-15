package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public User getById(long id){
        User foundUser = null;
        try {
            foundUser = userRepository.findById(id).orElse(null);
        } catch (Exception e){
            log.info("Exception when trying to find User by Id! Error: {}",e.getMessage());
            return null;
        }
        return foundUser;
    }
    public void createUser(User user){
        if(user==null || ObjectUtils.isEmpty(user)) {
            log.info("Exception while creating user! Given user is null:{}",user);
            return;
        }
        try{
            userRepository.save(user);
        }catch (Exception e){
            log.info("Exception while saving user! Error: {}",e.getMessage());
        }
    }
}

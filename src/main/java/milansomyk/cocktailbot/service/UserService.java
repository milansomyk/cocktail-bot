package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.Role;
import milansomyk.cocktailbot.constants.Constants;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.repository.UserRepository;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value = "userService")
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final TelegramClientService telegramClientService;
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
            User save = userRepository.save(user);
            log.info(save.toString());
        }catch (Exception e){
            log.info("Exception while saving user! Error: {}",e.getMessage());
        }
    }
    public List<User> findAllManagers(){
        List<User> managers = new ArrayList<>();
        try {
            managers = userRepository.findAllByRole(Role.MANAGER);
        } catch (Exception e){
            log.error("Exception while trying to find managers!");
        }
        if(managers.isEmpty()){
            log.error("Not found Role.MANAGER users!");
            return null;
        }
        return managers;
    }
}

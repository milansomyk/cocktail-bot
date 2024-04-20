package milansomyk.cocktailbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.cocktailbot.Role;
import milansomyk.cocktailbot.entity.User;
import milansomyk.cocktailbot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service(value = "userService")
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public User getById(long id){
        User foundUser;
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
    public boolean updateUserToManagerByUsername(List<String> usernameList){
        try {
            userRepository.updateUserToManagerByUsername(usernameList);
        }catch (Exception e){
            log.error("Error when trying to update User to Role.MANAGER! Error:{}",e.getMessage());
            return true;
        }
        return false;
    }
    public List<User> findAllUsers(){
        List<User> userList;
        try {
            userList = userRepository.findAll();
        }catch (Exception e){
            log.error("Exception while trying to find all users! Error: {}",e.getMessage());
            return null;
        }
        return userList;
    }
}

package orip.stocks_prediction_system.services;

import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.User;
import orip.stocks_prediction_system.repositories.UserRepo;

@Service
public class UserService 
{
    private UserRepo userRepo;
    /**
     * 
     * @param userRepo injection dependency for User repository
     */
    public UserService(UserRepo userRepo)
    {
        this.userRepo = userRepo;
    }

    /**
     * 
     * @param user
     * @throws Exception 
     */
    public void insertNewUser(User user) throws Exception
    {
        if (userRepo.existsById(user.getUsername()))
            throw new Exception("User allready exist!");
        
        userRepo.insert(user);
    }
}

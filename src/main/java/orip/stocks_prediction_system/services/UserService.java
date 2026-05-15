package orip.stocks_prediction_system.services;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.server.VaadinSession;

import orip.stocks_prediction_system.datamodels.User;
import orip.stocks_prediction_system.repositories.UserRepo;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

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

    public boolean login(String username, String password)
    {
        User user = userRepo.findByUsername(username);
        try
        {
            if(user!=null)
            {
                if(user.getPassword().equals(password))
                { 
                    VaadinSession.getCurrent().setAttribute("loggedInUser", user.getUsername());
                    return true;
                }
               throw new Exception("Incorrect Password");
            }
            throw new Exception("This username does not exist");
        }
        catch (Exception e) 
        {
            UtilsHelper.showNotification(e.getMessage(), 4000, Position.MIDDLE,NotificationVariant.LUMO_ERROR);
            return false;
        }
    }

    public boolean register(String username, String password, String email)
    {
        if(userRepo.findByUsername(username) != null)
            return false;

        User newUser = new User(username, password, email);
        userRepo.save(newUser);
        return true;
    }

    public void logout()
    {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();

    }
}

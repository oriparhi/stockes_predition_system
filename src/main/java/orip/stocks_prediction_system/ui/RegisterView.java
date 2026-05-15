package orip.stocks_prediction_system.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.repositories.UserRepo;
import orip.stocks_prediction_system.services.UserService;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;

@Route(value = "/register",layout = AppNavBarLayout.class)
public class RegisterView extends VerticalLayout
{
    UserService userService;
    TextField entryUser,verifyUser;
    PasswordField entryPassword,verifyPassword;
    EmailField entryEmail;
    
    String username,password,email,verifyUn,verifyPw;

    public RegisterView(UserService userService)
    {
        this.userService = userService;

        setWidthFull();
        setAlignItems(Alignment.CENTER);
        add(new H1("This page was not created yet"));

        entryUser = new TextField("Create username: ");
        entryUser.setClearButtonVisible(true);
        entryUser.setRequiredIndicatorVisible(true);
        entryUser.addValueChangeListener(e -> {
            this.username = e.getValue();
        });
        add(entryUser);

        entryPassword = new PasswordField("Create password: ");
        entryPassword.setClearButtonVisible(true);
        entryPassword.setRequiredIndicatorVisible(true);
        entryPassword.setAutocomplete(Autocomplete.NEW_PASSWORD);
        entryPassword.addValueChangeListener(e -> {
            this.password = e.getValue();
        });
        add(entryPassword);

        entryEmail = new EmailField("Enter email: ");
        entryEmail.setPlaceholder("example@domain.com");
        entryEmail.setRequiredIndicatorVisible(true);
        entryEmail.setClearButtonVisible(true);
        entryEmail.addValueChangeListener(e -> {
            this.email = e.getValue();
            if(userService.register(username, password, email))
                UtilsHelper.showNotification("User added successfully", 2500, Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS);
        });
        add(entryEmail);
        add(new H2("Verification: "));

        verifyUser = new TextField("Enter your username: ");
        verifyUser.setClearButtonVisible(true);
        verifyUser.setRequiredIndicatorVisible(true);
        verifyUser.addValueChangeListener(e -> {
            this.verifyUn= e.getValue();
        });
        add(verifyUser);
        
        verifyPassword = new PasswordField("Enter your password: ");
        verifyPassword.setClearButtonVisible(true);
        verifyPassword.setRequiredIndicatorVisible(true);
        verifyPassword.setAutocomplete(Autocomplete.NEW_PASSWORD);
        verifyPassword.addValueChangeListener(e -> {
            this.verifyPw = e.getValue();
        });
        add(verifyPassword);

        Button signInButton = new Button("Sign In");
        signInButton.getStyle().set("background-color", "#0859b1");
        signInButton.getStyle().set("color", "white");
        signInButton.getStyle().set("padding", "15px 30px");
        signInButton.getStyle().set("font-size", "1.2rem");
        signInButton.addClickListener(e -> {
            if((verifyUn != username))
            {
                if(verifyPw!=password)
                {
                    if(userService.login(verifyUn,verifyPw))
                    {
                        RouteHelper.navigateTo(UploadDataView.class);
                    }
                }
                else {
                    UtilsHelper.showNotification("ERROR: The passwords are not the same", 3000, Position.MIDDLE,NotificationVariant.LUMO_ERROR);
                }
            }
            else{
                UtilsHelper.showNotification("ERROR: The usernames are not the same", 3000, Position.MIDDLE,NotificationVariant.LUMO_ERROR);
            }

        });
        add(signInButton);
    }
}

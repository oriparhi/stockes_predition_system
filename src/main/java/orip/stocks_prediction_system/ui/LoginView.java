package orip.stocks_prediction_system.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.services.UserService;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

@Route(value = "/login",layout = AppNavBarLayout.class)
public class LoginView extends VerticalLayout
{
    private UserService userService;

    public LoginView(UserService userService)
    {
        this.userService = userService;
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        add(new H1("Login View"));

        LoginForm loginForm  = new LoginForm();
        loginForm.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();
            if(userService.login(username, password))
            {
                RouteHelper.navigateTo(UploadDataView.class);
            }
            else
            {
                loginForm.setError(true);
            }
        });
        loginForm.addForgotPasswordListener(e -> {
            UtilsHelper.showNotification("So just remember it", 5000,Position.MIDDLE,NotificationVariant.LUMO_PRIMARY);
        });
        loginForm.setForgotPasswordButtonVisible(false);

        Button registerButton = new Button("Are you new here? ");
        registerButton.getStyle().set("background-color", "#0859b1");
        registerButton.getStyle().set("color", "white");
        registerButton.getStyle().set("padding", "15px 30px");
        registerButton.getStyle().set("font-size", "1.2rem");
        registerButton.addClickListener(e -> {
            RouteHelper.navigateTo(RegisterView.class);
        });

        add(loginForm,registerButton);
    }

}

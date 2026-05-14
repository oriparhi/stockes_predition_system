package orip.stocks_prediction_system.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.services.HomeService;
import orip.stocks_prediction_system.utilities.RouteHelper;

@Route(value = "/",layout = AppNavBarLayout.class)
public class HomeView extends VerticalLayout    
{
    HomeService homeService;
    public HomeView(HomeService homeService) 
    {
        this.homeService = homeService;
        //מרכוז הדף
        setWidthFull();
        setAlignItems(Alignment.CENTER);

        add(new H1("TrendWise"));//System name
        add(new H3("Forcast the patterns behind the future"));

        Image websiteImage = new Image("images\\WebsiteMainPic.png", "websiteImage");
        websiteImage.setMaxWidth("450px");
        websiteImage.setWidth("100%");
        add(websiteImage);

        add(new H4("Made by Ori Parhi - May 2026"),new H4(" "));
        add(new H2("Welcome!!!"));


        HorizontalLayout navigateLayout = new HorizontalLayout();

        Button loginButton = new Button("Login ");
        loginButton.getStyle().set("background-color", "#0859b1");
        loginButton.getStyle().set("color", "white");
        loginButton.getStyle().set("padding", "15px 30px");
        loginButton.getStyle().set("font-size", "1.2rem");
        loginButton.addClickListener(e -> {
            RouteHelper.navigateTo(LoginView.class);
        });

        Button registerButton = new Button("Register");
        registerButton.getStyle().set("background-color", "#0859b1");
        registerButton.getStyle().set("color", "white");
        registerButton.getStyle().set("padding", "15px 30px");
        registerButton.getStyle().set("font-size", "1.2rem");
        registerButton.addClickListener(e -> {
            RouteHelper.navigateTo(RegisterView.class);
        });

        Button guestButton = new Button("Continue as guest");
        guestButton.getStyle().set("background-color", "#0859b1");
        guestButton.getStyle().set("color", "white");
        guestButton.getStyle().set("padding", "15px 30px");
        guestButton.getStyle().set("font-size", "1.2rem");
        guestButton.addClickListener(e -> {
            RouteHelper.navigateTo(UploadDataView.class);
        });
        navigateLayout.add(loginButton,registerButton,guestButton);
        add(navigateLayout);
    }
    
}

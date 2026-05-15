package orip.stocks_prediction_system.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import orip.stocks_prediction_system.services.UserService;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

public class HomeNavbar extends AppLayout
{
    private UserService userService;

    public HomeNavbar(UserService userService) 
    {
        this.userService = userService;
        this.userService = userService;
        // H1 title = new H1("Spring-Demo App");

        HorizontalLayout navbarPanel = new HorizontalLayout(Alignment.BASELINE);
        navbarPanel.setWidthFull();
        navbarPanel.getStyle().setMargin("10px");
        
        navbarPanel.add(new H2("Welcome"));
        navbarPanel.add(new RouterLink("Login Page", LoginView.class));
        navbarPanel.add(" | ");
        navbarPanel.add(new RouterLink("Sign In", RegisterView.class));
        navbarPanel.add(" | ");
        navbarPanel.add(new RouterLink("Upload data", UploadDataView.class));
        navbarPanel.add(" | ");
        
        Span space = new Span(" ");
        navbarPanel.add(space);
        navbarPanel.expand(space);

        Button logout = new Button("Logout");
        logout.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logout.addClickListener(e -> {
            if((String) VaadinSession.getCurrent().getAttribute("loggedInUser")!= null)
            {
                userService.logout();
                RouteHelper.navigateTo(HomeView.class);
            }
            else
                UtilsHelper.showNotification("You are not loged in yet", 2000,Position.TOP_CENTER,NotificationVariant.LUMO_WARNING);
        });
        navbarPanel.add(logout);
        
        HorizontalLayout DateTimePanel = new HorizontalLayout(Alignment.BASELINE);
        Span date = new Span("Date: "+LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+" |");
        date.getStyle().set("font-weight", "bold");
        Span time = new Span("Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        time.getStyle().set("font-weight", "bold");
        DateTimePanel.add(date,time);

        // יצירת כפתור להחלפת מצב תצוגה (Theme)
        Button themeToggleButton = new Button(new Icon(VaadinIcon.MOON));
        themeToggleButton.addClickListener(click -> {
            var themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                // חזרה למצב בהיר
                themeList.remove(Lumo.DARK);
                themeToggleButton.setIcon(new Icon(VaadinIcon.MOON));
            } else {
                // מעבר למצב כהה
                themeList.add(Lumo.DARK);
                themeToggleButton.setIcon(new Icon(VaadinIcon.SUN_O));
            }
        });

        Avatar userAvatar = new Avatar("Ori Parhi","https://images.icon-icons.com/1879/PNG/512/iconfinder-7-avatar-2754582_120519.png");
        userAvatar.getStyle().setMargin("0px");
        userAvatar.getStyle().setMarginTop("10px");

        String username;
        username = (String) VaadinSession.getCurrent().getAttribute("loggedInUser");
        if (username == null)
            username = "Guest";
        H4 info = new H4("Username: "+username);


        navbarPanel.add(DateTimePanel,themeToggleButton,info,userAvatar);
        //addToNavbar(navbarPanel,info);
        addToNavbar(navbarPanel);
    }
        
}

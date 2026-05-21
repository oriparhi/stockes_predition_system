package orip.stocks_prediction_system.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import orip.stocks_prediction_system.services.UserService;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

public class AppNavBarLayout extends AppLayout
{
    private UserService userService;
    private String username;

    private Span timeSpan;
    private Thread clockThread;
    private volatile boolean isRunning = false;// כותב ישירות לRAM כדי שלא תהיה חוסר תאימות בין הליבות

    public AppNavBarLayout(UserService userService)
    {
        this.userService = userService;
        username = (String) VaadinSession.getCurrent().getAttribute("loggedInUser");
        // H1 title = new H1("Spring-Demo App");

        HorizontalLayout navbarPanel = new HorizontalLayout(Alignment.BASELINE);
        navbarPanel.setWidthFull();
        navbarPanel.getStyle().setMargin("10px");
        
        navbarPanel.add(new H2("TrendWise"));
        navbarPanel.add(new RouterLink("Home page", HomeView.class));
        navbarPanel.add(" | ");
        navbarPanel.add(new RouterLink("Login Page", LoginView.class));
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
        if(username==null)
            logout.setVisible(false);
        navbarPanel.add(logout);
        
        HorizontalLayout DateTimePanel = new HorizontalLayout(Alignment.BASELINE);
        Span date = new Span("Date: "+LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+" |");
        date.getStyle().set("font-weight", "bold");
        
        timeSpan = new Span("Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeSpan.getStyle().set("font-weight", "bold");
        DateTimePanel.add(date,timeSpan);

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

        
        if (username == null)
            username = "Guest";
        H4 info = new H4("Username: "+username);


        navbarPanel.add(DateTimePanel,themeToggleButton,info,userAvatar);
        //addToNavbar(navbarPanel,info);
        addToNavbar(navbarPanel);
    }
    @Override
    // פונקציה המופעלת אוטומטית רק כאשר הרכיב מתחבר לדפדפן של המשתמש
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        isRunning = true;
        clockThread = new Thread(()-> {
            while (isRunning) 
            {
                try {
                    Thread.sleep(1000);

                    ui.access(()->{
                        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        timeSpan.setText("Time: "+ currentTime);
                    });
                } catch (Exception e) 
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }


            }
        });
        clockThread.setDaemon(true);//מגדיר את זה כתוכנית רקע ובעצם הורג את זה כאשר השרת נכבה
        clockThread.start();
    }
    //פונקציה המופעלת אוטומטית ברגע שהרכיב מוסר מן המסך ומטרתה היא לנקות את הרכיב והתהליכים שלו מהזיכרון
    @Override
    protected void onDetach(DetachEvent detachEvent) 
    {
        isRunning = false;
        if(clockThread != null)
            clockThread.interrupt();
    }


}

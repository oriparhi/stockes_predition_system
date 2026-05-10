package orip.stocks_prediction_system.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.RouterLink;

public class AppNavBarLayout extends AppLayout
{
    public AppNavBarLayout()
    {
        // H1 title = new H1("Spring-Demo App");

        HorizontalLayout navbarPanel = new HorizontalLayout(Alignment.BASELINE);
        navbarPanel.setWidthFull();
        navbarPanel.getStyle().setMargin("10px");
        
        navbarPanel.add(new H2("Spring-Demo App"));
        navbarPanel.add(new RouterLink("Login Page", UserView.class));
        navbarPanel.add(" | ");
        navbarPanel.add(new RouterLink("Upload data", UploadDataView.class));
        navbarPanel.add(" | ");
        
        Span space = new Span(" ");
        navbarPanel.add(space);
        navbarPanel.expand(space);
        
        HorizontalLayout DateTimePanel = new HorizontalLayout(Alignment.BASELINE);
        String date = "Date: "+LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String time = "Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        DateTimePanel.add(date+" | "+time);

        Avatar userAvatar = new Avatar("Ori Parhi","https://images.icon-icons.com/1879/PNG/512/iconfinder-7-avatar-2754582_120519.png");
        userAvatar.getStyle().setMargin("0px");
        userAvatar.getStyle().setMarginTop("10px");

        // String SessionId = SessionHelper.getSessionID();
        // User user = (User)SessionHelper.getAttribute("USER");
        // String username = null;
        // if(user!=null);
        //     username = user.getUsername();
        // H4 info = new H4("Username:"+username+"  SessionID: "+SessionId);


        navbarPanel.add(DateTimePanel,userAvatar);
        //addToNavbar(navbarPanel,info);
        addToNavbar(navbarPanel);
    }
}

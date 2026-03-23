package orip.stocks_prediction_system.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.datamodels.User;
import orip.stocks_prediction_system.services.UserService;

@Route(value = "/",layout = AppNavBarLayout.class)
public class UserView extends VerticalLayout
{
    private UserService userService;
    private Button btnInsert;
    private TextField txfUN;
    private TextField txfPW;

    public UserView(UserService userService)
    {
        this.userService = userService;
        add(new H1("User View"));
        HorizontalLayout layout = new HorizontalLayout(Alignment.BASELINE);
        layout.add(txfUN = new TextField("Username"));
        layout.add(txfPW = new TextField("password"));
        layout.add(btnInsert = new Button("Insert User"));
        btnInsert.addClickListener(clickEvent -> insertUserToDB(txfUN.getValue(), txfPW.getValue()));
        add(layout);
    }

    private void insertUserToDB(String un, String pw)
    {
        // validation check
        if(un==null || pw==null ||un.length()<6)
            return;
        try {
            userService.insertNewUser(new User(un, pw));
            Notification.show("User inserted OK", 3000, Position.MIDDLE);
        } catch (Exception e) {
            
            e.printStackTrace();
            // alert user by notification for this error
            Notification.show("User NOT inserted "+e.getMessage(), 5000, Position.MIDDLE);
        }
    }
}

package orip.stocks_prediction_system.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H1;

@Route(value = "/register",layout = AppNavBarLayout.class)
public class RegisterView extends VerticalLayout
{
    public RegisterView()
    {
        add(new H1("This page was not created yet"));
    }
}

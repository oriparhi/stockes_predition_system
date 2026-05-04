package orip.stocks_prediction_system.ui;

import java.time.LocalDateTime;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.services.ShowResultService;

@Route(value = "/showResults", layout = AppNavBarLayout.class)
public class ShowResultView extends VerticalLayout implements HasUrlParameter<String>
{
    private final ShowResultService showResultService;
    private String forcastResultsId;
    
    public ShowResultView(ShowResultService showResultService) 
    {
        this.showResultService = showResultService;


        add(new H1("Forcast Results"));
        add(LocalDateTime.now().toString());
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) 
    {
        this.forcastResultsId = parameter;
    }
     
}

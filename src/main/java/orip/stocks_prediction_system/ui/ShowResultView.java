package orip.stocks_prediction_system.ui;

import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.services.ShowResultService;

@Route(value = "/showResults", layout = AppNavBarLayout.class)
public class ShowResultView extends VerticalLayout implements HasUrlParameter<String>
{
    private final ShowResultService showResultService;
    
    private String forcastResultsId;
    private Grid<DataPoints> resultTable;
    
    public ShowResultView(ShowResultService showResultService) 
    {
        this.showResultService = showResultService;


        add(new H1("Forcast Results"));
        add(LocalDateTime.now().toString());

        // יצירת הרשימה להצגת התוצאות
        
        resultTable = new Grid<>(DataPoints.class, false);
        resultTable.addColumn(DataPoints::getIndex)
            .setHeader("index")// Table title
            .setSortable(true)// Alow sorting by pressing on the title
            .setAutoWidth(true); //מתאים את תוכן העמודה לתוכן

        resultTable.addColumn(DataPoints::getValue)
            .setHeader("Value")
            .setSortable(true)
            .setFlexGrow(1);

        ForcastResult forcastResult = showResultService.loadForcast(forcastResultsId);
        List<DataPoints> forcastList = forcastResult.getForcastResults();
        
        resultTable.setItems(forcastList);
        resultTable.setWidthFull();
        add(new H3("The results of the prediction in a table"),resultTable);


    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) 
    {
        this.forcastResultsId = parameter;

        addResultsToTheTable();
    }

    private void addResultsToTheTable() 
    {
        ForcastResult forcastResult = showResultService.loadForcast(forcastResultsId);
        
        if (forcastResult != null && forcastResult.getForcastResults() != null) 
        {
            List<DataPoints> forcastList = forcastResult.getForcastResults();
            resultTable.setItems(forcastList);
        } 
        else 
        {
            // יצירת נוטיפיקציית שגיאה
            Notification errorNotification = Notification.show(
                    "Error: Could not find forecast results for the provided ID.", 
                    5000, // משך הזמן שההודעה תוצג באלפיות שנייה (5 שניות)
                    Notification.Position.MIDDLE // מיקום ההודעה במסך
            );
            // הוספת צבע אדום של שגיאה
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
     
}

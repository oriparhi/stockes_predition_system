package orip.stocks_prediction_system.ui;

import java.util.List;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DashStyle;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.Marker;
import com.vaadin.flow.component.charts.model.PlotLine;
import com.vaadin.flow.component.charts.model.PlotOptionsLine;
import com.vaadin.flow.component.charts.model.PlotOptionsScatter;
import com.vaadin.flow.component.charts.model.style.Color;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.services.ShowResultService;
import orip.stocks_prediction_system.utilities.Interval;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.UtilsHelper;

@Route(value = "/showResults", layout = AppNavBarLayout.class)
public class ShowResultView extends VerticalLayout implements HasUrlParameter<String>
{
    private final ShowResultService showResultService;
    
    private String forcastResultsId;
    private Grid<DataPoints> resultTable;

    private Chart resultsChart;
    private Configuration resultsChartsConf;
    private ForcastResult forcastResult;
    private List<DataPoints> historicalData;
    private List<DataPoints> forcastList;
    private DataSeries forecastSeries;
    private DataSeries actualSeries;

    private Text indexText;
    private Text valueText; 

    private H3 userAndDate;
    private H3 predictionDetails;
    private H3 algorithmAndMse;
    
    public ShowResultView(ShowResultService showResultService) 
    {
        this.showResultService = showResultService;
        this.userAndDate =new H3(" ");
        this.predictionDetails = new H3(" ");
        this.algorithmAndMse = new H3(" ");
        this.indexText = new Text("Index");
        this.valueText = new Text("Value");

        this.setHeight(null);
        this.setPadding(true);
        this.setSpacing(true);

        add(new H1("Forcast Results"));
        add(userAndDate,predictionDetails,algorithmAndMse);
        

        //יצירת גרפים לתצוגה ברורה יותר של התוצאות
        resultsChart = new Chart(ChartType.LINE);
        resultsChartsConf = resultsChart.getConfiguration();
        
        // indexText.setText("Index");
        // valueText.setText("Value");
        resultsChartsConf.setTitle("Results Graph");
        resultsChartsConf.getxAxis().setTitle("Index");
        resultsChartsConf.getyAxis().setTitle("Value");

        resultsChart.setHeight("500px");
        resultsChart.setWidthFull();

        //TODO:
        forecastSeries = new DataSeries("Forecast Line");
        actualSeries = new DataSeries("Actual Data"); 
        // forcasts = new DataSeries("Forecast results");
        // resultsChartsConf.addSeries(forcasts);
        resultsChartsConf.addSeries(actualSeries);
        resultsChartsConf.addSeries(forecastSeries);
        add(resultsChart);

        H4 graphLegendTitle = new H4("Results Graph Legend: ");
        Paragraph graphLegend = new Paragraph();

        Span timeLabel = new Span("Time Index (X-axis): ");
        timeLabel.getStyle().set("font-weight", "bold");
        graphLegend.add(timeLabel);
        graphLegend.add(new Text("The horizontal axis represents the Index, which serves as a sequential counter for fixed, equidistant time intervals. Depending on the dataset configuration, each integer (e.g., 1, 2, 3) corresponds to one discrete unit of time, such as a single day or a specific month, starting from the baseline (0). This index allows for a standardized chronological view of the series regardless of the specific date format."));
        graphLegend.add(new HtmlComponent("br"));
        Span valueLabel = new Span("2. Value (Y-axis): ");
        valueLabel.getStyle().set("font-weight", "bold");
        graphLegend.add(valueLabel);
        graphLegend.add(new Text("The vertical axis represents the Value of the measured metric. This is the quantitative data point recorded or predicted for each corresponding time index."));
        graphLegend.add(new HtmlComponent("br"));
        Span forcastLineLabel = new Span("3. Forecast Results (Black Line): ");
        forcastLineLabel.getStyle().set("font-weight", "bold");
        graphLegend.add(forcastLineLabel);
        graphLegend.add(new Text("The plotted blue line with circular markers represents the data series. Points located to the left of the vertical dashed line represent Historical Observations (actual data), while points to the right of the line represent the Forecasted Values generated by the model's predictive algorithm."));
        graphLegend.add(new HtmlComponent("br"));
        Span actualDataLabel = new Span("4. Actual Data (Blue points): ");
        actualDataLabel.getStyle().set("font-weight", "bold");
        graphLegend.add(actualDataLabel);
        graphLegend.add(new Text("Those points on the graph before the red line are the actual data that you have enterd before the prediction was done on it. The goal of those points is to give you the ability to compare with your eyes between the forcast results to the actual data, in purpose of to show you reliable the forcast model is not just by the MSE number but with your own eyes."));
        graphLegend.add(new HtmlComponent("br"));
        Span redLineLabel = new Span("5. Forecast Origin (Red Dashed Line): ");
        redLineLabel.getStyle().set("font-weight", "bold");
        graphLegend.add(redLineLabel);
        graphLegend.add(new Text("The vertical red dashed line marks the Point of Origin. It serves as the boundary between the past and the future. Everything to the left of this line is known historical data used to train the model, while everything to the right indicates the Forecast Horizon, illustrating the projected trend based on mathematical modeling."));
        graphLegend.add(new HtmlComponent("br"));
        add(graphLegendTitle,graphLegend);



        add(graphLegendTitle,graphLegend);

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

        
        resultTable.setWidthFull();
        resultTable.setHeight("500px");
        add(new H3("The results of the prediction in a table"),resultTable);

        
        HorizontalLayout pageButtonsLayout = new HorizontalLayout();
        Button newPrediction = new Button("Create new prediction");
        newPrediction.addClickListener(clickEvent -> {
            RouteHelper.navigateTo("/upload");
        });
        add(newPrediction);
        //allow scrolling if needed
        this.getStyle().set("overflow-y", "auto");
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) 
    {
        this.forcastResultsId = parameter;

        this.forcastResult = showResultService.loadForcast(forcastResultsId);
        if (forcastResult != null && forcastResult.getForcastResults() != null) 
        {
            this.forcastList = forcastResult.getForcastResults();
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
            return;
        }
        this.historicalData = showResultService.getPastData(forcastResult);

        userAndDate.setText("This is the result of the prediction made by "+forcastResult.getCreatedBy()+" at "+forcastResult.getResultDate());
        predictionDetails.setText("The result is the forcast of "+showResultService.getDataName(forcastResult)+
        " to the next "+showResultService.getPredictionHorizon(forcastResult)+" in resolution of "+showResultService.getInterval(forcastResult)
        +" Based on the last "+showResultService.getDataSize(forcastResult)+" observations."); 
        algorithmAndMse.setText("The aglorithem that was used is " +forcastResult.getAlgorithemUsed()+" which his MSE is "+forcastResult.getMse());;

        Interval indexInterval = showResultService.getInterval(forcastResult);
        if(indexInterval == null)
        {
            Notification errorNotification = Notification.show(
                    "ERROR: There is no interval", 
                    5000, // משך הזמן שההודעה תוצג באלפיות שנייה (5 שניות)
                    Notification.Position.MIDDLE // מיקום ההודעה במסך
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        indexText.setText(indexInterval.getApiValue());
        String valueTitle = showResultService.getTitle(forcastResult);
        if(valueTitle==null)
        {
            Notification errorNotification = Notification.show(
                    "ERROR: There is no title", 
                    5000, // משך הזמן שההודעה תוצג באלפיות שנייה (5 שניות)
                    Notification.Position.MIDDLE // מיקום ההודעה במסך
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        valueText.setText(valueTitle);
        addResultsToTheTable();
        addResultsToTheChart(indexInterval,valueTitle);
    }

    private void addResultsToTheChart(Interval indexInterval, String valueTitle) 
    {
        resultsChartsConf.getxAxis().setTitle(resultsChartsConf.getxAxis().getTitle().getText()+" ("+indexInterval.getApiValue()+") ");
        resultsChartsConf.getyAxis().setTitle(resultsChartsConf.getyAxis().getTitle().getText()+" ("+valueTitle+") ");
        
        // The original Data - the blue points at the Excel's graph
        // DataSeries actualSeries = new DataSeries("Actual Data");
        System.out.println("Size of Historical Data: " + historicalData.size());
        System.out.println("Size of Forecast List: " + forcastList.size());
        PlotOptionsScatter scatterOptions = new PlotOptionsScatter();
        scatterOptions.setColor(SolidColor.BLACK);
        actualSeries.setPlotOptions(scatterOptions);
        if(historicalData != null)
        {
            for(DataPoints dp : historicalData)
            {
                actualSeries.add(new DataSeriesItem(dp.getIndex(), dp.getValue()));
            }
        }

        //The forcast Series
        // DataSeries forecastSeries = new DataSeries("Forecast Line");
        PlotOptionsLine lineOptions = new PlotOptionsLine();
        //lineOptions.setDashStyle(DashStyle.DOT); // הופך את הקו למקווקו (כמו באקסל)
        lineOptions.setColor(SolidColor.BLUE);

        Marker lineMarker = new Marker();
        lineMarker.setEnabled(false);
        lineOptions.setMarker(lineMarker);

        forecastSeries.setPlotOptions(lineOptions);
        for(DataPoints dp : forcastList)
        {
                DataSeriesItem item = new DataSeriesItem(dp.getIndex(), dp.getValue());
                forecastSeries.add(item);
                
                //forcasts.add(item);
        }
        // resultsChartsConf.addSeries(actualSeries);
        // resultsChartsConf.addSeries(forecastSeries);

        //יצירת הקו המסמן מאיפה מתחילה התחזית
        // int predictionHorizon = showResultService.getPredictionHorizon(forcastResult);
        // double predictionIndex = forcastList.size()-predictionHorizon+0.5;
        double predictionIndex = historicalData.size()+0.35;

        PlotLine forcastLine = new PlotLine();
        forcastLine.setValue(predictionIndex);
        forcastLine.setColor(SolidColor.RED);
        forcastLine.setWidth(2);
        forcastLine.setDashStyle(DashStyle.DASH);
        resultsChartsConf.getxAxis().addPlotLine(forcastLine);

    }

    private void addResultsToTheTable() 
    {
        resultTable.setItems(forcastList);
    }
     
}

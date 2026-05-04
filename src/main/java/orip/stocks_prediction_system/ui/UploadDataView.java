package orip.stocks_prediction_system.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.IntegerField.IntegerFieldI18n;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.services.ApiReaderService;
import orip.stocks_prediction_system.services.CsvReaderService;
import orip.stocks_prediction_system.services.ForcastingService;
import orip.stocks_prediction_system.utilities.Interval;

@Route(value = "/upload",layout = AppNavBarLayout.class)
public class UploadDataView extends VerticalLayout
{
    private final CsvReaderService csvReaderService;
    private final ApiReaderService apiReaderService;
    private final ForcastingService forcastingService;
    private String username;
    private String WelcomeMsg;
    private H1 title;
    private VerticalLayout uploadCsv;
    private VerticalLayout uploadApi;
    private Upload upload;
    private TextField StockSymbolField;
    private IntegerField outputSizeEntryField;
    private ComboBox<Interval> intervalComboBox;
    

    private LocalDateTime time;
    List<DataPoints> parsedData;
    private String newTimeSeriesId;
    private String symbol;
    private int outputsize;
    private Interval interval;
    List<DataPoints> apiData;
    private boolean isItSeasonality;
    private int seasonalityPeriod;

    private String algorithem;
    private int predictionHorizon;

    public UploadDataView(CsvReaderService csvReaderService, ApiReaderService apiReaderService,ForcastingService forcastingService) 
    {
        this.apiReaderService = apiReaderService;
        this.csvReaderService = csvReaderService;
        this.forcastingService = forcastingService;
        this.isItSeasonality = false;

        this.username = "Guest";
        WelcomeMsg = "Hello "+username;
        title = new H1(WelcomeMsg);
        time = LocalDateTime.now();

        add(title);
        add(new H2(time.toString()));
        add(new H3("Please enter here that data that you want to upload!"));
        add(new H3("You can do that by upload your CSV or by choosing a stock you want to forcast"));
        

        HorizontalLayout uploadData = new HorizontalLayout();// הפאנל שיאחסן את העלה של הCSV והAPI
        uploadData.setWidthFull();
        uploadData.getStyle().set("border", "2px solid black"); // צבע עובי וסוג הקו
        uploadData.getStyle().set("border-radius", "10px"); // עושה פינות מעוגלות למסגרת
        uploadData.getStyle().set("padding", "20px"); // מוסיף קצת רווח פנימי כדי שהתוכן לא ייצמד לקווים

        uploadApi = new VerticalLayout();// פאנל העלה של הAPI
        uploadCsv = new VerticalLayout();// פאנל העלה של הCSV
        // התחלת עבודה על הפאנל העלה של הCSV
        
        uploadCsv.setWidth("50%");
        uploadCsv.getStyle().set("border-right", "2px solid black"); 
        uploadCsv.getStyle().set("padding-right", "20px");
        

        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory((metadata,data) ->{

            String fileName = metadata.fileName();
            Notification notify = new Notification("", 5000, Position.TOP_CENTER);
            try 
            {
              parsedData = csvReaderService.ReadCsv(new ByteArrayInputStream(data), fileName);
              if(parsedData!=null)
              {
                newTimeSeriesId = csvReaderService.CreateNewTimeSeries(
                    this.username,
                    LocalDateTime.now(),
                    parsedData,
                    fileName
                );
                notify.setText("Csv was uploaded successfully");
                notify.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
              }
              else{
                throw new Exception("There is no data");
              }
                
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                String notificationText = "There was a problem with the data uploading!\n "+e;
                notify.setText(notificationText);
                notify.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }); 

        upload = new Upload(inMemoryHandler);
        upload.setAcceptedFileTypes(".csv");
        upload.addSucceededListener(event ->{ 
            uploadApi.setEnabled(false);
            uploadApi.getStyle().set("opacity", "0.5");
        });

        H4 attentionPlease = new H4("Please pay attention that your CSV must have 1 column only!");
        attentionPlease.getStyle().set("color", "red");
        attentionPlease.getStyle().set("font-weight", "bold");

        uploadCsv.add(new H2("Enter here your csv,"),attentionPlease,upload);

        uploadData.add(uploadCsv);
        // סוף עבודה על הפאנל העלה של הCSV

        // תחילת עבודה על הפאנל העלאה של הAPI
        //TODO: Connect the API frontend to the api service
        uploadApi.setWidth("50%");
        uploadApi.getStyle().set("padding-left", "20px");

        uploadApi.add(new H2("Enter here the stock's symbol/ticker"));
        StockSymbolField = new TextField("Stock Symbol", e -> symbol = e.getValue());
        StockSymbolField.setMinLength(1);
        StockSymbolField.setMaxLength(5);
        StockSymbolField.addValueChangeListener(e -> updateCsvStatus());
        uploadApi.add(StockSymbolField);

        outputSizeEntryField = new IntegerField("Output size",e -> outputsize = e.getValue());
        outputSizeEntryField.setRequiredIndicatorVisible(true);
        outputSizeEntryField.setMin(1);
        outputSizeEntryField.setMax(5000);
        outputSizeEntryField.setStepButtonsVisible(true);
        outputSizeEntryField.addValueChangeListener(e -> updateCsvStatus());
        outputSizeEntryField.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format")
            .setMinErrorMessage("the output size must be at list 1")
            .setMaxErrorMessage("The output size must be 5000 maximum"));
        uploadApi.add(outputSizeEntryField);

        intervalComboBox = new ComboBox<>("Choose the time interval",e -> interval = e.getValue());
        intervalComboBox.setItems(Interval.values());
        intervalComboBox.addValueChangeListener(e -> {
            updateCsvStatus();
            this.interval = e.getValue();
        });
        uploadApi.add(intervalComboBox);

        //TODO: לשחרר את הערות של העשר שורות הבאות שבהערה רק כאשר אני יוצר את הממשק של הטיפול בAPI
        // apiData = ApiReaderService.callApi(symbol,interval,outputsize);
        // if(apiData!=null)
        // {
        //     newTimeSeriesId = ApiReaderService.CreateNewTimeSeries(
        //     this.username,
        //     LocalDateTime.now(),
        //     apiData,
        //     symbol
        //     );
        // }

        uploadData.add(uploadApi);
        // סוף עבודה על הפאנל העלאה של הAPI

        add(uploadData);
        
        // Is it seasonality panel
        HorizontalLayout seasonalityPanel = new HorizontalLayout();
        IntegerField seasonalityPeriodEntry = new IntegerField("Seasonality Period");
        seasonalityPeriodEntry.setEnabled(false);

        // 1. יצירת ה-Checkbox ללא טקסט בבנאי
        Checkbox isItSeasonalCheckbox = new Checkbox();

        // 2. יצירת ה-Span עם הטקסט ועיצובו 
        Span checkboxLabel = new Span("Is the data you entered seasonal?");
        checkboxLabel.getStyle()
            .set("font-weight", "bold")
            .set("font-size", "1.5rem") // 
            .set("cursor", "pointer"); // הופך את הסמן ליד מעל הטקסט

        // 3. הוספת הלוגיקה ל-Checkbox 
        isItSeasonalCheckbox.addValueChangeListener(e -> {
            boolean isItSeasonality = e.getValue();
            forcastingService.setItSeasonality(newTimeSeriesId ,isItSeasonality);
            seasonalityPeriodEntry.setEnabled(isItSeasonality);
            
            if (!isItSeasonality) {
                seasonalityPeriodEntry.clear();
            }
        });

        // 4. אופציונלי: חיבור הלחיצה על הטקסט לסימון התיבה (שיפור חווית משתמש)
        checkboxLabel.getElement().addEventListener("click", e -> {
            isItSeasonalCheckbox.setValue(!isItSeasonalCheckbox.getValue());
        });

        // הגדרות ה-IntegerField 
        seasonalityPeriodEntry.setRequiredIndicatorVisible(true);
        seasonalityPeriodEntry.setMin(1);
        if(parsedData != null)
            seasonalityPeriodEntry.setMax((parsedData.size()) / 2);
        else if (apiData != null)
            seasonalityPeriodEntry.setMax(apiData.size() / 2);

        seasonalityPeriodEntry.setStepButtonsVisible(true);
        seasonalityPeriodEntry.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format"));

        seasonalityPeriodEntry.addValueChangeListener(e -> { 
            Integer seasonalityPeriod = e.getValue();
            forcastingService.setSeasonalityPeriod(newTimeSeriesId, seasonalityPeriod);
        });

        // 5. סידור מחדש בתוך הפאנל: הוספת התיבה, אחריה הטקסט, ואז שדה המספר
        seasonalityPanel.add(isItSeasonalCheckbox, checkboxLabel, seasonalityPeriodEntry);

        // יישור כל האלמנטים בפאנל לאמצע מבחינה אנכית
        seasonalityPanel.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, isItSeasonalCheckbox, checkboxLabel);

        add(seasonalityPanel);

        add(new H2("New forcast"));
        
        ComboBox<String> chooseAlgorithem = new ComboBox<>("Choose the algorithem you want to use for the forcasting: ");
        chooseAlgorithem.setItems("Average","Exponential Smoothing","Linear Reagression","Moving Average","Holt Winters","The best algorithem");
        chooseAlgorithem.addValueChangeListener(e -> {
            algorithem = e.getValue();
        });
        chooseAlgorithem.setValue("The best algorithem");

        IntegerField horizonEnteryField = new IntegerField("How much predictions do you want to get? ");
        horizonEnteryField.setRequiredIndicatorVisible(true);
        horizonEnteryField.setMin(1);
        horizonEnteryField.setStepButtonsVisible(true);
        horizonEnteryField.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format"));
        horizonEnteryField.addValueChangeListener(e -> {
            this.predictionHorizon = e.getValue();
        });
        HorizontalLayout newForcastPanel =new HorizontalLayout(chooseAlgorithem,horizonEnteryField);
        add(newForcastPanel);

        Button predictButton = new Button("Predict");
        predictButton.getStyle().set("background-color", "#0859b1");
        predictButton.getStyle().set("color", "white");
        predictButton.getStyle().set("padding", "15px 30px");
        predictButton.getStyle().set("font-size", "1.2rem");
        predictButton.addClickListener(clickEvent -> predictButtonClicked());
        add(predictButton);

        //allow scrolling if needed
        this.setSizeFull();
        this.getStyle().set("overflow-y", "auto");
        this.setPadding(true);
    }
    
    public void predictButtonClicked() 
    {
        // 1. וולידציה - מוודאים שכל הנתונים קיימים
        if (newTimeSeriesId == null || newTimeSeriesId.isEmpty()) {
            Notification.show("Please upload a CSV or fetch API data first.", 4000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return; // עוצר את הפונקציה
        }
        if (predictionHorizon <= 0) {
            Notification.show("Please enter a valid prediction horizon.", 4000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (algorithem == null || algorithem.isEmpty()) {
            Notification.show("Please select an algorithm.", 4000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        try
        {
            ForcastResult forcastResult = forcastingService.CreateNewForcast(newTimeSeriesId, predictionHorizon, algorithem, this.username, LocalDateTime.now());
            if (forcastResult != null && forcastResult.getId() != null) 
            {
                Notification.show("Forecast generated successfully!", 2000, Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate(ShowResultView.class,forcastResult.getId()));

            }
            else
            {
                Notification.show("Failed to generate forecast. Please try again.", 4000, Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);

            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            Notification.show("An error occurred during forecasting: " + e.getMessage(), 5000, Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * This function cancells the option to enter data to the Csv upload data panel
     * right after a data was enterd into the api panel
     */
    private void updateCsvStatus() 
    {
    boolean anyApiFieldFilled = (StockSymbolField.getValue() != null && !StockSymbolField.getValue().isEmpty()) ||
                                 (outputSizeEntryField.getValue() != null) ||
                                 (intervalComboBox.getValue() != null);
    
    // אם המשתמש התחיל למלא API, נבטל את האפשרות להעלות CSV
    upload.setEnabled(!anyApiFieldFilled);
    uploadCsv.getElement().getStyle().set("opacity", anyApiFieldFilled ? "0.5" : "1.0");
    }
}

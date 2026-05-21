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
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.theme.lumo.LumoIcon;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.services.ApiReaderService;
import orip.stocks_prediction_system.services.CsvReaderService;
import orip.stocks_prediction_system.services.ForcastingService;
import orip.stocks_prediction_system.utilities.Interval;
import orip.stocks_prediction_system.utilities.ReadCsvResponse;
import orip.stocks_prediction_system.utilities.RouteHelper;
import orip.stocks_prediction_system.utilities.TopStocks;
import orip.stocks_prediction_system.utilities.UtilsHelper;

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
    private ComboBox<String> stocksComboBox;
    private IntegerField outputSizeEntryField;
    private ComboBox<Interval> apiIntervalComboBox;
    private ComboBox<Interval> csvIntervalComboBox;
    

    private LocalDateTime time;
    List<DataPoints> parsedData;
    private String newTimeSeriesId;
    private String symbol;
    private int outputsize;
    private Interval csvInterval;
    private Interval apiInterval;
    List<DataPoints> apiData;
    private boolean isItSeasonality;
    private int seasonalityPeriod;

    private String algorithem;
    private int predictionHorizon;
    private String csvTitle;

    public UploadDataView(CsvReaderService csvReaderService, ApiReaderService apiReaderService,ForcastingService forcastingService) 
    {
        this.apiReaderService = apiReaderService;
        this.csvReaderService = csvReaderService;
        this.forcastingService = forcastingService;
        this.isItSeasonality = false;


        this.username = (String) VaadinSession.getCurrent().getAttribute("loggedInUser");
        if (username == null)
            username = "Guest";
        WelcomeMsg = "Hello "+username;
        title = new H1(WelcomeMsg);
        time = LocalDateTime.now();

        add(title);
        // add(new H2(time.toString()));
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
        
        csvIntervalComboBox = new ComboBox<>("Choose the time interval");
        csvIntervalComboBox.setItems(Interval.values());
        csvIntervalComboBox.setRequiredIndicatorVisible(true);
        csvIntervalComboBox.setValue(Interval.DAY_1);
        this.csvInterval = csvIntervalComboBox.getValue();
        //csvIntervalComboBox.setValue(Interval.DAY_1);
        // ברגע שמשנים את הערך, בודקים אם אפשר לאפשר את העלאת הקובץ
        csvIntervalComboBox.addValueChangeListener(e -> {
            this.csvInterval = e.getValue();
            updateCsvStatus(); 
        });

        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory((metadata,data) ->{

            String fileName = metadata.fileName();
            Notification notify = new Notification("", 5000, Position.TOP_CENTER);
            try 
            {
                ReadCsvResponse res = csvReaderService.ReadCsv(new ByteArrayInputStream(data), fileName);
                parsedData = res.data();
                csvTitle = res.title();
                if(parsedData!=null)
                {
                    newTimeSeriesId = csvReaderService.CreateNewTimeSeries(
                        this.username,
                        LocalDateTime.now(),
                        parsedData,
                        fileName,
                        csvTitle,
                        this.csvInterval
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
        //upload.setEnabled(false);
        upload.addSucceededListener(event ->{ 
            uploadApi.setEnabled(false);
            uploadApi.getStyle().set("opacity", "0.5");
        });

        H4 attentionPlease = new H4("Please pay attention that your CSV must have 1 column only!");
        attentionPlease.getStyle().set("color", "red");
        attentionPlease.getStyle().set("font-weight", "bold");

        uploadCsv.add(new H2("Enter here your csv,"),attentionPlease,csvIntervalComboBox,upload);

        uploadData.add(uploadCsv);
        // סוף עבודה על הפאנל העלה של הCSV

        // תחילת עבודה על הפאנל העלאה של הAPI
        //TODO: Connect the API frontend to the api service
        uploadApi.setWidth("50%");
        uploadApi.getStyle().set("padding-left", "20px");

        uploadApi.add(new H2("Enter here the stock's symbol/ticker"));
        stocksComboBox = new ComboBox<>("Stock Symbol");
        stocksComboBox.setItems(TopStocks.getAllTickers());
        stocksComboBox.setValue(TopStocks.AAPL.getApiValue());//עדכון ערך דיפולטיבי
        this.symbol = TopStocks.AAPL.getApiValue();
        stocksComboBox.setAllowCustomValue(true);
        stocksComboBox.addCustomValueSetListener(e -> {
            String customValue = e.getDetail().toUpperCase().trim();
            if ((!customValue.isEmpty())&&((customValue.length() >= 1 && customValue.length() <= 5))) 
            {
                stocksComboBox.setValue(customValue); // זה יפעיל אוטומטית את ה-ValueChangeListener למטה
            }
            else{
                UtilsHelper.showNotification("Symbol must be 1-5 characters", 5000, Position.TOP_CENTER, NotificationVariant.LUMO_ERROR);
            }
        });
        stocksComboBox.addValueChangeListener(e -> {
            this.symbol = e.getValue().toString();
            //updateCsvStatus();
            // fetchApiDataIfReady();
        });
        uploadApi.add(stocksComboBox);

        outputSizeEntryField = new IntegerField("Output size",e -> outputsize = e.getValue());
        outputSizeEntryField.setRequiredIndicatorVisible(true);
        outputSizeEntryField.setMin(1);
        outputSizeEntryField.setMax(5000);
        outputSizeEntryField.setValue(30);
        this.outputsize = 30;
        outputSizeEntryField.setStepButtonsVisible(true);
        outputSizeEntryField.addValueChangeListener(e -> {
            // IntegerField.getValue() מחזיר Integer (עטיפה), יכול להיות null
            this.outputsize = (e.getValue() != null) ? e.getValue() : 0;
            //updateCsvStatus();
            // fetchApiDataIfReady();
        });
        outputSizeEntryField.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format")
            .setMinErrorMessage("the output size must be at list 1")
            .setMaxErrorMessage("The output size must be 5000 maximum"));
        uploadApi.add(outputSizeEntryField);

        apiIntervalComboBox = new ComboBox<>("Choose the time interval",e -> apiInterval = e.getValue());
        apiIntervalComboBox.setItems(Interval.values());
        apiIntervalComboBox.setValue(Interval.DAY_1);
        this.apiInterval = Interval.DAY_1;
        apiIntervalComboBox.addValueChangeListener(e -> {
            this.apiInterval = e.getValue();
            //updateCsvStatus();
            // fetchApiDataIfReady();
        });
        uploadApi.add(apiIntervalComboBox);
        Button fetchApiButton = new Button("Fetch Api Data");
        fetchApiButton.getStyle().set("background-color", "#0859b1");
        fetchApiButton.getStyle().set("color", "white");
        fetchApiButton.getStyle().set("padding", "15px 30px");
        fetchApiButton.getStyle().set("font-size", "1.2rem");
        fetchApiButton.addClickListener(e -> {
            if(fetchApiDataIfReady())
                System.out.println("Operation succeded");
                //UtilsHelper.showNotification("Operation succeded",3000,Position.MIDDLE, NotificationVariant.LUMO_SUCCESS );
            else
                UtilsHelper.showNotification("Operation faild", 3000,Position.MIDDLE, NotificationVariant.LUMO_ERROR);
        });
        uploadApi.add(fetchApiButton);
        

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
        horizonEnteryField.setValue(15);
        this.predictionHorizon = 15;
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

        // Button predictButtonCopy = new Button("Predict2 (Only for developer)");
        // predictButtonCopy.getStyle().set("background-color", "#0fe404");
        // predictButtonCopy.getStyle().set("color", "white");
        // predictButtonCopy.getStyle().set("padding", "15px 30px");
        // predictButtonCopy.getStyle().set("font-size", "1.2rem");
        // predictButtonCopy.addClickListener(clickEvent -> predictButtonClickedCopy());
        // add(predictButtonCopy);

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
    boolean anyApiFieldFilled = (stocksComboBox.getValue() != null && !stocksComboBox.getValue().isEmpty()) ||
                                 (outputSizeEntryField.getValue() != null) ||
                                 (apiIntervalComboBox.getValue() != null);
    
    boolean isCsvIntervalSelected = csvIntervalComboBox.getValue() != null;
    // Enable the option to upload CSV only when the API is empty and the user chose an interval for the CSV
    //upload.setEnabled(!anyApiFieldFilled && isCsvIntervalSelected);
    //uploadCsv.getElement().getStyle().set("opacity", anyApiFieldFilled ? "0.5" : "1.0");
    }

    private boolean fetchApiDataIfReady()
    {
        if(symbol!=null && !symbol.trim().isEmpty() && apiInterval != null && outputsize>0)
        {
            try 
            {
                apiData = apiReaderService.getStockData(symbol,apiInterval,outputsize);
                if(apiData!=null&& !apiData.isEmpty())
                {
                    newTimeSeriesId = apiReaderService.CreateNewTimeSeries(
                    symbol,
                    LocalDateTime.now(),
                    apiData,
                    this.username,
                    this.apiInterval
                    );
                }
                Notification.show("API data loaded and saved!", 3000, Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                return true;
                
                //updateCsvStatus();
            } catch (Exception e) 
            {
                System.out.println(e);
                Notification.show("Failed to fetch API data: " + e.getMessage(), 5000, Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return false;
            }
        }
        return false;
    }

    // public void predictButtonClickedCopy() 
    // {
    //     // 1. וולידציה - מוודאים שכל הנתונים קיימים
    //     if (newTimeSeriesId == null || newTimeSeriesId.isEmpty()) {
    //         Notification.show("Please upload a CSV or fetch API data first.", 4000, Position.MIDDLE)
    //                 .addThemeVariants(NotificationVariant.LUMO_ERROR);
    //         return; // עוצר את הפונקציה
    //     }
    //     if (predictionHorizon <= 0) {
    //         Notification.show("Please enter a valid prediction horizon.", 4000, Position.MIDDLE)
    //                 .addThemeVariants(NotificationVariant.LUMO_ERROR);
    //         return;
    //     }
    //     if (algorithem == null || algorithem.isEmpty()) {
    //         Notification.show("Please select an algorithm.", 4000, Position.MIDDLE)
    //                 .addThemeVariants(NotificationVariant.LUMO_ERROR);
    //         return;
    //     }
    //     try
    //     {
    //         ForcastResult forcastResult = forcastingService.CreateNewForcast(newTimeSeriesId, predictionHorizon, algorithem, this.username, LocalDateTime.now());
    //         if (forcastResult != null && forcastResult.getId() != null) 
    //         {
    //             Notification.show("Forecast generated successfully!", 2000, Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    //             getUI().ifPresent(ui -> ui.navigate(ShowResultCopy.class,forcastResult.getId()));

    //         }
    //         else
    //         {
    //             Notification.show("Failed to generate forecast. Please try again.", 4000, Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);

    //         }
    //     }
    //     catch (Exception e) 
    //     {
    //         e.printStackTrace();
    //         Notification.show("An error occurred during forecasting: " + e.getMessage(), 5000, Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
    //     }
    // }
}

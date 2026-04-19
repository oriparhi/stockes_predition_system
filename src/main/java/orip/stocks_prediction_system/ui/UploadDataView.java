package orip.stocks_prediction_system.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
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
        intervalComboBox.addValueChangeListener(e -> updateCsvStatus());
        uploadApi.add(intervalComboBox);

        //TODO: לשחרר את הערות של העשר שורות הבאות שבהערה
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
        HorizontalLayout seasonalityPanel =new HorizontalLayout();
        IntegerField seasonalityPeriodEntry = new IntegerField("Seasonlity Period");
        seasonalityPeriodEntry.setEnabled(false);
        Checkbox isItSeasonalCheckbox = new Checkbox("Is the data you entered seasonal?",e -> {
            isItSeasonality = e.getValue();

            forcastingService.setItSeasonality(isItSeasonality);
            seasonalityPeriodEntry.setEnabled(isItSeasonality);
            // אופציונלי: איפוס הערך אם המשתמש ביטל את הסימון
            if (!isItSeasonality) {
                seasonalityPeriodEntry.clear();
            }
        });

        seasonalityPeriodEntry.setRequiredIndicatorVisible(true);
        seasonalityPeriodEntry.setMin(1);
        if(parsedData!= null)
            seasonalityPeriodEntry.setMax((parsedData.size())/2);
        else if (apiData!= null)
            seasonalityPeriodEntry.setMax(apiData.size()/2);
        seasonalityPeriodEntry.setStepButtonsVisible(true);
        seasonalityPeriodEntry.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format"));
        seasonalityPeriodEntry.addValueChangeListener(e ->{ 
            seasonalityPeriod = e.getValue();
            forcastingService.setSeasonalityPeriod(newTimeSeriesId, seasonalityPeriod);
        });
        seasonalityPanel.add(isItSeasonalCheckbox,seasonalityPeriodEntry);
        //----------------------------------
        add(seasonalityPanel);

        //allow scrolling if needed
        this.setSizeFull();
        this.getStyle().set("overflow-y", "auto");
        this.setPadding(true);
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

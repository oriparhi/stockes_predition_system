package orip.stocks_prediction_system.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import orip.stocks_prediction_system.utilities.Interval;

@Route(value = "/upload",layout = AppNavBarLayout.class)
public class UploadDataView extends VerticalLayout
{
    private final CsvReaderService csvReaderService;
    private final ApiReaderService apiReaderService;
    private String username;
    private String WelcomeMsg;
    private H1 title;
    private LocalDateTime time;
    private String newTimeSeriesId;
    private String symbol;
    private int outputsize;
    private Interval interval;

    public UploadDataView(CsvReaderService csvReaderService, ApiReaderService apiReaderService) 
    {
        this.apiReaderService = apiReaderService;
        this.csvReaderService = csvReaderService;

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

        VerticalLayout uploadCsv = new VerticalLayout();// פאנל העלה של הCSV
        uploadCsv.setWidth("50%");
        uploadCsv.getStyle().set("border-right", "2px solid black"); 
        uploadCsv.getStyle().set("padding-right", "20px");
        

        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory((metadata,data) ->{

            String fileName = metadata.fileName();
            Notification notify = new Notification("", 5000, Position.TOP_CENTER);
            try 
            {
              List<DataPoints> parsedData = csvReaderService.ReadCsv(new ByteArrayInputStream(data), fileName);
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

        Upload upload = new Upload(inMemoryHandler);
        upload.setAcceptedFileTypes(".csv");

        H4 attentionPlease = new H4("Please pay attention that your CSV must have 1 column only!");
        attentionPlease.getStyle().set("color", "red");
        attentionPlease.getStyle().set("font-weight", "bold");

        uploadCsv.add(new H2("Enter here your csv,"),attentionPlease,upload);

        uploadData.add(uploadCsv);

        VerticalLayout uploadApi = new VerticalLayout();// פאנל העלה של הAPI
        uploadApi.setWidth("50%");
        uploadApi.getStyle().set("padding-left", "20px");

        uploadApi.add(new H2("Enter here the stock's symbol/ticker"));
        TextField StockSymbolField = new TextField("Stock Symbol", e -> symbol = e.getValue());
        StockSymbolField.setMinLength(1);
        StockSymbolField.setMaxLength(5);
        uploadApi.add(StockSymbolField);

        IntegerField outputSizeEnteryField = new IntegerField("Output size",e -> outputsize = e.getValue());
        outputSizeEnteryField.setRequiredIndicatorVisible(true);
        outputSizeEnteryField.setMin(1);
        outputSizeEnteryField.setMax(5000);
        outputSizeEnteryField.setStepButtonsVisible(true);
        outputSizeEnteryField.setI18n(new IntegerFieldI18n()
            .setRequiredErrorMessage("Field is required")
            .setBadInputErrorMessage("Invalid number format")
            .setMinErrorMessage("the output size must be at list 1")
            .setMaxErrorMessage("The output size must be 5000 maximum"));
        uploadApi.add(outputSizeEnteryField);

        ComboBox<Interval> intervalComboBox = new ComboBox<>("Choose the time interval",e -> interval = e.getValue());
        intervalComboBox.setItems(Interval.values());
        uploadApi.add(intervalComboBox);

        uploadData.add(uploadApi);

        add(uploadData);
    }
    
}

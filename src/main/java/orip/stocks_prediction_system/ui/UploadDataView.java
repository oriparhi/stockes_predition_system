package orip.stocks_prediction_system.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.services.CsvReaderService;

@Route(value = "/upload",layout = AppNavBarLayout.class)
public class UploadDataView extends VerticalLayout
{
    private final CsvReaderService csvReaderService;
    private String username;
    private String WelcomeMsg;
    private H1 title;
    private LocalDateTime time;
    private String newTimeSeriesId;

    public UploadDataView(CsvReaderService csvReaderService) 
    {
        
        this.csvReaderService = csvReaderService;

        this.username = "Guest";
        WelcomeMsg = "Hello "+username;
        title = new H1(WelcomeMsg);
        time = LocalDateTime.now();

        add(title);
        add(new H2(time.toString()));
        add(new H3("Please enter here that data that you want to upload!"));
        add(new H3("You can do that by upload your CSV or by choosing a stock you want to forcast"));
        
        H4 attentionPlease = new H4("Please pay attention that your CSV must have 1 column only!");
        attentionPlease.getStyle().set("color", "red");
        attentionPlease.getStyle().set("font-weight", "bold");
        add(attentionPlease);

        HorizontalLayout uploadData = new HorizontalLayout();// הפאנל שיאחסן את העלה של הCSV והAPI
        uploadData.setWrap(true);
        VerticalLayout uploadCsv = new VerticalLayout();// פאנל העלה של הCSV
        VerticalLayout uploadApi = new VerticalLayout();// פאנל העלה של הAPI

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

        uploadCsv.add(new H2("Enter here your csv,"),upload);
        uploadData.add(uploadCsv);
        add(uploadData);
    }
    
}

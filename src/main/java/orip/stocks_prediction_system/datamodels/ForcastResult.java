package orip.stocks_prediction_system.datamodels;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.vaadin.flow.component.template.Id;

@Document(collection = "ForcastResult")
public class ForcastResult 
{
    @Id
    private String id;
    private String requestId;
    private String CreatedBy;
    private LocalDateTime resultDate;
    private String algorithemUsed;
    private double mse;
    private List<DataPoints> forcastResults;


    public ForcastResult(String requestId, String createdBy, LocalDateTime resultDate, String algorithemUsed,
            double mse, List<DataPoints> forcastResults) {
        this.requestId = requestId;
        CreatedBy = createdBy;
        this.resultDate = resultDate;
        this.algorithemUsed = algorithemUsed;
        this.mse = mse;
        this.forcastResults = forcastResults;
    }


    public String getId() {
        return id;
    }



    public String getCreatedBy() {
        return CreatedBy;
    }



    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }



    public String getRequestId() {
        return requestId;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public LocalDateTime getResultDate() {
        return resultDate;
    }


    public void setResultDate(LocalDateTime resultDate) {
        this.resultDate = resultDate;
    }


    public String getAlgorithemUsed() {
        return algorithemUsed;
    }


    public void setAlgorithemUsed(String algorithemUsed) {
        this.algorithemUsed = algorithemUsed;
    }


    public double getMse() {
        return mse;
    }


    public void setMse(double mse) {
        this.mse = mse;
    }


    public List<DataPoints> getForcastResults() {
        return forcastResults;
    }


    public void setForcastResults(List<DataPoints> forcastResults) {
        this.forcastResults = forcastResults;
    }    
    
}

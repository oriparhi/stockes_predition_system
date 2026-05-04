package orip.stocks_prediction_system.datamodels;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "ForcastRequests")
public class ForcastRequest 
{
    @Id
    private String forcastId;
    private String timeSeriesId;
    private int predictionHorizon;
    private String Algorithm;
    private String requestedBy;
    private LocalDateTime requestedAt;
    

    public ForcastRequest(String timeSeriesId, int predictionHorizon, boolean isItSeasonality, String algorithm, String requestedBy, LocalDateTime requestedAt) {
        this.timeSeriesId = timeSeriesId;
        this.predictionHorizon = predictionHorizon;
        Algorithm = algorithm;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
    }



    public String getRequestedBy() {
        return requestedBy;
    }



    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }



    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }



    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }



    public int getPredictionHorizon() {
        return predictionHorizon;
    }

    public void setPredictionHorizon(int predictionHorizon) {
        this.predictionHorizon = predictionHorizon;
    }

    public String getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(String algorithm) {
        Algorithm = algorithm;
    }

    public String getForcastId() {
        return forcastId;
    }

    public String getTimeSeriesId() {
        return timeSeriesId;
    }

    public void setTimeSeriesId(String timeSeriesId) {
        this.timeSeriesId = timeSeriesId;
    }


    

}

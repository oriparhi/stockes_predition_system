package orip.stocks_prediction_system.datamodels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import orip.stocks_prediction_system.utilities.Interval;

@Document(collection = "ForcastRequests")
public class ForcastRequest 
{
    @Id
    private String forcastId;
    private String timeSeriesId;
    private Interval interval;
    private int dataLimit;
    private int predictionHorizon;
    private String Algorithem;
    
    public ForcastRequest(String timeSeriesId, Interval interval, int dataLimit, int predictionHorizon,
            String algorithem) {
        this.timeSeriesId = timeSeriesId;
        this.interval = interval;
        this.dataLimit = dataLimit;
        this.predictionHorizon = predictionHorizon;
        Algorithem = algorithem;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public int getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(int dataLimit) {
        this.dataLimit = dataLimit;
    }

    public int getPredictionHorizon() {
        return predictionHorizon;
    }

    public void setPredictionHorizon(int predictionHorizon) {
        this.predictionHorizon = predictionHorizon;
    }

    public String getAlgorithem() {
        return Algorithem;
    }

    public void setAlgorithem(String algorithem) {
        Algorithem = algorithem;
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

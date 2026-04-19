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
    private int dataLimit;
    private int predictionHorizon;
    private boolean isItSeasonality;
    private String Algorithem;
    
    public ForcastRequest(String timeSeriesId, int predictionHorizon, String algorithem,boolean isItSeasonality,int dataLimit) 
    {
        this.timeSeriesId = timeSeriesId;
        this.predictionHorizon = predictionHorizon;
        Algorithem = algorithem;
        this.isItSeasonality = isItSeasonality;
        this.dataLimit = dataLimit;
    }


    public ForcastRequest(String timeSeriesId, int predictionHorizon, boolean isItSeasonality, String algorithem) 
    {
        this.timeSeriesId = timeSeriesId;
        this.predictionHorizon = predictionHorizon;
        this.isItSeasonality = isItSeasonality;
        Algorithem = algorithem;
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

    public boolean isItSeasonality() {
        return isItSeasonality;
    }

    public void setItSeasonality(boolean isItSeasonality) {
        this.isItSeasonality = isItSeasonality;
    }

    

}

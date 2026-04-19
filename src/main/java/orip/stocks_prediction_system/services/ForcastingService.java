package orip.stocks_prediction_system.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch;
import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.Forcasting.ForcastModel;
import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.repositories.ForcastRequestRepository;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;

@Service
public class ForcastingService 
{
    TimeSeriesRepo timeSeriesRepo;
    ForcastRequestRepository forcastRequestRepository;

    private boolean isItSeasonality;
    private int seasonalityPeriod;

    private String timeSeriesId;
    private int predictionHorizon;
    private String Algorithem;

    public ForcastingService(TimeSeriesRepo timeSeriesRepo, ForcastRequestRepository forcastRequestRepository) 
    {
        this.timeSeriesRepo = timeSeriesRepo;
        this.forcastRequestRepository = forcastRequestRepository;
    }

    public boolean isItSeasonality() {
        return isItSeasonality;
    }

    public void setItSeasonality(boolean isItSeasonality) {
        this.isItSeasonality = isItSeasonality;
    }

    public int getSeasonalityPeriod() {
        return seasonalityPeriod;
    }

    public void setSeasonalityPeriod(String timeSeriesId ,int seasonalityPeriod) 
    {
        this.seasonalityPeriod = seasonalityPeriod;
        if(timeSeriesRepo.existsById(timeSeriesId))
        {
            TimeSeries ts = timeSeriesRepo.findById(timeSeriesId).orElseThrow();
            ts.setSeasonalityPeriod(seasonalityPeriod);
            timeSeriesRepo.save(ts);
        }
        
    }

    public List<DataPoints> CreateNewForcast(String timeSeriesId, int predictionHorizon, String Algorithm)
    {
        this.timeSeriesId = timeSeriesId;
        this.predictionHorizon = predictionHorizon;
        this.Algorithem = Algorithm;
        List<DataPoints> forcastResults = new ArrayList<DataPoints>();
        ForcastRequest newForcastRequest = new ForcastRequest(timeSeriesId, predictionHorizon, isItSeasonality, Algorithm);
        forcastRequestRepository.insert(newForcastRequest);
        switch (Algorithm) {
            case "Average":
                forcastResults = runAverage();
                break;
            case "Exponential Smoothing":
                forcastResults = runExponentialSmoothing();
                break;
            case "Linear Reagression":
                forcastResults = runLinearReagression();
                break;
            case "Moving Average":
                forcastResults = runMovingAverage();
                break;
            case "Holt Winters":
                if(isItSeasonality)
                {
                    forcastResults = runHoltWinters();
                }
                else
                {
                    System.out.println("Can't active Holt-Winters algorithem, The data doesn't have seasonality. ");
                    forcastResults = null;
                }
                break;
            case "The best algorithem":
                if(isItSeasonality)
                    forcastResults = runBestAlgorithm_WithHoltWinters();
                else
                    forcastResults = runBestAlgorithem_WithoutHoltWinters();
                break;
        
            default:
                System.out.println("ERROR: User didn't chose any algorithem to activate");
                forcastResults = null;
                break;
            
            return forcastResults;
        }
    }
    
}

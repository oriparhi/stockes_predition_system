package orip.stocks_prediction_system.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch;
import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.forcasting.Average;
import orip.stocks_prediction_system.forcasting.ExponentialSmoothing;
import orip.stocks_prediction_system.forcasting.ForcastModel;
import orip.stocks_prediction_system.forcasting.HoltWinters_SeasonalityModel;
import orip.stocks_prediction_system.forcasting.LinearRegression;
import orip.stocks_prediction_system.forcasting.MovingAverage;
import orip.stocks_prediction_system.repositories.ForcastRequestRepository;
import orip.stocks_prediction_system.repositories.ForcastResultRepository;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;

@Service
public class ForcastingService 
{
    TimeSeriesRepo timeSeriesRepo;
    ForcastRequestRepository forcastRequestRepository;
    ForcastResultRepository forcastResultRepository;

    private boolean isItSeasonality;
    private int seasonalityPeriod;

    private String timeSeriesId;
    private int predictionHorizon;
    private String Algorithem;

    private double MSE;

    public ForcastingService(TimeSeriesRepo timeSeriesRepo, ForcastRequestRepository forcastRequestRepository, ForcastResultRepository forcastResultRepository) 
    {
        this.timeSeriesRepo = timeSeriesRepo;
        this.forcastRequestRepository = forcastRequestRepository;
        this.forcastResultRepository = forcastResultRepository;
    }

    public boolean isItSeasonality() {
        return isItSeasonality;
    }

    public void setItSeasonality(String timeSeriesId, boolean isItSeasonality)
    {
        this.isItSeasonality = isItSeasonality;
        if(timeSeriesRepo.existsById(timeSeriesId))
        {
            TimeSeries ts = timeSeriesRepo.findById(timeSeriesId).orElseThrow();
            ts.setItSeasonality(isItSeasonality);
            timeSeriesRepo.save(ts);
        }    
        
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

    public ForcastResult CreateNewForcast(String timeSeriesId, int predictionHorizon, String Algorithm, String requestedBy, LocalDateTime requestedAt)
    {
        //Creating new forcast request
        this.timeSeriesId = timeSeriesId;
        this.predictionHorizon = predictionHorizon;
        this.Algorithem = Algorithm;
        List<DataPoints> forcastResultsList = new ArrayList<DataPoints>();
        ForcastRequest newForcastRequest = new ForcastRequest(timeSeriesId, predictionHorizon, isItSeasonality, Algorithm,requestedBy,requestedAt);
        newForcastRequest = forcastRequestRepository.insert(newForcastRequest);

        //Doing the forcast
        Long startTime = System.nanoTime();

        TimeSeries ts = timeSeriesRepo.findById(timeSeriesId).orElseThrow(() -> new RuntimeException("TimeSeries not found with id: " + timeSeriesId));;
        List<DataPoints> data = ts.getData();
        int splitIndex = (int) (data.size() * 0.8);
        List<DataPoints> buildingNumbers = data.subList(0,splitIndex);
        List<DataPoints> auditData = data.subList(splitIndex,data.size());
        switch (Algorithm) {
            case "Average":
                forcastResultsList = runAverage(buildingNumbers,auditData);
                break;
            case "Exponential Smoothing":
                forcastResultsList = runExponentialSmoothing(buildingNumbers,auditData);
                break;
            case "Linear Reagression":
                forcastResultsList = runLinearReagression(buildingNumbers,auditData);
                break;
            case "Moving Average":
                forcastResultsList = runMovingAverage(buildingNumbers,auditData);
                break;
            case "Holt Winters":
                if(isItSeasonality)
                {
                    forcastResultsList = runHoltWinters(buildingNumbers,auditData);
                }
                else
                {
                    System.out.println("Can't active Holt-Winters algorithem, The data doesn't have seasonality. ");
                    forcastResultsList = null;
                }
                break;
            case "The best algorithem":
                if(isItSeasonality)
                    forcastResultsList = runBestAlgorithm_WithHoltWinters(buildingNumbers,auditData);
                else
                    forcastResultsList = runBestAlgorithem_WithoutHoltWinters(buildingNumbers,auditData);
                break;
        
            default:
                System.out.println("ERROR: User didn't chose any algorithem to activate");
                forcastResultsList = null;
        }
        Long endTime = System.nanoTime();
        long duration = endTime-startTime;
        String createdBy = requestedBy;
        LocalDateTime createdAt = requestedAt.plusNanos(duration);
        ForcastResult forcastResult = new ForcastResult(newForcastRequest.getForcastId(),createdBy, createdAt, Algorithm, MSE, forcastResultsList);
        forcastResultRepository.insert(forcastResult);
        return forcastResult;
        //return forcastResultsList;
    }

    /**
     * Found the best forcast algorithem from all the forcast's algorithem available.
     * This one is without Holt-Winters model
     * @param buildingNumbers - the data for building the parameters and the first forcast for checking the MSE of the algorithem
     * @param auditData - the data the real data that we want to check it against the first prediction in order to find the MSE
     * @return - the total forcast.
     */
    private List<DataPoints> runBestAlgorithem_WithoutHoltWinters(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        //Creating the objects of the forcast methods
        Average avg = new Average(buildingNumbers, auditData);
        MovingAverage ma = new MovingAverage(buildingNumbers, auditData);
        LinearRegression lr = new LinearRegression(buildingNumbers, auditData);
        ExponentialSmoothing ema = new ExponentialSmoothing(buildingNumbers, auditData);
        

        //Calculating the mse for each model
        double avgMse, maMse, lrMse, emaMse;
        avgMse = avg.CalculateMse();
        maMse = ma.CalculateMse();
        lrMse = lr.CalculateMse();
        emaMse = lr.CalculateMse();
        

        //Finding the best mse, Starting with the assumption that average have the best mse
        double minMse = avgMse;
        String bestModelName = "Average";

        // בדיקה מול Moving Average
        if (maMse < minMse) {
            minMse = maMse;
            bestModelName = "Moving Average";
        }

        // בדיקה מול Linear Regression
        if (lrMse < minMse) {
            minMse = lrMse;
            bestModelName = "Linear Regression";
        }

        // בדיקה מול EMA
        if (emaMse < minMse) {
            minMse = emaMse;
            bestModelName = "EMA";
        }


        System.out.println("Best model is "+bestModelName+" and his mse is "+minMse);
        
        //Doing the real forcast
        List<DataPoints> forcastList = new ArrayList<>();
        switch (bestModelName) {
            case "Average":
                forcastList = avg.predict(predictionHorizon);
                break;
            
            case "Moving Average":
                forcastList = ma.predict(predictionHorizon);
                break;
            
            case "Linear Regression":
                forcastList = lr.predict(predictionHorizon);
                break;

            case "EMA":
                forcastList = ema.predict(predictionHorizon);
                break;

        
            default:
                System.out.println("Error");
                break;
        }

        for(int i  = 0; i<forcastList.size();i++)
        {
            System.out.println(i+ " : "+forcastList.get(i));
        }

        MSE = minMse;
        Algorithem = bestModelName;
        
        return forcastList;
    }
    
    /**
     * Found the best forcast algorithem from all the forcast's algorithem available.
     * This one checks with for Holt-Winters model for cases with seasonality.
     * @param buildingNumbers - the data for building the parameters and the first forcast for checking the MSE of the algorithem
     * @param auditData - the data the real data that we want to check it against the first prediction in order to find the MSE
     * @return - the total forcast.
     */
    private List<DataPoints> runBestAlgorithm_WithHoltWinters(List<DataPoints> buildingNumbers,List<DataPoints> auditData) 
    {
        //Creating the objects of the forcast methods
        Average avg = new Average(buildingNumbers, auditData);
        MovingAverage ma = new MovingAverage(buildingNumbers, auditData);
        LinearRegression lr = new LinearRegression(buildingNumbers, auditData);
        ExponentialSmoothing ema = new ExponentialSmoothing(buildingNumbers, auditData);
        HoltWinters_SeasonalityModel holtWinters = new HoltWinters_SeasonalityModel(buildingNumbers, auditData, seasonalityPeriod);

        //Calculating the mse for each model
        double avgMse, maMse, lrMse, emaMse, holtWintersMse;
        avgMse = avg.CalculateMse();
        maMse = ma.CalculateMse();
        lrMse = lr.CalculateMse();
        emaMse = lr.CalculateMse();
        holtWintersMse = holtWinters.CalculateMse();

        //Finding the best mse, Starting with the assumption that average have the best mse
        double minMse = avgMse;
        String bestModelName = "Average";

        // בדיקה מול Moving Average
        if (maMse < minMse) {
            minMse = maMse;
            bestModelName = "Moving Average";
        }

        // בדיקה מול Linear Regression
        if (lrMse < minMse) {
            minMse = lrMse;
            bestModelName = "Linear Regression";
        }

        // בדיקה מול EMA
        if (emaMse < minMse) {
            minMse = emaMse;
            bestModelName = "EMA";
        }

        // בדיקה מול Holt-Winters
        if (holtWintersMse < minMse) {
            minMse = holtWintersMse;
            bestModelName = "Holt-Winters";
        }

        System.out.println("Best model is "+bestModelName+" and his mse is "+minMse);
        
        //Doing the real forcast
        List<DataPoints> forcastList = new ArrayList<>();
        switch (bestModelName) {
            case "Average":
                forcastList = avg.predict(predictionHorizon);
                break;
            
            case "Moving Average":
                forcastList = ma.predict(predictionHorizon);
                break;
            
            case "Linear Regression":
                forcastList = lr.predict(predictionHorizon);
                break;

            case "EMA":
                forcastList = ema.predict(predictionHorizon);
                break;

            case "Holt-Winters":
                forcastList = holtWinters.predict(predictionHorizon);
                break;
        
            default:
                System.out.println("Error");
                break;
        }

        for(int i  = 0; i<forcastList.size();i++)
        {
            System.out.println(i+ " : "+forcastList.get(i));
        }
        
        MSE = minMse;
        Algorithem = bestModelName;
        return forcastList;
    }
        
    private List<DataPoints> runHoltWinters(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        HoltWinters_SeasonalityModel holtWinters = new HoltWinters_SeasonalityModel(new ArrayList<>(buildingNumbers), new ArrayList<>(auditData), seasonalityPeriod);
        double holtWintersMse = holtWinters.CalculateMse();
        System.out.println("The MSE of Holt Winters for this data is "+ holtWintersMse);
        List<DataPoints> Predictions = holtWinters.predict(predictionHorizon);
        return Predictions;
    }

    private List<DataPoints> runMovingAverage(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        MovingAverage ma = new MovingAverage(new ArrayList<>(buildingNumbers), new ArrayList<>(auditData));
        double maMse = ma.CalculateMse();
        System.out.println("The MSE of Moving Average for this data is "+ maMse);
        List<DataPoints> Predictions = ma.predict(predictionHorizon);
        MSE = maMse;
        return Predictions;
    }

    private List<DataPoints> runLinearReagression(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        LinearRegression lr = new LinearRegression(new ArrayList<>(buildingNumbers), new ArrayList<>(auditData));
        double lrMse = lr.CalculateMse();
        System.out.println("The MSE of Linear Regression for this data is "+ lrMse);
        List<DataPoints> Predictions = lr.predict(predictionHorizon);
        MSE = lrMse;
        return Predictions;
    }
    private List<DataPoints> runExponentialSmoothing(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        ExponentialSmoothing ema = new ExponentialSmoothing(new ArrayList<>(buildingNumbers), new ArrayList<>(auditData));
        double emaMse = ema.CalculateMse();
        System.out.println("The MSE of exponential smoothing is "+emaMse);
        List<DataPoints> Predictions = ema.predict(predictionHorizon);

        MSE = emaMse;
        return Predictions;
    }

    private List<DataPoints> runAverage(List<DataPoints> buildingNumbers, List<DataPoints> auditData) 
    {
        Average avg = new Average(buildingNumbers, auditData);
        double avgMse = avg.CalculateMse();
        System.out.println("The Mse of Average for the data is "+avgMse);
        List<DataPoints> Predictions = new ArrayList<>();
        Predictions = avg.predict(predictionHorizon);

        MSE = avgMse;
        return Predictions;
    }
    
}

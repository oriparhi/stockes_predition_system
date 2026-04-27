package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

import orip.stocks_prediction_system.datamodels.DataPoints;

public interface ForcastModel 
{
    public double CalculateMse();
    ArrayList<DataPoints> predict(int futureSteps);
    
}

package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

public interface ForcastModel 
{
    public double CalculateMse();
    ArrayList<Double> predict(int futureSteps);
    
}

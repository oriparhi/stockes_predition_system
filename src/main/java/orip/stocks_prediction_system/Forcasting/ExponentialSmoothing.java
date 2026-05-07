package orip.stocks_prediction_system.forcasting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import orip.stocks_prediction_system.datamodels.DataPoints;

public class ExponentialSmoothing extends AbstractForcastModel 
{
    public ExponentialSmoothing(List<DataPoints> buildingNumbers,List<DataPoints> auditData) {
        this.buildingNumbers = new ArrayList<>(buildingNumbers);
        this.auditData = new ArrayList<>(auditData);
        
        // אתחול רשימות ריקות
        this.errorList = new ArrayList<>();
        this.forecastList = new ArrayList<>();
        
        // ערך התחלתי -1
        this.MSE = -1;
        
        // חישוב גודל כולל
        this.totalSize = buildingNumbers.size() + auditData.size();
    }

    
    @Override
    public double CalculateMse() 
    {
        double lastForecast = buildingNumbers.get(0).getValue();
        double alpha = findBestAlpha(buildingNumbers);
        double sumErrorSqr = 0;
        for(int i = 1; i<buildingNumbers.size();i++)
        {
            lastForecast = alpha*buildingNumbers.get(i).getValue() + (1-alpha)*lastForecast;
        }

        for(DataPoints actual : auditData) 
        {
            sumErrorSqr += Math.pow((actual.getValue() - lastForecast), 2);
        }

        this.MSE = sumErrorSqr/auditData.size();
        System.out.println("Exponential smoothing mse: "+this.MSE);
        return this.MSE;
    }

    @Override
    public ArrayList<DataPoints> predict(int futureSteps) 
    {
        forecastList.clear();
        ArrayList <DataPoints> combined = new ArrayList<>(buildingNumbers);
        combined.addAll(auditData);
        double lastForecast = combined.get(0).getValue();
        double alpha = findBestAlpha(combined);
        for(int i = 1; i<combined.size();i++)
        {
            lastForecast = alpha*combined.get(i).getValue() + (1-alpha)*lastForecast;
        }
        
        for(int j = 0; j<futureSteps;j++)
        {
            if(j==0)
                forecastList.add(new DataPoints(0,lastForecast));
            else
                forecastList.add(new DataPoints(forecastList.getLast().getIndex()+1,lastForecast));
        }
        return forecastList;
    }    

    private double findBestAlpha(ArrayList <DataPoints> data)
    {
        int N = data.size();
        UnivariateFunction insideMseFunction = new UnivariateFunction() 
        {
            @Override
            public double value(double alpha) 
            {
                double insideMse=0;
                double previousForecast = data.get(0).getValue();
                for(int i = 1; i<N;i++)
                {
                    double currentActual = data.get(i).getValue();
                    double error = currentActual-previousForecast;
                    insideMse+= Math.pow(error, 2);

                    //Updating forcast for next round
                    previousForecast = alpha*currentActual + (1-alpha) * previousForecast;
                }
                insideMse = insideMse/(N-1);
                return insideMse;
            }
        };
        // 2. הגדרת האופטימיזטור (BrentOptimizer נחשב יעיל ומהיר לחיפוש חד-ממדי)
        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);

        UnivariatePointValuePair result = optimizer.optimize(
            new MaxEval(1000),
            new UnivariateObjectiveFunction(insideMseFunction),
            GoalType.MINIMIZE,
            new SearchInterval(0, 1)
        );

        double bestAlpha = result.getPoint();
        System.out.println("inside msem for best alpha is "+result.getValue());
        System.out.println("Exponential smoothing alpha: "+bestAlpha);
        return bestAlpha;
    }
}

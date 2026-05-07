package orip.stocks_prediction_system.forcasting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

import orip.stocks_prediction_system.datamodels.DataPoints;

public class HoltWinters_SeasonalityModel extends AbstractForcastModel
{
    private int Seasonality_Period;

    public HoltWinters_SeasonalityModel(List<DataPoints> buildingNumbers, List<DataPoints> auditData, int Seasonality_Period) 
    {
        this.buildingNumbers = new ArrayList<>(buildingNumbers);
        this.auditData = new ArrayList<>(auditData);
        
        // אתחול רשימות ריקות
        this.errorList = new ArrayList<>();
        this.forecastList = new ArrayList<>();
        
        // ערך התחלתי -1
        this.MSE = -1;
        
        // חישוב גודל כולל
        this.totalSize = buildingNumbers.size() + auditData.size();
        // פרמטר המחזוריות
        this.Seasonality_Period= Seasonality_Period;
    }

    public int getSeasonality_Period() {
        return Seasonality_Period;
    }

    public void setSeasonality_Period(int seasonality_Period) {
        Seasonality_Period = seasonality_Period;
    }


    @Override
    public double CalculateMse() 
    {
        int forecastHorizon = auditData.size();

        double bestParams[] = optimizeParams(buildingNumbers);
        double alpha = bestParams[0];
        double beta = bestParams[1];
        double gamma = bestParams[2];
        System.out.printf("calculateTotalMSE: alpha = %.9f, beta = %.9f, gamma = %.9f",alpha,beta,gamma);

        ArrayList <DataPoints> forcast = calculateForecast(buildingNumbers,alpha,beta,gamma,forecastHorizon);

        double sumSquaredError = 0;
        for(int j = 0; j<auditData.size();j++)
        {
            sumSquaredError+= Math.pow((auditData.get(j).getValue()-forcast.get(j).getValue()), 2);
        }

        this.MSE = sumSquaredError/auditData.size();
        System.out.println("Holt winters mse: "+this.MSE);
        return this.MSE;
    }

    @Override
    public ArrayList<DataPoints> predict(int futureSteps) 
    {
         ArrayList <DataPoints> combinedData = new ArrayList<>(buildingNumbers);
        combinedData.addAll(auditData);

        double bestParams[] = optimizeParams(combinedData);
        double alpha = bestParams[0];
        double beta = bestParams[1];
        double gamma = bestParams[2];
        System.out.printf("predict: alpha = %.9f, beta = %.9f, gamma = %.9f",alpha,beta,gamma);

        forecastList = calculateForecast(combinedData, alpha, beta, gamma, futureSteps);

        return forecastList;
    }
    
    //Helper functions

    // --- Optimization Logic ---
    private double[] optimizeParams(ArrayList<DataPoints> data) 
    {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);

        MultivariateFunction function = point -> {
            double a = point[0];
            double b = point[1];
            double g = point[2];

            // Constraint: Parameters must be between 0 and 1
            if (a < 0 || a > 1 || b < 0 || b > 1 || g < 0 || g > 1) {
                return Double.MAX_VALUE;
            }
            return calculateInsideMSE(data, a, b, g);
        };

        // Initial guess
        double[] initialGuess = {0.1, 0.1, 0.1};

        PointValuePair optimum = optimizer.optimize(
                new MaxEval(10000),
                new ObjectiveFunction(function),
                GoalType.MINIMIZE,
                new InitialGuess(initialGuess),
                new NelderMeadSimplex(3)
        );

        return optimum.getPoint();
    }

    private double calculateInsideMSE(ArrayList<DataPoints> data, double alpha, double beta, double gamma) 
    {
        int n = data.size();
        int s = Seasonality_Period;

        double Level[] = new double[n];
        double Trend[] = new double[n];
        double Seasonal[] = new double[n];

        // Initialize Seasonals (Year 1)
        double avgYear1 = 0;
        for (int i = 0; i < s; i++) avgYear1 += data.get(i).getValue();
        avgYear1 /= s;

        for (int i = 0; i < s; i++) {
            Seasonal[i] = data.get(i).getValue() / avgYear1;
        }

        double sumSquaredError=0;
        int errorCount=0;

        // Main Loop (starting from Year 2)
        for(int t = s; t<n; t++)
        {
            
            if(t == s) //First prediction after the initialization
            {
                Level[t] = data.get(t).getValue()/Seasonal[t - s];
                double prevDecDeserialized = data.get(t - 1).getValue() / Seasonal[t - 1];
                Trend[t] = Level[t] - prevDecDeserialized;
            }
            else
            {
                //Calculate Error
                double forcast = (Level[t-1] + Trend[t-1]) * Seasonal[t-1];
                double error = data.get(t).getValue() - forcast;
                sumSquaredError += (error * error);
                errorCount++;

                // Update Level
                double valDeserialized = data.get(t).getValue() / Seasonal[t - s];
                Level[t] = alpha * valDeserialized + (1 - alpha) * (Level[t - 1] + Trend[t - 1]);

                // Update Trend
                Trend[t] = beta * (Level[t] - Level[t - 1]) + (1 - beta) * Trend[t - 1];
            }

            // Update Seasonal (Multiplicative)
            Seasonal[t] = gamma * (data.get(t).getValue() / Level[t]) + (1 - gamma) * Seasonal[t - s];
        }
        
        if (errorCount == 0) {
            return 0;
        } else {
            return sumSquaredError / errorCount;
        }
    }
    
    private ArrayList<DataPoints> calculateForecast(ArrayList<DataPoints> data, double alpha, double beta, double gamma,int horizons) 
    {
        int n = data.size();
        int s = Seasonality_Period;

        double Level[] = new double[n];
        double Trend[] = new double[n];
        double Seasonal[] = new double[n + horizons];

        // Initialize Seasonals (Year 1)
        double avgYear1 = 0;
        for (int i = 0; i < s; i++) avgYear1 += data.get(i).getValue();
        
        avgYear1 /= s;

        for (int i = 0; i < s; i++) {
            Seasonal[i] = data.get(i).getValue() / avgYear1;
        }


        // Main Loop (starting from Year 2)
        for(int t = s; t<n; t++)
        {
            
            if(t == s) //First prediction after the initialization
            {
                Level[t] = data.get(t).getValue()/Seasonal[t - s];
                double prevDecDeserialized = data.get(t - 1).getValue() / Seasonal[t - 1];
                Trend[t] = Level[t] - prevDecDeserialized;
            }
            else
            {
                // Update Level
                double valDeserialized = data.get(t).getValue() / Seasonal[t - s];
                Level[t] = alpha * valDeserialized + (1 - alpha) * (Level[t - 1] + Trend[t - 1]);

                // Update Trend
                Trend[t] = beta * (Level[t] - Level[t - 1]) + (1 - beta) * Trend[t - 1];
            }
            Seasonal[t] = gamma * (data.get(t).getValue() / Level[t]) + (1 - gamma) * Seasonal[t - s];
        }

        //project into the future
        ArrayList <DataPoints> forecasts = new ArrayList<>();
        double lastLevel = Level[n-1];
        double lastTrend = Trend[n-1];
        
        for (int k = 1; k <= horizons; k++) 
        {
            int seasonalIndex = (n - 1) + k - s;
            
            //In case of a forcast for the deep future, in case of horizon > s
            while(seasonalIndex >= n) {
                seasonalIndex -= s;
            }
            
            double seasonalFactor = Seasonal[seasonalIndex];
            
            double prediction = (lastLevel + (k * lastTrend)) * seasonalFactor;
            if(forecasts.size()==0)
                forecasts.add(new DataPoints(0,prediction));
            else
                forecasts.add(new DataPoints(forecasts.getLast().getIndex()+1,prediction));
        }

        return forecasts;
    }

}

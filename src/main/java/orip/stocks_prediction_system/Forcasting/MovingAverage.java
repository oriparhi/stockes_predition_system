package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
@Component("Moving_Average")
public class MovingAverage extends AbstractForcastModel
{
    private int K;
    public MovingAverage(ArrayList<Double> buildingNumbers,ArrayList<Double> auditData) 
    {
        this.buildingNumbers = buildingNumbers;
        this.auditData = auditData;
        this.MSE =-1;
        totalSize = buildingNumbers.size()+auditData.size();
        this.K = 0;
    }
    
    /**
     * Finds best K for the moving average model
     */
    public void findBestK()
    {
        double minInsideMSE = Double.MAX_VALUE;
        int bestK = 1;
        
        // running on all the possible K values in order to find the best K
        for (int k = 1; k < buildingNumbers.size(); k++) {
            double[] mseAndForecast = calculateInsideMse(k);
            double insideMse = mseAndForecast[0];

            if (insideMse < minInsideMSE) {
                minInsideMSE = insideMse;
                bestK = k;
            }
        }
        
        // Saving the best K that was founded in class's k
        this.K = bestK;
        System.out.println("Moving average K: " + this.K);
    }


    @Override
    public double CalculateMse() 
    {
        if(this.K<=0){
            System.out.println("K is not set. Running findBestK() first...");
            findBestK();
        }

        List<Double> simulatedData = new ArrayList<>(buildingNumbers);
        double currentForecast = calculateAvg(this.K);
        List<Double> predictionsForAudit = new ArrayList<>();

        for (int j = 0; j < auditData.size(); j++) {
            // שומרים את התחזית הנוכחית
            predictionsForAudit.add(currentForecast);
            
            // מוסיפים את התחזית לרשימה המדומה כדי לחשב את הצעד הבא (חיזוי רקורסיבי)
            simulatedData.add(currentForecast);
            
            double sum = 0;
            for (int i = 0; i < this.K; i++) {
                sum += simulatedData.get(simulatedData.size() - 1 - i);
            }
            currentForecast = sum / this.K;
        }

        //The calculation of the final MSE
        errorList.clear();
        for (int i = 0; i < auditData.size(); i++) {
             double error = auditData.get(i) - predictionsForAudit.get(i);
             errorList.add(error * error);
        }
        this.MSE = calculateMSE(); // קורא לפונקציית העזר הפרטית של המחלקה
        
        System.out.println("Moving Average Audit MSE: " + this.MSE);
        return this.MSE;
    }

    @Override
    public ArrayList<Double> predict(int futureSteps) 
    {
        ArrayList <Double> combined = new ArrayList<>(buildingNumbers);
        combined.addAll(auditData);
        for(int j = 0; j<futureSteps;j++)
        {
            double avg=0,forcast = 0,sum=0;
            for(int i = 0;i<K;i++)
            {
                sum+=combined.get(combined.size()-1-i);
            }
            avg = sum/K;
            forcast = avg;
            combined.add(forcast);
            forecastList.add(forcast);
        }
        return forecastList;
    }

    //private functions
        private double[] calculateInsideMse(int k) 
    {
        double insideMse;
        double forcast = calculateAvg(k);
        insideMse = calculateMSE();
        double mseAndForcast[] ={insideMse,forcast};
        return mseAndForcast;
    }

    private double calculateMSE()
    {
        //To prevent dividing by 0
        if (errorList.isEmpty()) {
            return 0;
        }

        double sumErrors = 0,mse = 0.0;
        for (Double errorVal : errorList) 
        {
        sumErrors += errorVal;
        }

        mse = sumErrors/errorList.size();
        return mse;
    }

    private double calculateAvg(int k)
    {
        errorList.clear();

        double forcast=0,sum=0,errorSquered = 0;
        for(int i=0; i<k;i++)
            sum+=buildingNumbers.get(i);

        forcast = sum/k;

        for(int i = k; i<buildingNumbers.size(); i++)
        {
            double actualValue = buildingNumbers.get(i);

            double error = buildingNumbers.get(i)-forcast;
            errorSquered = error*error;
            errorList.add(errorSquered);
            // 2. עדכון הסכום עבור התחזית הבאה (Sliding Window)
            // מורידים את האיבר הישן ביותר בחלון (i - k)
            // ומוסיפים את האיבר הנוכחי שנכנס לחלון (i)
            sum = sum - buildingNumbers.get(i - k) + actualValue;

            // 3. עדכון התחזית לאיטרציה הבאה
            forcast = sum/k;
        }
        
        return forcast;
    }
}

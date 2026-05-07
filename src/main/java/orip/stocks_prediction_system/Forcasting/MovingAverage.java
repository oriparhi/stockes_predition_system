package orip.stocks_prediction_system.forcasting;

import java.util.ArrayList;
import java.util.List;

import orip.stocks_prediction_system.datamodels.DataPoints;

public class MovingAverage extends AbstractForcastModel
{
    private int K;
    public MovingAverage(List<DataPoints> buildingNumbers,List<DataPoints> auditData) 
    {
        this.buildingNumbers = new ArrayList<>(buildingNumbers);
        this.auditData = new ArrayList<>(auditData);
        this.errorList = new ArrayList<>();
        this.forecastList = new ArrayList<>();
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

        List<DataPoints> simulatedData = new ArrayList<>(buildingNumbers);
        double currentForecast = calculateAvg(this.K);
        List<DataPoints> predictionsForAudit = new ArrayList<>();

        for (int j = 0; j < auditData.size(); j++) {
            // שומרים את התחזית הנוכחית
            if(j==0)
                predictionsForAudit.add(new DataPoints(0,currentForecast));
            else
                predictionsForAudit.add(new DataPoints(predictionsForAudit.getLast().getIndex()+1,currentForecast));
            // מוסיפים את התחזית לרשימה המדומה כדי לחשב את הצעד הבא (חיזוי רקורסיבי)
            if(j==0)
                simulatedData.add(new DataPoints(0,currentForecast));
            else
                simulatedData.add(new DataPoints(simulatedData.getLast().getIndex()+1,currentForecast));

            double sum = 0;
            for (int i = 0; i < this.K; i++) {
                sum += simulatedData.get(simulatedData.size() - 1 - i).getValue();
            }
            currentForecast = sum / this.K;
        }

        //The calculation of the final MSE
        errorList.clear();
        for (int i = 0; i < auditData.size(); i++) 
        {
             double error = auditData.get(i).getValue() - predictionsForAudit.get(i).getValue();
             if(i==0)
                errorList.add(new DataPoints(0,error * error));
            else
                errorList.add(new DataPoints(errorList.getLast().getIndex()+1,error * error));
        }
        this.MSE = calculateMSE(); // קורא לפונקציית העזר הפרטית של המחלקה
        
        System.out.println("Moving Average Audit MSE: " + this.MSE);
        return this.MSE;
    }

    @Override
    public ArrayList<DataPoints> predict(int futureSteps) 
    {
        ArrayList<DataPoints> combined = new ArrayList<>(buildingNumbers);
        combined.addAll(auditData);
        for(int j = 0; j<futureSteps;j++)
        {
            double avg=0,forcast = 0,sum=0;
            for(int i = 0;i<K;i++)
            {
                sum+=combined.get(combined.size()-1-i).getValue();
            }
            avg = sum/K;
            forcast = avg;
            if(j==0)
            {
                combined.add(new DataPoints(0,forcast));
                forecastList.add(new DataPoints(0,forcast));
            }
            else
            {
                combined.add(new DataPoints(combined.getLast().getIndex()+1,forcast));
                forecastList.add(new DataPoints(forecastList.getLast().getIndex()+1,forcast));
            }
            
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
        for (DataPoints errorVal : errorList) 
        {
        sumErrors += errorVal.getValue();
        }

        mse = sumErrors/errorList.size();
        return mse;
    }

    private double calculateAvg(int k)
    {
        errorList.clear();

        double forcast=0,sum=0,errorSquered = 0;
        for(int i=0; i<k;i++)
            sum+=buildingNumbers.get(i).getValue();

        forcast = sum/k;

        for(int i = k; i<buildingNumbers.size(); i++)
        {
            double actualValue = buildingNumbers.get(i).getValue();

            double error = buildingNumbers.get(i).getValue()-forcast;
            errorSquered = error*error;
            if(errorList.isEmpty())
                errorList.add(new DataPoints(0, errorSquered));
            else
                errorList.add(new DataPoints(errorList.getLast().getIndex()+1,errorSquered));
            // 2. עדכון הסכום עבור התחזית הבאה (Sliding Window)
            // מורידים את האיבר הישן ביותר בחלון (i - k)
            // ומוסיפים את האיבר הנוכחי שנכנס לחלון (i)
            sum = sum - buildingNumbers.get(i - k).getValue() + actualValue;

            // 3. עדכון התחזית לאיטרציה הבאה
            forcast = sum/k;
        }
        
        return forcast;
    }
}

package orip.stocks_prediction_system.forcasting;

import java.util.ArrayList;
import java.util.List;

import orip.stocks_prediction_system.datamodels.DataPoints;


public class Average extends AbstractForcastModel
{
    private double average;

    public Average(List<DataPoints> buildingNumbers,List<DataPoints> auditData) 
    {
        this.buildingNumbers = new ArrayList<>(buildingNumbers);
        this.auditData = new ArrayList<>(auditData);
        this.forecastList = new ArrayList<>();
        this.errorList = new ArrayList<>();
        this.MSE =-1;
        totalSize = buildingNumbers.size()+auditData.size();
    
    }

    @Override
    public double CalculateMse() 
    {
         if(auditData.isEmpty())
        {
            System.out.println("ERROR: Real results list is empty");
                return -1.0;
        }

        double totalSum = 0;
        for (DataPoints dp : buildingNumbers) {
            totalSum += dp.getValue();
        }
        this.average = totalSum / buildingNumbers.size();

        double errorSum = 0;
        for(int i =0; i<auditData.size();i++)
        {
            errorSum+= Math.pow((auditData.get(i).getValue()-this.average),2);
            
        }
        
        MSE = errorSum/auditData.size();

        System.out.println("Simple average calculated: " + average);
        System.out.println("Simple average mse: "+MSE);
        return MSE;
    }
    @Override
    public ArrayList<DataPoints> predict(int futureSteps) 
    {
        for(int i = 0; i<futureSteps;i++)
            forecastList.add(new DataPoints(i,average));

        return forecastList;
    }

    
    
    
}

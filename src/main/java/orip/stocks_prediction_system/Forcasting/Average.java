package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

import orip.stocks_prediction_system.datamodels.DataPoints;


public class Average extends AbstractForcastModel
{
    private double average;

    public Average(ArrayList<DataPoints> buildingNumbers,ArrayList<DataPoints> auditData) 
    {
        this.buildingNumbers = buildingNumbers;
        this.auditData = auditData;
        this.MSE =-1;
        totalSize = buildingNumbers.size()+auditData.size();
    }

    @Override
    public double CalculateMse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'CalculateMse'");
    }

    @Override
    public ArrayList<DataPoints> predict(int futureSteps) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'predict'");
    }

    
    
    
}

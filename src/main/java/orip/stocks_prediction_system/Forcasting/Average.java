package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
@Component("Average")
public class Average extends AbstractForcastModel
{
    private double average;

    public Average(ArrayList<Double> buildingNumbers,ArrayList<Double> auditData) 
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
    public ArrayList<Double> predict(int futureSteps) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'predict'");
    }

    
    
    
}

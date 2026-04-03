package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

public abstract class AbstractForcastModel implements ForcastModel
{
    protected ArrayList<Double> buildingNumbers;
    protected ArrayList<Double> auditData;
    protected ArrayList<Double> errorList;
    protected ArrayList<Double> forecastList;
    protected double MSE;
    protected int totalSize;

    public ArrayList<Double> getBuildingNumbers() {
        return buildingNumbers;
    }
    public void setBuildingNumbers(ArrayList<Double> buildingNumbers) {
        this.buildingNumbers = buildingNumbers;
    }
    public ArrayList<Double> getAuditData() {
        return auditData;
    }
    public void setAuditData(ArrayList<Double> auditData) {
        this.auditData = auditData;
    }

    public int getTotalSize() {
        return totalSize;
    }
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
    public ArrayList<Double> getErrorList() {
        return errorList;
    }
    public ArrayList<Double> getForecastList() {
        return forecastList;
    }
    public double getMSE() {
        return MSE;
    }

    /**
     * Let's you add a number to the BuildingNumbers list
     * @param number - the number you want to add to the list
     */
    public void addNumber(Double number)
    {   
        buildingNumbers.add(number);
    }
    
}

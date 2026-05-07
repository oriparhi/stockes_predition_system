package orip.stocks_prediction_system.forcasting;

import java.util.ArrayList;

import orip.stocks_prediction_system.datamodels.DataPoints;

public abstract class AbstractForcastModel implements ForcastModel
{
    protected ArrayList<DataPoints> buildingNumbers;
    protected ArrayList<DataPoints> auditData;
    protected ArrayList<DataPoints> errorList;
    protected ArrayList<DataPoints> forecastList;
    protected double MSE;
    protected int totalSize;

    public ArrayList<DataPoints> getBuildingNumbers() {
        return buildingNumbers;
    }
    public void setBuildingNumbers(ArrayList<DataPoints> buildingNumbers) {
        this.buildingNumbers = buildingNumbers;
    }
    public ArrayList<DataPoints> getAuditData() {
        return auditData;
    }
    public void setAuditData(ArrayList<DataPoints> auditData) {
        this.auditData = auditData;
    }

    public int getTotalSize() {
        return totalSize;
    }
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
    
    public ArrayList<DataPoints> getErrorList() {
        return errorList;
    }
    public ArrayList<DataPoints> getForecastList() {
        return forecastList;
    }
    public double getMSE() {
        return MSE;
    }

    /**
     * Let's you add a number to the BuildingNumbers list
     * @param number - the number you want to add to the list
     */
    public void addBuildingNumber(Double number)
    {   
        DataPoints dp = new DataPoints(buildingNumbers.getLast().getIndex()+1,number);
        buildingNumbers.add(dp);
        this.totalSize++;
    }

    public void addAuditData(double num) 
    {
        DataPoints dp = new DataPoints(auditData.getLast().getIndex()+1,num);
        this.auditData.add(dp);
        this.totalSize++; // עדכון הגודל הכולל
    }
}

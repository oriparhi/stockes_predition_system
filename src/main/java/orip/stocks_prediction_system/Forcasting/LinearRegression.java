package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;
import java.util.List;

import orip.stocks_prediction_system.datamodels.DataPoints;

public class LinearRegression extends AbstractForcastModel 
{
    private ArrayList<DataPoints> X_axis;
    private int n;
    public LinearRegression(List<DataPoints> buildingNumbers,List<DataPoints> auditData) 
    {
        this.buildingNumbers = new ArrayList<>(buildingNumbers);
        this.auditData = new ArrayList<>(auditData);
        this.MSE =-1;
        this.n = buildingNumbers.size();
        totalSize = n+auditData.size();
        this.X_axis = new ArrayList<>();

        for(int i=0; i<totalSize;i++)
        {
            X_axis.add(new DataPoints(i,(double)i+1));
        }
    }
    public ArrayList<DataPoints> getX_axis() {
        return X_axis;
    }
    public void setX_axis(ArrayList<DataPoints> x_axis) {
        X_axis = x_axis;
    }
    public int getN() {
        return n;
    }
    public void setN(int n) {
        this.n = n;
    }


    @Override
    public double CalculateMse() 
    {
        ArrayList<DataPoints> trainingX = new ArrayList<>();
        for(int i=0; i<n; i++) {
            trainingX.add(X_axis.get(i));
        }

        double M = calculateM(trainingX,buildingNumbers);
        double B = calculateB(trainingX,buildingNumbers, M);

        forecastList.clear(); 
        errorList.clear();
        
        double sumErrorSq = 0;
        // 2. ביצוע חיזוי והשוואה רק עבור נתוני הבדיקה (Audit Data)
        // ה-Audit מתחיל לאחר ה-n איברים הראשונים
        for(int i = 0; i < auditData.size(); i++)
        {
            // שליפת ה-X המתאים לנתון הבדיקה הנוכחי
            // אם נתוני האימון הם אינדקסים 0 עד n-1, הבדיקה מתחילה ב-n
            double xVal = X_axis.get(n + i).getValue(); 
            
            double predictedY = (M * xVal) + B; 

            // שמירת התחזית
            if(i==0)
                forecastList.add(new DataPoints(0,predictedY)); 
            else
                forecastList.add(new DataPoints(forecastList.getLast().getIndex()+1,predictedY)); 

            // הנתון האמיתי
            double actualY = auditData.get(i).getValue();

            // חישוב השגיאה
            double error = actualY - predictedY;
            if(i==0)
                errorList.add(new DataPoints(0,error));
            else
                errorList.add(new DataPoints(errorList.getLast().getIndex()+1,error));
            
            // סיכום ריבועי השגיאות
            sumErrorSq += (error * error);
        }

        // 3. חישוב הממוצע (חילוק במספר איברי הבדיקה, לא האימון)
        if (auditData.size() > 0) {
            this.MSE = sumErrorSq / auditData.size();
        } else {
            this.MSE = 0;
        }
        System.out.println("Linear Regression mse: "+ this.MSE);
        return this.MSE;
    
    }

    @Override
    public ArrayList<DataPoints> predict(int futureSteps) 
    {
        forecastList.clear();
        ArrayList<DataPoints> combined = new ArrayList<>(buildingNumbers);
        combined.addAll(auditData);
        double M = calculateM(X_axis,combined);
        double B = calculateB(X_axis,combined, M);
        
        if(X_axis.size()<futureSteps)
        {
            for(int i = X_axis.size();i<futureSteps;i++)
                X_axis.add(new DataPoints(i,(double)i+1));
        }

        for(int j = 0; j<X_axis.size();j++)
        {
            if(j==0)
                forecastList.add(new DataPoints(0, M*X_axis.get(j).getValue()+B));
            else
                forecastList.add(new DataPoints(forecastList.getLast().getIndex()+1, M*X_axis.get(j).getValue()+B));
        }
        return forecastList;
    }
    
    private double calculateM(ArrayList<DataPoints> X,ArrayList<DataPoints> Y)
    {
        double currentN = Y.size();
        double sumX = 0,sumY=0,SumXY=0,SumSqX=0,SqSumX=0,nSumSqX=0,nSumXY=0,SumXSumY=0;
        for(int i=0;i<currentN;i++)
        {
            sumX+=X.get(i).getValue();
            SumSqX+=Math.pow(X.get(i).getValue(),2);
            sumY+=Y.get(i).getValue();
            SumXY+=(X.get(i).getValue()*Y.get(i).getValue());

        }
        SqSumX = Math.pow(sumX, 2);
        nSumSqX = currentN*SumSqX;
        nSumXY = currentN*SumXY;
        SumXSumY = sumX*sumY;

        double m = (nSumXY-SumXSumY)/(nSumSqX-SqSumX);
        System.out.println("LR M: "+m);
        return m;
    } 
    
    private double calculateB(ArrayList<DataPoints> X,ArrayList<DataPoints> Y,double M)
    {
        double currentN = Y.size();

        double sumX = 0,sumY = 0, b=0;
        for(int i = 0;i<Y.size();i++)
        {
            sumX+=X.get(i).getValue();
            sumY+=Y.get(i).getValue();
        }
        b=(sumY-M*sumX)/currentN;
        System.out.println("LR B: "+b);
        return b;
    }
}

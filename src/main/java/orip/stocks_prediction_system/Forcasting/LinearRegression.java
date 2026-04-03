package orip.stocks_prediction_system.Forcasting;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
@Component("LinearRegression")
public class LinearRegression extends AbstractForcastModel 
{
    private ArrayList <Double> X_axis;
    private int n;
    public LinearRegression(ArrayList<Double> buildingNumbers,ArrayList<Double> auditData) 
    {
        this.buildingNumbers = buildingNumbers;
        this.auditData = auditData;
        this.MSE =-1;
        this.n = buildingNumbers.size();
        totalSize = n+auditData.size();
        this.X_axis = new ArrayList<>();

        for(int i=0; i<totalSize;i++)
            X_axis.add((double)i+1);
    }
    public ArrayList<Double> getX_axis() {
        return X_axis;
    }
    public void setX_axis(ArrayList<Double> x_axis) {
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
        ArrayList<Double> trainingX = new ArrayList<>();
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
            double xVal = X_axis.get(n + i); 
            
            double predictedY = (M * xVal) + B; 
            forecastList.add(predictedY); // שמירת התחזית

            // הנתון האמיתי
            double actualY = auditData.get(i);

            // חישוב השגיאה
            double error = actualY - predictedY;
            errorList.add(error);
            
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
    public ArrayList<Double> predict(int futureSteps) 
    {
        forecastList.clear();
        ArrayList <Double> combined = new ArrayList<>(buildingNumbers);
        combined.addAll(auditData);
        double M = calculateM(X_axis,combined);
        double B = calculateB(X_axis,combined, M);
        
        if(X_axis.size()<futureSteps)
        {
            for(int i = X_axis.size();i<futureSteps;i++)
                X_axis.add((double)i+1);
        }

        for(int j = 0; j<X_axis.size();j++)
        {
           forecastList.add(M*X_axis.get(j)+B);
        }
        return forecastList;
    }
    
    private double calculateM(ArrayList <Double> X,ArrayList <Double> Y)
    {
        double currentN = Y.size();
        double sumX = 0,sumY=0,SumXY=0,SumSqX=0,SqSumX=0,nSumSqX=0,nSumXY=0,SumXSumY=0;
        for(int i=0;i<currentN;i++)
        {
            sumX+=X.get(i);
            SumSqX+=Math.pow(X.get(i),2);
            sumY+=Y.get(i);
            SumXY+=(X.get(i)*Y.get(i));

        }
        SqSumX = Math.pow(sumX, 2);
        nSumSqX = currentN*SumSqX;
        nSumXY = currentN*SumXY;
        SumXSumY = sumX*sumY;

        double m = (nSumXY-SumXSumY)/(nSumSqX-SqSumX);
        System.out.println("LR M: "+m);
        return m;
    } 
    
    private double calculateB(ArrayList <Double> X,ArrayList <Double> Y,double M)
    {
        double currentN = Y.size();

        double sumX = 0,sumY = 0, b=0;
        for(int i = 0;i<Y.size();i++)
        {
            sumX+=X.get(i);
            sumY+=Y.get(i);
        }
        b=(sumY-M*sumX)/currentN;
        System.out.println("LR B: "+b);
        return b;
    }
}

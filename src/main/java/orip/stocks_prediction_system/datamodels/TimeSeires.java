package orip.stocks_prediction_system.datamodels;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;

import orip.stocks_prediction_system.utilities.Interval;

@Document(collection = "TimeSeries")
public class TimeSeires 
{
    private String name;
    private Interval interval;
    private int dataLimit;
    private ArrayList<DataPoints> data;
    
    public TimeSeires(String name, Interval interval, int dataLimit, ArrayList<DataPoints> data) {
        this.name = name;
        this.interval = interval;
        this.dataLimit = dataLimit;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public int getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(int dataLimit) {
        this.dataLimit = dataLimit;
    }

    public ArrayList<DataPoints> getData() {
        return data;
    }

    public void setData(ArrayList<DataPoints> data) {
        this.data = data;
    }

    public void addData(DataPoints newData)
    {
        this.data.add(newData);
    }

    public DataPoints giveSpecificData(int n)
    {
        return data.get(n);
    }
}

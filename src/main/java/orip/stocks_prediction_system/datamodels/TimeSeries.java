package orip.stocks_prediction_system.datamodels;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import orip.stocks_prediction_system.utilities.Interval;

@Document(collection = "TimeSeries")
public class TimeSeries 
{
    @Id
    private String dataId; 
    private String name;
    private Interval interval;
    private int dataLimit;
    private ArrayList<DataPoints> data;
    
    //TODO: For cases that the API returns in LocalDate Format only, add default hour like 12:00
    public TimeSeries(String dataId,String name, Interval interval, int dataLimit, ArrayList<DataPoints> data) {
        this.dataId = dataId;
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

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @param series - The series where we want to search on
     * @param date - the specific date we want to get his value
     * @return - the value of the specific date. If this date doesn't exist, it returns null
     */
    public Double getValueByDate(TimeSeries series, LocalDateTime date) 
    {

        for (DataPoints p : series.getData()) 
        {

            if (p.getDate().equals(date)) 
            {
                return p.getValue();
            }

        }

        return null;
    }

    public Double getValueByMonth(TimeSeries series,int year, int month) 
    {

        for (DataPoints p : series.getData())  
        {

            if (p.getDate().getYear() == year && p.getDate().getMonthValue() == month) 
            {

                return p.getValue();
            }
        }

        return null;
    }
}

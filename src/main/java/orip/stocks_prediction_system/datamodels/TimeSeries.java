package orip.stocks_prediction_system.datamodels;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import orip.stocks_prediction_system.utilities.Interval;

@Document(collection = "TimeSeries")
public class TimeSeries 
{
    @Id
    private String timeSeriesId; 
    private String name;
    private List<DataPoints> data;
    private LocalDateTime createdAt;
    private String creator;
    private Interval interval;


    public TimeSeries(String name, List<DataPoints> data, LocalDateTime createdAt, String creator) 
    {
        this.name = name;
        this.data = data;
        this.createdAt = createdAt;
        this.creator = creator;
    }
    

    public TimeSeries(String name, List<DataPoints> data, LocalDateTime createdAt, String creator, Interval interval) {
        this.name = name;
        this.data = data;
        this.createdAt = createdAt;
        this.creator = creator;
        this.interval = interval;
    }


    public Interval getInterval() {
        return interval;
    }


    public String getTimeSeriesId() {
        return timeSeriesId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataPoints> getData() {
        return data;
    }

    public void setData(List<DataPoints> data) {
        this.data = data;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getcreator() {
        return creator;
    }

    public void setcreator(String creator) {
        this.creator = creator;
    }

    /**
     * @param series - The series where we want to search on
     * @param index - the specific index we want to get index doesn't exist, it returns null
     */
    public Double getValueByDate(int index) 
    {

        for (DataPoints p : data) 
        {

            if (p.getIndex() == index) 
            {
                return p.getValue();
            }

        }

        return null;
    }

    // public Double getValueByMonth(int year, int month) 
    // {

    //     for (DataPoints p : data)  
    //     {

    //         if (p.getDate().getYear() == year && p.getDate().getMonthValue() == month) 
    //         {

    //             return p.getValue();
    //         }
    //     }

    //     return null;
    // }
}

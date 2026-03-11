package orip.stocks_prediction_system.datamodels;

import java.time.LocalDateTime;

public class DataPoints 
{
    private LocalDateTime date;
    private double value;

    public DataPoints(LocalDateTime date, double value) {
        this.date = date;
        this.value = value;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}

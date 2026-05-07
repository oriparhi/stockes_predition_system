package orip.stocks_prediction_system.datamodels;


//TODO: For cases that the API returns in Localindex Format only, add default hour like 12:00
public class DataPoints 
{
    private int index;
    private double value;

    public DataPoints(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public DataPoints(double value) {
        this.value = value;
    }

    public DataPoints() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DataPoints [index=" + index + ", value=" + value + "]";
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}

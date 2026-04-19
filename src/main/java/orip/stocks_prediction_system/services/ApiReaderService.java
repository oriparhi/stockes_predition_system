package orip.stocks_prediction_system.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.utilities.Interval;

@Service
public class ApiReaderService {

    public static List<DataPoints> callApi(String symbol, Interval interval, int outputsize) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'callApi'");
    }

    public static String CreateNewTimeSeries(String username, LocalDateTime now, List<DataPoints> apiData,
            String symbol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'CreateNewTimeSeries'");
    }
    
}

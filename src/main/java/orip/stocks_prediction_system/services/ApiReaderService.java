package orip.stocks_prediction_system.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;
import orip.stocks_prediction_system.utilities.Interval;

@Service
public class ApiReaderService {
    private TimeSeriesRepo timeSeriesRepo;
    private static final String BASE_URL = "https://api.twelvedata.com/time_series";

    private final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
    private final String apiKey;
    private final String order;


    public ApiReaderService(TimeSeriesRepo timeSeriesRepo, @Value("${twelvedata.api.key}") String apiKey) 
    {
        this.timeSeriesRepo = timeSeriesRepo;
        this.apiKey = apiKey;
        this.order = "asc";
    }

    public List<DataPoints> getStockData(String symbol, Interval interval, int outputSize) 
    {
        // בניית ה-URL
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("interval", interval.getApiValue())
                .addQueryParameter("outputsize", String.valueOf(outputSize))
                .addQueryParameter("order", this.order)
                .addQueryParameter("apikey", this.apiKey)
                .build();
    

        Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build();
        try (Response response = client.newCall(request).execute())
        {
            if(response.isSuccessful() && response.body()!=null)
            {
                String jsonString = response.body().string();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonString);
                
                List<DataPoints> prices = new ArrayList<>();
                int index = 0;
                JsonNode valuesNode = root.path("values");
                if(valuesNode.isArray())
                {
                    for(JsonNode node :valuesNode)
                    {
                        double value = node.path("close").asDouble();
                        prices.add(new DataPoints(index, value));
                        index++;
                    }
                }
                return prices;
            }
            else 
            {
                throw new IOException("Unexpected code " + response);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }

    public String CreateNewTimeSeries(String Creator, LocalDateTime CreatedAt,List<DataPoints> data, String stockSymbol,Interval interval)
    {
        String mesurmentUnit = "$";
        TimeSeries newTimeSeries = new TimeSeries(stockSymbol+" stocks", data, CreatedAt, Creator, interval,mesurmentUnit);
        timeSeriesRepo.insert(newTimeSeries);
        String newTimeSeriesId = newTimeSeries.getTimeSeriesId();
        System.out.println("newTimeSeriesId: "+newTimeSeriesId);
        return newTimeSeriesId;
    }
    
}

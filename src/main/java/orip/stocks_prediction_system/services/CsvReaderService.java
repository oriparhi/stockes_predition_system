package orip.stocks_prediction_system.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.DataPoints;
import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;

@Service
public class CsvReaderService 
{
    private TimeSeriesRepo timeSeriesRepo;
    private String fileName;
    private List<DataPoints> data;
    private String creator;
    private LocalDateTime createdAt;

    public CsvReaderService(TimeSeriesRepo timeSeriesRepo) {
        this.timeSeriesRepo = timeSeriesRepo;
        data = new ArrayList<>();
        
    }

    public boolean ReadCsv(InputStream inputStream, String fileName)
    {
        this.fileName = fileName;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            boolean isHeader = true;
            int currentIndex = 1;

            while((line = reader.readLine())!=null)
            {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if(!line.trim().isEmpty())
                {
                    try 
                    {
                        double value = Double.parseDouble(line.trim());
                        data.add(new DataPoints(currentIndex, value));
                        currentIndex++;    
                    } catch (Exception ex) {
                        System.out.println("Error in file "+fileName+"! There is a problem in a line");
                        System.out.println(ex);
                        
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.out.println("Error in reading the file "+fileName);
            System.out.println(e);
            return false;
        }
        return true;
    }

    public String CreateNewTimeSeries(String creator, LocalDateTime createdAt)
    {
        this.creator = creator;
        this.createdAt = createdAt;
        TimeSeries newTimeSeries = new TimeSeries(fileName, data, createdAt, creator);
        String newTimeSeriesId = timeSeriesRepo.insert(newTimeSeries).getTimeSeriesId();
        return newTimeSeriesId;

    }
}

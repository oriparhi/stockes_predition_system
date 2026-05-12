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
import orip.stocks_prediction_system.utilities.Interval;
import orip.stocks_prediction_system.utilities.ReadCsvResponse;

@Service
public class CsvReaderService 
{
    private TimeSeriesRepo timeSeriesRepo;
    private String creator;
    private LocalDateTime createdAt;

    public CsvReaderService(TimeSeriesRepo timeSeriesRepo) {
        this.timeSeriesRepo = timeSeriesRepo;
        
    }

    public ReadCsvResponse ReadCsv(InputStream inputStream, String FileName)
    {
        List<DataPoints> data = new ArrayList<>();
        String fileName = FileName;
        String header = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            boolean isHeader = true;
            int currentIndex = 1;

            while((line = reader.readLine())!=null)
            {
                if (isHeader && !line.trim().isEmpty()) {
                    header = line.trim();
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
                        return null;
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.out.println("Error in reading the file "+fileName);
            System.out.println(e);
            return null;
        }
        return new ReadCsvResponse(data,header);
    }

    public String CreateNewTimeSeries(String Creator, LocalDateTime CreatedAt,List<DataPoints> data, String fileName,String title,Interval interval)
    {
        String creator = Creator;
        LocalDateTime createdAt = CreatedAt;
        TimeSeries newTimeSeries = new TimeSeries(fileName, data, createdAt, creator,interval,title);
        timeSeriesRepo.insert(newTimeSeries);
        String newTimeSeriesId = newTimeSeries.getTimeSeriesId();
        System.out.println("newTimeSeriesId: "+newTimeSeriesId);
        return newTimeSeriesId;
    }
}

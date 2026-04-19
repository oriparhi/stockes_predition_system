package orip.stocks_prediction_system.services;

import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.repositories.ForcastRequestRepository;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;

@Service
public class ForcastingService 
{
    TimeSeriesRepo timeSeriesRepo;
    ForcastRequestRepository forcastRequestRepository;
    private boolean isItSeasonality;
    private int seasonalityPeriod;

    public ForcastingService(TimeSeriesRepo timeSeriesRepo, ForcastRequestRepository forcastRequestRepository) 
    {
        this.timeSeriesRepo = timeSeriesRepo;
        this.forcastRequestRepository = forcastRequestRepository;
    }

    public boolean isItSeasonality() {
        return isItSeasonality;
    }

    public void setItSeasonality(boolean isItSeasonality) {
        this.isItSeasonality = isItSeasonality;
    }

    public int getSeasonalityPeriod() {
        return seasonalityPeriod;
    }

    public void setSeasonalityPeriod(String timeSeriesId ,int seasonalityPeriod) 
    {
        this.seasonalityPeriod = seasonalityPeriod;
        if(timeSeriesRepo.existsById(timeSeriesId))
        {
            TimeSeries ts = timeSeriesRepo.findById(timeSeriesId).orElseThrow();
            ts.setSeasonalityPeriod(seasonalityPeriod);
            timeSeriesRepo.save(ts);
        }
        
    }
    
}

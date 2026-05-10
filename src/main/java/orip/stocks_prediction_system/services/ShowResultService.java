package orip.stocks_prediction_system.services;


import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.repositories.ForcastRequestRepository;
import orip.stocks_prediction_system.repositories.ForcastResultRepository;
import orip.stocks_prediction_system.repositories.TimeSeriesRepo;
import orip.stocks_prediction_system.utilities.Interval;

@Service
public class ShowResultService 
{
    ForcastResultRepository forcastResultRepository;
    ForcastRequestRepository forcastRequestRepository;
    TimeSeriesRepo timeSeriesRepo;
    
    public ShowResultService(ForcastResultRepository forcastResultRepository, ForcastRequestRepository forcastRequestRepository,
        TimeSeriesRepo timeSeriesRepo) {
        this.forcastResultRepository = forcastResultRepository;
        this.forcastRequestRepository = forcastRequestRepository;
        this.timeSeriesRepo = timeSeriesRepo;
    }

    public ForcastResult loadForcast(String forcastResultsId) 
    {
        if(forcastResultRepository.existsById(forcastResultsId))
        {
            ForcastResult FResult = forcastResultRepository.findById(forcastResultsId).orElseThrow();
            return FResult;
        }
        else return null;
    }

    public Integer getPredictionHorizon(ForcastResult forcastResult) 
    {
        String forcastRequestId = forcastResult.getRequestId();
        if(forcastRequestRepository.existsById(forcastRequestId))
        {
            ForcastRequest FRequest = forcastRequestRepository.findById(forcastRequestId).orElseThrow();
            return FRequest.getPredictionHorizon();
        }
        else return null;
    }

    public Interval getInterval(ForcastResult forcastResult)
    {
        String forcastRequestId = forcastResult.getRequestId();
        if(forcastRequestRepository.existsById(forcastRequestId))
        {
            ForcastRequest FRequest = forcastRequestRepository.findById(forcastRequestId).orElseThrow();
            String timeSeriesId = FRequest.getTimeSeriesId();
            if(timeSeriesRepo.existsById(timeSeriesId))
            {
                TimeSeries ts = timeSeriesRepo.findById(timeSeriesId).orElseThrow();
                Interval interval = ts.getInterval();
                return interval;
            }
            else return null;
        }
        else return null;
    }
    
}

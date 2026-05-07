package orip.stocks_prediction_system.services;

import org.springframework.stereotype.Service;

import orip.stocks_prediction_system.datamodels.ForcastResult;
import orip.stocks_prediction_system.repositories.ForcastResultRepository;

@Service
public class ShowResultService 
{
    ForcastResultRepository forcastResultRepository;
    
    public ShowResultService(ForcastResultRepository forcastResultRepository) {
        this.forcastResultRepository = forcastResultRepository;
    }

    public ForcastResult loadForcast(String forcastResultsId) 
    {
        if(forcastResultRepository.existsById(forcastResultsId))
        {
            ForcastResult FR = forcastResultRepository.findById(forcastResultsId).orElseThrow();
            return FR;
        }
        return null;
    }
    
}

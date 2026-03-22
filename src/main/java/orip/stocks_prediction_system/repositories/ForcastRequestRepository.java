package orip.stocks_prediction_system.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.utilities.Interval;

@Repository
public interface ForcastRequestRepository 
{
    public List<ForcastRequest> FindByinterval(Interval interval);

    public List<ForcastRequest> FindBytimeSeriesId(String timeSeriesId);
}

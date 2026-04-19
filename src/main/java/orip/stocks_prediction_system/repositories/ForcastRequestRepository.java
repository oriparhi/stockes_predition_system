package orip.stocks_prediction_system.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.utilities.Interval;

@Repository
public interface ForcastRequestRepository extends MongoRepository<ForcastRequest,String>
{
    // public List<ForcastRequest> findByinterval(Interval interval);

    public List<ForcastRequest> findBytimeSeriesId(String timeSeriesId);
}

package orip.stocks_prediction_system.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastRequest;


@Repository
public interface ForcastRequestRepository extends MongoRepository<ForcastRequest,String>
{

    public List<ForcastRequest> findBytimeSeriesId(String timeSeriesId);
    
    public List<ForcastRequest> findAllByAlgorithem(String Algorithem);

    public List<ForcastRequest> findAllByItSeasonality(boolean itSeasonality);
}

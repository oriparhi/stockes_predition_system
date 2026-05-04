package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastRequest;


@Repository
public interface ForcastRequestRepository extends MongoRepository<ForcastRequest,String>
{

    public List<ForcastRequest> findBytimeSeriesId(String timeSeriesId);
    
    public List<ForcastRequest> findAllByAlgorithm(String Algorithm);


    public List<ForcastRequest> findAllByRequestedBy(String requestedBy);

    public List<ForcastRequest> findAllByRequestedAtBetween(LocalDateTime start, LocalDateTime end);

    public List<ForcastRequest> findAllByRequestedByAndRequestedAtBetween(String requestedBy, LocalDateTime start, LocalDateTime end);

    public ForcastRequest findOneByRequestedByAndRequestedAt(String requestedBy, LocalDateTime requestedAt);

}

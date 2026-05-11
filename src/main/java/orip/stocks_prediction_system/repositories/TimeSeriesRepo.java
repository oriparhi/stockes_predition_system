package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastRequest;
import orip.stocks_prediction_system.datamodels.TimeSeries;

@Repository
public interface TimeSeriesRepo extends MongoRepository<TimeSeries, String>
{
   public List<TimeSeries> findAllByDataName(String dataName);

   public TimeSeries findOneByDataNameAndCreator(String dataName,String creator);

   public List<TimeSeries> findByDataNameLike(String dataName);

   public TimeSeries findOneByDataNameAndCreatedAt(String dataName, LocalDateTime createdAt);

   // public TimeSeries findOneById(String timeSeriesId);

   public List<TimeSeries> findByCreator(String creator);

   public List<TimeSeries> findByCreatedAt(LocalDateTime createdAt);

   public List<ForcastRequest> findAllByItSeasonality(boolean itSeasonality);

}

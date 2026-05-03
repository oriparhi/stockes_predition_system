package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.TimeSeries;

@Repository
public interface TimeSeriesRepo extends MongoRepository<TimeSeries, String>
{
   public List<TimeSeries> findAllByName(String name);

   public TimeSeries findOneByNameAndCreator(String name,String creator);

   public List<TimeSeries> findByNameLike(String name);

   public TimeSeries findOneByNameAndCreatedAt(String name, LocalDateTime createdAt);

   // public TimeSeries findOneById(String timeSeriesId);

   public List<TimeSeries> findByCreator(String creator);

   public List<TimeSeries> findByCreatedAt(LocalDateTime createdAt);

}

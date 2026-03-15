package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.utilities.Interval;

@Repository
public interface TimeSeriesRepo extends MongoRepository<TimeSeries, String>
{
   public List<TimeSeries> findAllByName(String name);

   public TimeSeries findOneByIdAndName(String dataId, String name);

   public List<TimeSeries> findByNameLike(String name);

   public List<TimeSeries> findByInterval(Interval interval);

}

package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.TimeSeries;
import orip.stocks_prediction_system.utilities.Interval;

@Repository
public interface TimeSeriesReop extends MongoRepository<TimeSeries, String>
{
   public List<TimeSeries> findAllByTimeSeriesName(String name);

   public TimeSeries findOneByIdAndName(String Id, String name);

   public List<TimeSeries> findByTimeSeriesNameLike(String name);

   public List<TimeSeries> findByInterval(Interval interval);

}

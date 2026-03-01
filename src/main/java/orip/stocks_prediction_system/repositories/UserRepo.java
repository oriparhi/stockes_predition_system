package orip.stocks_prediction_system.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.User;


@Repository
public interface UserRepo extends MongoRepository <User,String>
{
    
    
} 

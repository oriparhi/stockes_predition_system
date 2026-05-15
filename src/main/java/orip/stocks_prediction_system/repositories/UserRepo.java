package orip.stocks_prediction_system.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.User;


@Repository
public interface UserRepo extends MongoRepository <User,String>
{
   public List<User> findAllByUsername(String name);

   User findByUsername(String username);

   public List<User> findByUsernameLike(String name);

   public List<User> findByEmail(String email);

   public User findOneByUsernameAndPassword(String un, String pw);
    
} 

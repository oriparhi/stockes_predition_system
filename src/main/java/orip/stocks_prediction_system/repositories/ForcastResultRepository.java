package orip.stocks_prediction_system.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import orip.stocks_prediction_system.datamodels.ForcastResult;

@Repository
public interface ForcastResultRepository extends MongoRepository<ForcastResult, String>
{
    // מציאת התוצאה לפי מזהה הבקשה המקורית
    Optional<ForcastResult> findByRequestId(String requestId);

    // מציאת כל התוצאות שהופקו על ידי אלגוריתם מסוים
    List<ForcastResult> findAllByAlgorithemUsed(String algorithemUsed);

    // שליפת כל התוצאות שה-MSE שלהן קטן מערך מסוים
    List<ForcastResult> findByMseLessThan(double maxMse);
    
    // שליפת כל התוצאות שה-MSE שלהן קטן מערך מסוים, מסודרות מהמדויק ביותר להכי פחות מדויק
    List<ForcastResult> findByMseLessThanOrderByMseAsc(double maxMse);

    List<ForcastResult> findAllByCreatedBy(String createdBy);

    List<ForcastResult> findAllByResultDateBetween(LocalDateTime start, LocalDateTime end);

    List<ForcastResult> findAllByCreatedByAndResultDateBetween(String createdBy, LocalDateTime start, LocalDateTime end);

    ForcastResult findOneByCreatedByAndResultDate(String createdBy, LocalDateTime resultDate);
}

package tudor.work.repository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Long> {

}

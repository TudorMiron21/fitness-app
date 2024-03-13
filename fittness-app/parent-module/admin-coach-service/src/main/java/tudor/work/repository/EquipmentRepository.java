package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Equipment;

import java.util.Optional;

@Repository

public interface EquipmentRepository extends JpaRepository<Equipment,Long> {
    Optional<Equipment> findByName(String equipmentName);
}

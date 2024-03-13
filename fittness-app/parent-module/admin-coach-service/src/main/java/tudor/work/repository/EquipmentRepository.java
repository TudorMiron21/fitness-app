package tudor.work.repository;

import org.springframework.stereotype.Repository;
import tudor.work.model.Equipment;

import java.util.Optional;

@Repository

public interface EquipmentRepository {
    Optional<Equipment> findByName(String equipmentName);
}

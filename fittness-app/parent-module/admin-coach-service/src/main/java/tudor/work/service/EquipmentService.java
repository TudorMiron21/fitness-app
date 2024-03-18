package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Equipment;
import tudor.work.repository.EquipmentRepository;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public Equipment getEquipmentByName(String equipmentName) throws NotFoundException {
        return equipmentRepository.findByName(equipmentName).orElseThrow(() -> new NotFoundException("equipment with name " + equipmentName + " not found "));
    }

}

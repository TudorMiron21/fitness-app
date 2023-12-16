package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Equipment;
import tudor.work.repository.EquipmentRepository;

@RequiredArgsConstructor
@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public Equipment getEquipmentByName(String name) throws NotFoundException {
        return equipmentRepository.getByName(name).orElseThrow(()->new NotFoundException("equipment not found"));
    }
}

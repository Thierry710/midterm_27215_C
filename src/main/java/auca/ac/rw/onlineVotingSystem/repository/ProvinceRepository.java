package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Optional<Province> findByCode(String code);
    Optional<Province> findByName(String name);
    boolean existsByCode(String code);
}

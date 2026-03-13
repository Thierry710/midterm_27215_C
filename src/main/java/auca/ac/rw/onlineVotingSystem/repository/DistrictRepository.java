package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByName(String name);
}

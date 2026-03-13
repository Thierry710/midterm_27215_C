package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Village;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VillageRepository extends JpaRepository<Village, Long> {
    boolean existsByName(String name);
}

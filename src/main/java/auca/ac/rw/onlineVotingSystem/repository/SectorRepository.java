package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsByName(String name);
}

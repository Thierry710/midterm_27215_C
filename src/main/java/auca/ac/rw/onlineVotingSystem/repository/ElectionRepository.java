package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ElectionRepository extends JpaRepository<Election, Long> {
    Optional<Election> findByActiveTrue();
}

package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CellRepository extends JpaRepository<Cell, Long> {
    boolean existsByName(String name);
}

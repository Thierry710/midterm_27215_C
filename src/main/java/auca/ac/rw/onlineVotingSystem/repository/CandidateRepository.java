package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByApprovedTrue();
    Page<Candidate> findByApprovedTrue(Pageable pageable);
    boolean existsByNameAndElectionId(String name, Long electionId);
}

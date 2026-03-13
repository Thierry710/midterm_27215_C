package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.model.VoterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VoterProfileRepository extends JpaRepository<VoterProfile, Long> {
    boolean existsByNationalId(String nationalId);
}

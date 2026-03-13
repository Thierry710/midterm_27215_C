package auca.ac.rw.onlineVotingSystem.api;

import auca.ac.rw.onlineVotingSystem.model.Candidate;
import auca.ac.rw.onlineVotingSystem.model.Election;
import auca.ac.rw.onlineVotingSystem.repository.CandidateRepository;
import auca.ac.rw.onlineVotingSystem.repository.ElectionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
public class CandidateApiController {

    private final CandidateRepository candidateRepo;
    private final ElectionRepository electionRepo;

    public CandidateApiController(CandidateRepository candidateRepo, ElectionRepository electionRepo) {
        this.candidateRepo = candidateRepo;
        this.electionRepo  = electionRepo;
    }

    @GetMapping
    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        return candidateRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/approved")
    public List<Candidate> getApprovedCandidates() {
        return candidateRepo.findByApprovedTrue();
    }

    @PostMapping
    public ResponseEntity<?> createCandidate(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        if (name == null) return ResponseEntity.badRequest().body("Required: name");

        Election election = null;
        if (body.containsKey("electionId")) {
            Long electionId = Long.valueOf(body.get("electionId").toString());
            election = electionRepo.findById(electionId).orElse(null);
        } else {
            election = electionRepo.findByActiveTrue().orElse(null);
        }
        if (election == null) return ResponseEntity.badRequest().body("No active election found");

        if (candidateRepo.existsByNameAndElectionId(name, election.getId())) {
            return ResponseEntity.badRequest().body("Candidate already registered for this election");
        }

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setElection(election);
        candidate.setApproved(false);
        candidate.setVotes(0);
        return ResponseEntity.ok(candidateRepo.save(candidate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Candidate candidate = candidateRepo.findById(id).orElse(null);
        if (candidate == null) return ResponseEntity.notFound().build();

        if (body.containsKey("name"))     candidate.setName((String) body.get("name"));
        if (body.containsKey("approved")) candidate.setApproved((Boolean) body.get("approved"));
        if (body.containsKey("votes"))    candidate.setVotes((Integer) body.get("votes"));

        return ResponseEntity.ok(candidateRepo.save(candidate));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveCandidate(@PathVariable Long id) {
        Candidate candidate = candidateRepo.findById(id).orElse(null);
        if (candidate == null) return ResponseEntity.notFound().build();
        candidate.setApproved(true);
        return ResponseEntity.ok(candidateRepo.save(candidate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        if (!candidateRepo.existsById(id)) return ResponseEntity.notFound().build();
        candidateRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted successfully", "id", id));
    }
}

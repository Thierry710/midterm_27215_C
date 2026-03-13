package auca.ac.rw.onlineVotingSystem.api;

import auca.ac.rw.onlineVotingSystem.model.Election;
import auca.ac.rw.onlineVotingSystem.repository.ElectionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elections")
public class ElectionApiController {

    private final ElectionRepository electionRepo;

    public ElectionApiController(ElectionRepository electionRepo) {
        this.electionRepo = electionRepo;
    }

    @GetMapping
    public List<Election> getAllElections() {
        return electionRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable Long id) {
        return electionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveElection() {
        return electionRepo.findByActiveTrue()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createElection(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        if (title == null) return ResponseEntity.badRequest().body("Required: title");

        electionRepo.findByActiveTrue().ifPresent(e -> {
            e.setActive(false);
            electionRepo.save(e);
        });

        Election election = new Election();
        election.setTitle(title);
        election.setActive(true);
        return ResponseEntity.ok(electionRepo.save(election));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateElection(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Election election = electionRepo.findById(id).orElse(null);
        if (election == null) return ResponseEntity.notFound().build();

        if (body.containsKey("title"))  election.setTitle((String) body.get("title"));
        if (body.containsKey("active")) election.setActive((Boolean) body.get("active"));

        return ResponseEntity.ok(electionRepo.save(election));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable Long id) {
        if (!electionRepo.existsById(id)) return ResponseEntity.notFound().build();
        electionRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Election deleted successfully", "id", id));
    }
}

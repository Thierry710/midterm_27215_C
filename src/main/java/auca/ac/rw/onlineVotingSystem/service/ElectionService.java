package auca.ac.rw.onlineVotingSystem.service;

import auca.ac.rw.onlineVotingSystem.model.Election;
import auca.ac.rw.onlineVotingSystem.repository.ElectionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ElectionService {

    private final ElectionRepository electionRepo;

    public ElectionService(ElectionRepository electionRepo) {
        this.electionRepo = electionRepo;
    }

    public Optional<Election> getActiveElection() {
        return electionRepo.findByActiveTrue();
    }

    public Election createElection(String title) {
        electionRepo.findByActiveTrue().ifPresent(e -> {
            e.setActive(false);
            electionRepo.save(e);
        });
        Election election = new Election();
        election.setTitle(title);
        election.setActive(true);
        return electionRepo.save(election);
    }
}

package auca.ac.rw.onlineVotingSystem.service;

import auca.ac.rw.onlineVotingSystem.model.Candidate;
import auca.ac.rw.onlineVotingSystem.model.Election;
import auca.ac.rw.onlineVotingSystem.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepo;

    public CandidateService(CandidateRepository candidateRepo) {
        this.candidateRepo = candidateRepo;
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    public List<Candidate> getApprovedCandidates() {
        return candidateRepo.findByApprovedTrue();
    }

    public void registerCandidate(String name, Election election) {
        if (candidateRepo.existsByNameAndElectionId(name, election.getId())) return;
        Candidate c = new Candidate();
        c.setName(name);
        c.setElection(election);
        candidateRepo.save(c);
    }

    public void approveCandidate(Long id) {
        candidateRepo.findById(id).ifPresent(c -> {
            c.setApproved(true);
            candidateRepo.save(c);
        });
    }
}

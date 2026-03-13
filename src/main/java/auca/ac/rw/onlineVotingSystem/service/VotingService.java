package auca.ac.rw.onlineVotingSystem.service;

import auca.ac.rw.onlineVotingSystem.model.Candidate;
import auca.ac.rw.onlineVotingSystem.model.User;
import auca.ac.rw.onlineVotingSystem.repository.CandidateRepository;
import auca.ac.rw.onlineVotingSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class VotingService {

    private final UserRepository userRepo;
    private final CandidateRepository candidateRepo;

    public VotingService(UserRepository userRepo, CandidateRepository candidateRepo) {
        this.userRepo = userRepo;
        this.candidateRepo = candidateRepo;
    }

    public boolean hasVoted(String username) {
        return userRepo.findByUsername(username)
                .map(User::isHasVoted)
                .orElse(false);
    }

    public boolean castVote(Long candidateId, String username) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null || Boolean.TRUE.equals(user.isHasVoted())) return false;

        Candidate candidate = candidateRepo.findById(candidateId).orElse(null);
        if (candidate == null) return false;

        candidate.setVotes(candidate.getVotes() + 1);
        candidateRepo.save(candidate);

        user.setHasVoted(true);
        userRepo.save(user);
        return true;
    }
}

package auca.ac.rw.onlineVotingSystem.controller;

import auca.ac.rw.onlineVotingSystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/voter")
public class VoterController {

    private final CandidateService candidateService;
    private final ElectionService electionService;
    private final VotingService votingService;

    public VoterController(CandidateService candidateService, ElectionService electionService,
                           VotingService votingService) {
        this.candidateService = candidateService;
        this.electionService  = electionService;
        this.votingService    = votingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("candidates", candidateService.getApprovedCandidates());
        model.addAttribute("hasVoted",   votingService.hasVoted(auth.getName()));
        model.addAttribute("election",   electionService.getActiveElection().orElse(null));
        return "voter-dashboard";
    }

    @PostMapping("/vote/{id}")
    public String vote(@PathVariable Long id, Authentication auth) {
        boolean success = votingService.castVote(id, auth.getName());
        return success
                ? "redirect:/voter/dashboard?success"
                : "redirect:/voter/dashboard?alreadyVoted";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("candidates", candidateService.getAllCandidates());
        model.addAttribute("backUrl", "/voter/dashboard");
        return "results";
    }
}

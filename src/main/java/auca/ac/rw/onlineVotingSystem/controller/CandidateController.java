package auca.ac.rw.onlineVotingSystem.controller;

import auca.ac.rw.onlineVotingSystem.model.Election;
import auca.ac.rw.onlineVotingSystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final ElectionService electionService;
    private final CandidateService candidateService;

    public CandidateController(ElectionService electionService, CandidateService candidateService) {
        this.electionService = electionService;
        this.candidateService = candidateService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("election", electionService.getActiveElection().orElse(null));
        model.addAttribute("username", auth.getName());
        return "candidate-dashboard";
    }

    @PostMapping("/register")
    public String register(Authentication auth) {
        Election election = electionService.getActiveElection().orElse(null);
        if (election == null) return "redirect:/candidate/dashboard?noElection";
        candidateService.registerCandidate(auth.getName(), election);
        return "redirect:/candidate/dashboard?registered";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("candidates", candidateService.getAllCandidates());
        model.addAttribute("backUrl", "/candidate/dashboard");
        return "results";
    }
}

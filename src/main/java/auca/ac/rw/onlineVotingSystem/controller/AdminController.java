package auca.ac.rw.onlineVotingSystem.controller;

import auca.ac.rw.onlineVotingSystem.model.User;
import auca.ac.rw.onlineVotingSystem.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final UserService userService;
    private final LocationService locationService;

    public AdminController(ElectionService electionService, CandidateService candidateService,
                           UserService userService, LocationService locationService) {
        this.electionService = electionService;
        this.candidateService = candidateService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("election",    electionService.getActiveElection().orElse(null));
        model.addAttribute("candidates",  candidateService.getAllCandidates());
        model.addAttribute("totalVoters", userService.countVoters());
        model.addAttribute("votedCount",  userService.countVoted());
        return "admin-dashboard";
    }

    @PostMapping("/create-election")
    public String createElection(@RequestParam String title) {
        electionService.createElection(title);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveCandidate(@PathVariable Long id) {
        candidateService.approveCandidate(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("candidates", candidateService.getAllCandidates());
        model.addAttribute("backUrl", "/admin/dashboard");
        return "results";
    }

    @GetMapping("/voters")
    public String manageVoters(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        Page<User> votersPage = userService.getVotersPaginated(page, size);
        model.addAttribute("votersPage",   votersPage);
        model.addAttribute("currentPage",  page);
        model.addAttribute("totalPages",   votersPage.getTotalPages());
        return "manage-voters";
    }

    @GetMapping("/voters/province")
    public String votersByProvince(@RequestParam String search, Model model) {
        model.addAttribute("voters", locationService.getUsersByProvince(search));
        model.addAttribute("search", search);
        return "voters-by-province";
    }

    @GetMapping("/locations")
    public String locations(Model model) {
        model.addAttribute("provinces", locationService.getAllProvinces());
        model.addAttribute("districts", locationService.getAllDistricts());
        model.addAttribute("sectors",   locationService.getAllSectors());
        model.addAttribute("cells",     locationService.getAllCells());
        model.addAttribute("villages",  locationService.getAllVillages());
        return "locations";
    }
}

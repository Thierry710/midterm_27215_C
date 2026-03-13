package auca.ac.rw.onlineVotingSystem.api;

import auca.ac.rw.onlineVotingSystem.enums.UserRole;
import auca.ac.rw.onlineVotingSystem.model.User;
import auca.ac.rw.onlineVotingSystem.model.Village;
import auca.ac.rw.onlineVotingSystem.repository.UserRepository;
import auca.ac.rw.onlineVotingSystem.repository.VillageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserRepository userRepo;
    private final VillageRepository villageRepo;
    private final PasswordEncoder passwordEncoder;

    public UserApiController(UserRepository userRepo, VillageRepository villageRepo,
                             PasswordEncoder passwordEncoder) {
        this.userRepo        = userRepo;
        this.villageRepo     = villageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<User> users = userRepo.findAll().stream()
                    .filter(u -> u.getRole() == userRole)
                    .toList();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Use: ADMIN, VOTER, or CANDIDATE");
        }
    }

    @GetMapping("/by-village/{name}")
    public ResponseEntity<?> getUsersByVillage(@PathVariable String name) {
        List<User> users = userRepo.findByVillageName(name);
        if (users.isEmpty()) return ResponseEntity.ok(Map.of(
            "message", "No users found in village: " + name,
            "users", users
        ));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-village-id/{id}")
    public ResponseEntity<?> getUsersByVillageId(@PathVariable Long id) {
        List<User> users = userRepo.findByVillageId(id);
        return ResponseEntity.ok(Map.of(
            "villageId", id,
            "count", users.size(),
            "users", users
        ));
    }

    @GetMapping("/by-cell/{name}")
    public ResponseEntity<?> getUsersByCell(@PathVariable String name) {
        List<User> users = userRepo.findByCellName(name);
        return ResponseEntity.ok(Map.of(
            "cell", name,
            "count", users.size(),
            "users", users
        ));
    }

    @GetMapping("/by-sector/{name}")
    public ResponseEntity<?> getUsersBySector(@PathVariable String name) {
        List<User> users = userRepo.findBySectorName(name);
        return ResponseEntity.ok(Map.of(
            "sector", name,
            "count", users.size(),
            "users", users
        ));
    }

    @GetMapping("/by-district/{name}")
    public ResponseEntity<?> getUsersByDistrict(@PathVariable String name) {
        List<User> users = userRepo.findByDistrictName(name);
        return ResponseEntity.ok(Map.of(
            "district", name,
            "count", users.size(),
            "users", users
        ));
    }

    @GetMapping("/by-province/{search}")
    public ResponseEntity<?> getUsersByProvince(@PathVariable String search) {
        List<User> users = userRepo.findByProvinceCodeOrName(search);
        return ResponseEntity.ok(Map.of(
            "province", search,
            "count", users.size(),
            "users", users
        ));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String role     = (String) body.get("role");

        if (username == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Required fields: username, password, role",
                "optionalFields", "villageId (links user to full location chain)"
            ));
        }
        if (userRepo.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        user.setHasVoted(false);

        if (body.containsKey("villageId")) {
            Long villageId = Long.valueOf(body.get("villageId").toString());
            Village village = villageRepo.findById(villageId).orElse(null);
            if (village == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Village not found with id: " + villageId));
            }
            user.setVillage(village);
        }

        User saved = userRepo.save(user);
        return ResponseEntity.ok(Map.of(
            "message", "User created successfully",
            "user", saved,
            "locationChain", saved.getVillage() != null ? Map.of(
                "village",  saved.getVillage().getName(),
                "cell",     saved.getVillage().getCell().getName(),
                "sector",   saved.getVillage().getCell().getSector().getName(),
                "district", saved.getVillage().getCell().getSector().getDistrict().getName(),
                "province", saved.getVillage().getCell().getSector().getDistrict().getProvince().getName()
            ) : "No village assigned"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        if (body.containsKey("username")) user.setUsername((String) body.get("username"));
        if (body.containsKey("password")) user.setPassword(passwordEncoder.encode((String) body.get("password")));
        if (body.containsKey("role"))     user.setRole(UserRole.valueOf(((String) body.get("role")).toUpperCase()));
        if (body.containsKey("hasVoted")) user.setHasVoted((Boolean) body.get("hasVoted"));
        if (body.containsKey("villageId")) {
            Long villageId = Long.valueOf(body.get("villageId").toString());
            villageRepo.findById(villageId).ifPresent(user::setVillage);
        }

        return ResponseEntity.ok(userRepo.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id)) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully", "id", id));
    }
}

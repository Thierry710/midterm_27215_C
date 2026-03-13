package auca.ac.rw.onlineVotingSystem.config;

import auca.ac.rw.onlineVotingSystem.enums.UserRole;
import auca.ac.rw.onlineVotingSystem.model.Cell;
import auca.ac.rw.onlineVotingSystem.model.District;
import auca.ac.rw.onlineVotingSystem.model.Province;
import auca.ac.rw.onlineVotingSystem.model.Sector;
import auca.ac.rw.onlineVotingSystem.model.Village;
import auca.ac.rw.onlineVotingSystem.repository.UserRepository;
import auca.ac.rw.onlineVotingSystem.service.LocationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepo;

    public SecurityConfig(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            auca.ac.rw.onlineVotingSystem.model.User appUser = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return org.springframework.security.core.userdetails.User
                    .withUsername(appUser.getUsername())
                    .password(appUser.getPassword())
                    .roles(appUser.getRole().name())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST,   "/api/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PUT,    "/api/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                .requestMatchers("/voter/**").hasRole("VOTER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/default", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic(basic -> {});
        return http.build();
    }

    @Bean
    public CommandLineRunner seedData(LocationService locationService,
                                      UserRepository userRepo,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.existsByUsername("admin")) return;

            Province kigali     = locationService.saveProvince("KIG", "Kigali City");

            District gasabo     = locationService.saveDistrict("Gasabo", kigali);
            Sector remera       = locationService.saveSector("Remera", gasabo);
            Cell rukirI         = locationService.saveCell("Rukiri I", remera);
            locationService.saveVillage("Village A", rukirI);
            locationService.saveVillage("Village B", rukirI);
            locationService.saveVillage("Village C", rukirI);
            Cell rukirII        = locationService.saveCell("Rukiri II", remera);
            locationService.saveVillage("Village A", rukirII);
            locationService.saveVillage("Village B", rukirII);
            Cell nyabisindu     = locationService.saveCell("Nyabisindu", remera);
            locationService.saveVillage("Village A", nyabisindu);
            locationService.saveVillage("Village B", nyabisindu);

            Sector kacyiru      = locationService.saveSector("Kacyiru", gasabo);
            Cell kamatamu       = locationService.saveCell("Kamatamu", kacyiru);
            locationService.saveVillage("Village A", kamatamu);
            locationService.saveVillage("Village B", kamatamu);
            Cell kagugu         = locationService.saveCell("Kagugu", kacyiru);
            locationService.saveVillage("Village A", kagugu);
            locationService.saveVillage("Village B", kagugu);

            District kicukiro   = locationService.saveDistrict("Kicukiro", kigali);
            Sector kagarama     = locationService.saveSector("Kagarama", kicukiro);
            Cell kanserege      = locationService.saveCell("Kanserege", kagarama);
            locationService.saveVillage("Village A", kanserege);
            locationService.saveVillage("Village B", kanserege);
            Cell muyange        = locationService.saveCell("Muyange", kagarama);
            locationService.saveVillage("Village A", muyange);
            locationService.saveVillage("Village B", muyange);

            Sector niboye       = locationService.saveSector("Niboye", kicukiro);
            Cell niboyeCell     = locationService.saveCell("Niboye", niboye);
            locationService.saveVillage("Village A", niboyeCell);
            locationService.saveVillage("Village B", niboyeCell);
            Cell nyakabanda     = locationService.saveCell("Nyakabanda", niboye);
            locationService.saveVillage("Village A", nyakabanda);
            locationService.saveVillage("Village B", nyakabanda);

            District nyarugenge = locationService.saveDistrict("Nyarugenge", kigali);
            Sector muhima       = locationService.saveSector("Muhima", nyarugenge);
            Cell nyabugogo      = locationService.saveCell("Nyabugogo", muhima);
            locationService.saveVillage("Village A", nyabugogo);
            locationService.saveVillage("Village B", nyabugogo);
            Cell kabasengerezi  = locationService.saveCell("Kabasengerezi", muhima);
            locationService.saveVillage("Village A", kabasengerezi);
            locationService.saveVillage("Village B", kabasengerezi);

            Sector nyamirambo   = locationService.saveSector("Nyamirambo", nyarugenge);
            Cell cyivugiza      = locationService.saveCell("Cyivugiza", nyamirambo);
            locationService.saveVillage("Village A", cyivugiza);
            locationService.saveVillage("Village B", cyivugiza);
            Cell rugarama       = locationService.saveCell("Rugarama", nyamirambo);
            locationService.saveVillage("Village A", rugarama);
            locationService.saveVillage("Village B", rugarama);

            Province northern   = locationService.saveProvince("N", "Northern Province");
            District musanze    = locationService.saveDistrict("Musanze", northern);
            Sector muhoza       = locationService.saveSector("Muhoza", musanze);
            Cell mpenge         = locationService.saveCell("Mpenge", muhoza);
            locationService.saveVillage("Village A", mpenge);
            locationService.saveVillage("Village B", mpenge);

            Province southern   = locationService.saveProvince("S", "Southern Province");
            District huye       = locationService.saveDistrict("Huye", southern);
            Sector tumba        = locationService.saveSector("Tumba", huye);
            Cell ngoma          = locationService.saveCell("Ngoma", tumba);
            locationService.saveVillage("Village A", ngoma);
            locationService.saveVillage("Village B", ngoma);

            Province eastern    = locationService.saveProvince("E", "Eastern Province");
            District rwamagana  = locationService.saveDistrict("Rwamagana", eastern);
            Sector fumbwe       = locationService.saveSector("Fumbwe", rwamagana);
            Cell musha          = locationService.saveCell("Musha", fumbwe);
            locationService.saveVillage("Village A", musha);
            locationService.saveVillage("Village B", musha);

            Province western    = locationService.saveProvince("W", "Western Province");
            District rubavu     = locationService.saveDistrict("Rubavu", western);
            Sector gisenyi      = locationService.saveSector("Gisenyi", rubavu);
            Cell buhene         = locationService.saveCell("Buhene", gisenyi);
            locationService.saveVillage("Village A", buhene);
            locationService.saveVillage("Village B", buhene);

            Village adminVillage = locationService.getAllVillages().stream()
                    .filter(v -> v.getName().equals("Village A")
                              && v.getCell().getName().equals("Rukiri I"))
                    .findFirst().orElse(null);

            auca.ac.rw.onlineVotingSystem.model.User admin =
                    new auca.ac.rw.onlineVotingSystem.model.User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setHasVoted(false);
            admin.setVillage(adminVillage);
            userRepo.save(admin);

            System.out.println("✅ Rwanda seeded: 5 Provinces → Districts → Sectors → Cells → Villages");
            System.out.println("✅ Admin created: admin / admin123");
        };
    }
}

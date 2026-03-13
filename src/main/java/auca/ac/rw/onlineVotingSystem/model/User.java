package auca.ac.rw.onlineVotingSystem.model;

import auca.ac.rw.onlineVotingSystem.enums.UserRole;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(columnDefinition = "boolean default false")
    private Boolean hasVoted = false;

    @ManyToOne
    @JoinColumn(name = "village_id")
    private Village village;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private VoterProfile voterProfile;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public Boolean isHasVoted() { return hasVoted != null && hasVoted; }
    public void setHasVoted(Boolean hasVoted) { this.hasVoted = hasVoted; }
    public Village getVillage() { return village; }
    public void setVillage(Village village) { this.village = village; }
    public VoterProfile getVoterProfile() { return voterProfile; }
    public void setVoterProfile(VoterProfile voterProfile) { this.voterProfile = voterProfile; }

    public String getProvinceName() {
        if (village == null) return "—";
        return village.getCell().getSector().getDistrict().getProvince().getName();
    }

    public String getDistrictName() {
        if (village == null) return "—";
        return village.getCell().getSector().getDistrict().getName();
    }
}

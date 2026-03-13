package auca.ac.rw.onlineVotingSystem.model;

import jakarta.persistence.*;
@Entity
public class VoterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nationalId;
    private String phoneNumber;
    private String dateOfBirth;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Long getId() { return id; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

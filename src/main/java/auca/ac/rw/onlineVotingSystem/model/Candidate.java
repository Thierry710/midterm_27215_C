package auca.ac.rw.onlineVotingSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int votes = 0;
    private boolean approved = false;

    @ManyToOne
    @JsonIgnoreProperties({"tags", "candidates"})
    private Election election;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }
}

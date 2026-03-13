package auca.ac.rw.onlineVotingSystem.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
@Entity
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "cell_id")
    private Cell cell;

    @OneToMany(mappedBy = "village")
    @JsonIgnore
    private List<User> users;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Cell getCell() { return cell; }
    public void setCell(Cell cell) { this.cell = cell; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public String getFullLocation() {
        return name + " → " +
               cell.getName() + " → " +
               cell.getSector().getName() + " → " +
               cell.getSector().getDistrict().getName() + " → " +
               cell.getSector().getDistrict().getProvince().getName();
    }
}

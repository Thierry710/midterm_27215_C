package auca.ac.rw.onlineVotingSystem.repository;
import auca.ac.rw.onlineVotingSystem.enums.UserRole;
import auca.ac.rw.onlineVotingSystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole(UserRole role);
    long countByHasVotedTrue();
    boolean existsByUsername(String username);
    Page<User> findByRole(UserRole role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.village.cell.sector.district.province.code = :search OR u.village.cell.sector.district.province.name = :search")
    List<User> findByProvinceCodeOrName(@Param("search") String search);

    @Query("SELECT u FROM User u WHERE u.village.cell.sector.district.name = :name")
    List<User> findByDistrictName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.village.cell.sector.name = :name")
    List<User> findBySectorName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.village.cell.name = :name")
    List<User> findByCellName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.village.name = :name")
    List<User> findByVillageName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.village.id = :id")
    List<User> findByVillageId(@Param("id") Long id);
}

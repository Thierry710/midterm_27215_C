package auca.ac.rw.onlineVotingSystem.api;

import auca.ac.rw.onlineVotingSystem.model.*;
import auca.ac.rw.onlineVotingSystem.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationApiController {

    private final ProvinceRepository provinceRepo;
    private final DistrictRepository districtRepo;
    private final SectorRepository sectorRepo;
    private final CellRepository cellRepo;
    private final VillageRepository villageRepo;

    public LocationApiController(ProvinceRepository provinceRepo, DistrictRepository districtRepo,
                                 SectorRepository sectorRepo, CellRepository cellRepo,
                                 VillageRepository villageRepo) {
        this.provinceRepo = provinceRepo;
        this.districtRepo = districtRepo;
        this.sectorRepo   = sectorRepo;
        this.cellRepo     = cellRepo;
        this.villageRepo  = villageRepo;
    }

    @GetMapping("/provinces")
    public List<Province> getAllProvinces() { return provinceRepo.findAll(); }

    @GetMapping("/provinces/{id}")
    public ResponseEntity<Province> getProvince(@PathVariable Long id) {
        return provinceRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/provinces")
    public ResponseEntity<?> createProvince(@RequestBody Map<String, String> body) {
        if (!body.containsKey("code") || !body.containsKey("name"))
            return ResponseEntity.badRequest().body("Required: code, name");
        if (provinceRepo.existsByCode(body.get("code")))
            return ResponseEntity.badRequest().body("Province code already exists");
        Province p = new Province();
        p.setCode(body.get("code"));
        p.setName(body.get("name"));
        return ResponseEntity.ok(provinceRepo.save(p));
    }

    @PutMapping("/provinces/{id}")
    public ResponseEntity<?> updateProvince(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Province p = provinceRepo.findById(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        if (body.containsKey("code")) p.setCode(body.get("code"));
        if (body.containsKey("name")) p.setName(body.get("name"));
        return ResponseEntity.ok(provinceRepo.save(p));
    }

    @DeleteMapping("/provinces/{id}")
    public ResponseEntity<?> deleteProvince(@PathVariable Long id) {
        if (!provinceRepo.existsById(id)) return ResponseEntity.notFound().build();
        provinceRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Province deleted", "id", id));
    }

    @GetMapping("/districts")
    public List<District> getAllDistricts() { return districtRepo.findAll(); }

    @GetMapping("/districts/{id}")
    public ResponseEntity<District> getDistrict(@PathVariable Long id) {
        return districtRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/districts")
    public ResponseEntity<?> createDistrict(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("name") || !body.containsKey("provinceId"))
            return ResponseEntity.badRequest().body("Required: name, provinceId");
        Province province = provinceRepo.findById(Long.valueOf(body.get("provinceId").toString())).orElse(null);
        if (province == null) return ResponseEntity.badRequest().body("Province not found");
        District d = new District();
        d.setName((String) body.get("name"));
        d.setProvince(province);
        return ResponseEntity.ok(districtRepo.save(d));
    }

    @PutMapping("/districts/{id}")
    public ResponseEntity<?> updateDistrict(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        District d = districtRepo.findById(id).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        if (body.containsKey("name")) d.setName((String) body.get("name"));
        if (body.containsKey("provinceId"))
            provinceRepo.findById(Long.valueOf(body.get("provinceId").toString())).ifPresent(d::setProvince);
        return ResponseEntity.ok(districtRepo.save(d));
    }

    @DeleteMapping("/districts/{id}")
    public ResponseEntity<?> deleteDistrict(@PathVariable Long id) {
        if (!districtRepo.existsById(id)) return ResponseEntity.notFound().build();
        districtRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "District deleted", "id", id));
    }

    @GetMapping("/sectors")
    public List<Sector> getAllSectors() { return sectorRepo.findAll(); }

    @GetMapping("/sectors/{id}")
    public ResponseEntity<Sector> getSector(@PathVariable Long id) {
        return sectorRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sectors")
    public ResponseEntity<?> createSector(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("name") || !body.containsKey("districtId"))
            return ResponseEntity.badRequest().body("Required: name, districtId");
        District district = districtRepo.findById(Long.valueOf(body.get("districtId").toString())).orElse(null);
        if (district == null) return ResponseEntity.badRequest().body("District not found");
        Sector s = new Sector();
        s.setName((String) body.get("name"));
        s.setDistrict(district);
        return ResponseEntity.ok(sectorRepo.save(s));
    }

    @PutMapping("/sectors/{id}")
    public ResponseEntity<?> updateSector(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Sector s = sectorRepo.findById(id).orElse(null);
        if (s == null) return ResponseEntity.notFound().build();
        if (body.containsKey("name")) s.setName((String) body.get("name"));
        if (body.containsKey("districtId"))
            districtRepo.findById(Long.valueOf(body.get("districtId").toString())).ifPresent(s::setDistrict);
        return ResponseEntity.ok(sectorRepo.save(s));
    }

    @DeleteMapping("/sectors/{id}")
    public ResponseEntity<?> deleteSector(@PathVariable Long id) {
        if (!sectorRepo.existsById(id)) return ResponseEntity.notFound().build();
        sectorRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Sector deleted", "id", id));
    }

    @GetMapping("/cells")
    public List<Cell> getAllCells() { return cellRepo.findAll(); }

    @GetMapping("/cells/{id}")
    public ResponseEntity<Cell> getCell(@PathVariable Long id) {
        return cellRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cells")
    public ResponseEntity<?> createCell(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("name") || !body.containsKey("sectorId"))
            return ResponseEntity.badRequest().body("Required: name, sectorId");
        Sector sector = sectorRepo.findById(Long.valueOf(body.get("sectorId").toString())).orElse(null);
        if (sector == null) return ResponseEntity.badRequest().body("Sector not found");
        Cell c = new Cell();
        c.setName((String) body.get("name"));
        c.setSector(sector);
        return ResponseEntity.ok(cellRepo.save(c));
    }

    @PutMapping("/cells/{id}")
    public ResponseEntity<?> updateCell(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Cell c = cellRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();
        if (body.containsKey("name")) c.setName((String) body.get("name"));
        if (body.containsKey("sectorId"))
            sectorRepo.findById(Long.valueOf(body.get("sectorId").toString())).ifPresent(c::setSector);
        return ResponseEntity.ok(cellRepo.save(c));
    }

    @DeleteMapping("/cells/{id}")
    public ResponseEntity<?> deleteCell(@PathVariable Long id) {
        if (!cellRepo.existsById(id)) return ResponseEntity.notFound().build();
        cellRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Cell deleted", "id", id));
    }

    @GetMapping("/villages/all-with-chain")
    public List<Map<String, Object>> getAllVillagesWithChain() {
        return villageRepo.findAll().stream()
                .map(v -> Map.<String, Object>of(
                    "villageId",    v.getId(),
                    "village",      v.getName(),
                    "cell",         v.getCell().getName(),
                    "sector",       v.getCell().getSector().getName(),
                    "district",     v.getCell().getSector().getDistrict().getName(),
                    "province",     v.getCell().getSector().getDistrict().getProvince().getName(),
                    "provinceCode", v.getCell().getSector().getDistrict().getProvince().getCode()
                ))
                .toList();
    }

    @GetMapping("/villages")
    public List<Village> getAllVillages() { return villageRepo.findAll(); }

    @GetMapping("/villages/{id}")
    public ResponseEntity<Village> getVillage(@PathVariable Long id) {
        return villageRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/villages/{id}/chain")
    public ResponseEntity<?> getVillageChain(@PathVariable Long id) {
        Village v = villageRepo.findById(id).orElse(null);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of(
            "villageId",    v.getId(),
            "village",      v.getName(),
            "cell",         v.getCell().getName(),
            "sector",       v.getCell().getSector().getName(),
            "district",     v.getCell().getSector().getDistrict().getName(),
            "province",     v.getCell().getSector().getDistrict().getProvince().getName(),
            "provinceCode", v.getCell().getSector().getDistrict().getProvince().getCode()
        ));
    }

    @PostMapping("/villages")
    public ResponseEntity<?> createVillage(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("name") || !body.containsKey("cellId"))
            return ResponseEntity.badRequest().body("Required: name, cellId");
        Cell cell = cellRepo.findById(Long.valueOf(body.get("cellId").toString())).orElse(null);
        if (cell == null) return ResponseEntity.badRequest().body("Cell not found");
        Village v = new Village();
        v.setName((String) body.get("name"));
        v.setCell(cell);
        return ResponseEntity.ok(villageRepo.save(v));
    }

    @PutMapping("/villages/{id}")
    public ResponseEntity<?> updateVillage(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Village v = villageRepo.findById(id).orElse(null);
        if (v == null) return ResponseEntity.notFound().build();
        if (body.containsKey("name")) v.setName((String) body.get("name"));
        if (body.containsKey("cellId"))
            cellRepo.findById(Long.valueOf(body.get("cellId").toString())).ifPresent(v::setCell);
        return ResponseEntity.ok(villageRepo.save(v));
    }

    @DeleteMapping("/villages/{id}")
    public ResponseEntity<?> deleteVillage(@PathVariable Long id) {
        if (!villageRepo.existsById(id)) return ResponseEntity.notFound().build();
        villageRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Village deleted", "id", id));
    }
}

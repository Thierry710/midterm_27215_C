package auca.ac.rw.onlineVotingSystem.service;

import auca.ac.rw.onlineVotingSystem.model.*;
import auca.ac.rw.onlineVotingSystem.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final ProvinceRepository provinceRepo;
    private final DistrictRepository districtRepo;
    private final SectorRepository sectorRepo;
    private final CellRepository cellRepo;
    private final VillageRepository villageRepo;
    private final UserRepository userRepo;

    public LocationService(ProvinceRepository provinceRepo, DistrictRepository districtRepo,
                           SectorRepository sectorRepo, CellRepository cellRepo,
                           VillageRepository villageRepo, UserRepository userRepo) {
        this.provinceRepo = provinceRepo;
        this.districtRepo = districtRepo;
        this.sectorRepo = sectorRepo;
        this.cellRepo = cellRepo;
        this.villageRepo = villageRepo;
        this.userRepo = userRepo;
    }

    public Province saveProvince(String code, String name) {
        if (provinceRepo.existsByCode(code)) return provinceRepo.findByCode(code).orElseThrow();
        Province p = new Province();
        p.setCode(code);
        p.setName(name);
        return provinceRepo.save(p);
    }

    public District saveDistrict(String name, Province province) {
        District d = new District();
        d.setName(name);
        d.setProvince(province);
        return districtRepo.save(d);
    }

    public Sector saveSector(String name, District district) {
        Sector s = new Sector();
        s.setName(name);
        s.setDistrict(district);
        return sectorRepo.save(s);
    }

    public Cell saveCell(String name, Sector sector) {
        Cell c = new Cell();
        c.setName(name);
        c.setSector(sector);
        return cellRepo.save(c);
    }

    public Village saveVillage(String name, Cell cell) {
        Village v = new Village();
        v.setName(name);
        v.setCell(cell);
        return villageRepo.save(v);
    }

    public List<Province> getAllProvinces()   { return provinceRepo.findAll(); }
    public List<District> getAllDistricts()   { return districtRepo.findAll(); }
    public List<Sector>   getAllSectors()     { return sectorRepo.findAll(); }
    public List<Cell>     getAllCells()       { return cellRepo.findAll(); }
    public List<Village>  getAllVillages()    { return villageRepo.findAll(); }

    public Village findVillageById(Long id)  { return villageRepo.findById(id).orElseThrow(); }

    public List<User> getUsersByProvince(String search) {
        return userRepo.findByProvinceCodeOrName(search);
    }

    public Page<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return userRepo.findAll(pageable);
    }
}

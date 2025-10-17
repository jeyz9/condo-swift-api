package com.cs.jeyz9.condoswiftapi.seeders;

import com.cs.jeyz9.condoswiftapi.constants.AnnounceTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.BadgeConstant;
import com.cs.jeyz9.condoswiftapi.constants.SaleTypeConstant;
import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.NearbyPlace;
import com.cs.jeyz9.condoswiftapi.models.NearbyPlaceTypes;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.SaleType;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.NearbyPlaceRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AnnounceStateApproveRepository approveRepository;
    private final BadgeRepository badgeRepository;
    private final AnnounceTypeRepository announceTypeRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final NearbyPlaceRepository nearbyPlaceRepository;

    @Autowired
    public DatabaseSeeder(RoleRepository roleRepository, AnnounceStateApproveRepository approveRepository, BadgeRepository badgeRepository, AnnounceTypeRepository announceTypeRepository, SaleTypeRepository saleTypeRepository, NearbyPlaceRepository nearbyPlaceRepository) {
        this.roleRepository = roleRepository;
        this.approveRepository = approveRepository;
        this.badgeRepository = badgeRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.saleTypeRepository = saleTypeRepository;
        this.nearbyPlaceRepository = nearbyPlaceRepository;
    }
    
    @Override
    public void run(String... args) {
        if(roleRepository.count() == 0L) {
            List<Role> roles = new ArrayList<>();
            roles.add(new Role(RoleName.ADMIN));
            roles.add(new Role(RoleName.MODERATOR));
            roles.add(new Role(RoleName.AGENT));
            roles.add(new Role(RoleName.USER));
            roleRepository.saveAll(roles);
        }
        
        if(approveRepository.count() == 0L) {
            List<AnnounceStateApprove> approves = new ArrayList<>();
            approves.add(new AnnounceStateApprove(ApproveStatus.APPROVED));
            approves.add(new AnnounceStateApprove(ApproveStatus.REJECTED));
            approves.add(new AnnounceStateApprove(ApproveStatus.PENDING));
            approves.add(new AnnounceStateApprove(ApproveStatus.DRAFT));
            approveRepository.saveAll(approves);
        }
        
        if(badgeRepository.count() == 0L) {
            List<Badge> badges = new ArrayList<>();
            badges.add(new Badge(BadgeConstant.RECOMMEND));
            badges.add(new Badge(BadgeConstant.CONDO));
            badges.add(new Badge(BadgeConstant.VILLA));
            badges.add(new Badge(BadgeConstant.RENT));
            badgeRepository.saveAll(badges);
        }
        
        if(announceTypeRepository.count() == 0L){
            List<AnnounceType> announceTypes = new ArrayList<>();
            announceTypes.add(new AnnounceType(AnnounceTypeConstant.CONDO));
            announceTypes.add(new AnnounceType(AnnounceTypeConstant.LAND));
            announceTypes.add(new AnnounceType(AnnounceTypeConstant.LUXURY_HOUSE));
            announceTypes.add(new AnnounceType(AnnounceTypeConstant.VILLA));
            announceTypeRepository.saveAll(announceTypes);
        }

        if(saleTypeRepository.count() == 0L){
            List<SaleType> saleTypes = new ArrayList<>();
            saleTypes.add(new SaleType(SaleTypeConstant.RENT));
            saleTypes.add(new SaleType(SaleTypeConstant.SALE));
            saleTypeRepository.saveAll(saleTypes);
        }
        
        if(nearbyPlaceRepository.count() == 0L) {
            List<NearbyPlace> nearbyPlaces = new ArrayList<>();
            nearbyPlaces.add(new NearbyPlace("ใกล้ BTS", NearbyPlaceTypes.BTS_STATION));
            nearbyPlaces.add(new NearbyPlace("วิลล่าชลบุรี", NearbyPlaceTypes.PROVINCE));
            nearbyPlaces.add(new NearbyPlace("วิลล่าราชบุรี", NearbyPlaceTypes.PROVINCE));
            nearbyPlaces.add(new NearbyPlace("วิลล่าเพชรบุรี", NearbyPlaceTypes.PROVINCE));
            nearbyPlaces.add(new NearbyPlace("วิลล่านครปฐม", NearbyPlaceTypes.PROVINCE));
            nearbyPlaceRepository.saveAll(nearbyPlaces);
        }
    }
}

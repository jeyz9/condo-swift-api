package com.cs.jeyz9.condoswiftapi.seeders;

import com.cs.jeyz9.condoswiftapi.constants.AnnounceTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.BadgeConstant;
import com.cs.jeyz9.condoswiftapi.constants.SaleTypeConstant;
import com.cs.jeyz9.condoswiftapi.constants.StationTypeConstant;
import com.cs.jeyz9.condoswiftapi.models.AnnounceStateApprove;
import com.cs.jeyz9.condoswiftapi.models.AnnounceType;
import com.cs.jeyz9.condoswiftapi.models.ApproveStatus;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.models.SaleType;
import com.cs.jeyz9.condoswiftapi.models.Station;
import com.cs.jeyz9.condoswiftapi.models.Villa;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceStateApproveRepository;
import com.cs.jeyz9.condoswiftapi.repository.AnnounceTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import com.cs.jeyz9.condoswiftapi.repository.SaleTypeRepository;
import com.cs.jeyz9.condoswiftapi.repository.StationRepository;
import com.cs.jeyz9.condoswiftapi.repository.VillaRepository;
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
    private final StationRepository stationRepository;
    private final VillaRepository villaRepository;

    @Autowired
    public DatabaseSeeder(RoleRepository roleRepository, AnnounceStateApproveRepository approveRepository, BadgeRepository badgeRepository, AnnounceTypeRepository announceTypeRepository, SaleTypeRepository saleTypeRepository, StationRepository stationRepository, VillaRepository villaRepository) {
        this.roleRepository = roleRepository;
        this.approveRepository = approveRepository;
        this.badgeRepository = badgeRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.saleTypeRepository = saleTypeRepository;
        this.stationRepository = stationRepository;
        this.villaRepository = villaRepository;
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
            badges.add(new Badge(BadgeConstant.NEW));
            badges.add(new Badge(BadgeConstant.PREMIUM));
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
        
//        if(nearbyPlaceRepository.count() == 0L) {
//            List<NearbyPlace> nearbyPlaces = new ArrayList<>();
//            nearbyPlaces.add(new NearbyPlace("ใกล้ BTS", NearbyPlaceTypes.BTS_STATION));
//            nearbyPlaces.add(new NearbyPlace("วิลล่าชลบุรี", NearbyPlaceTypes.PROVINCE));
//            nearbyPlaces.add(new NearbyPlace("วิลล่าราชบุรี", NearbyPlaceTypes.PROVINCE));
//            nearbyPlaces.add(new NearbyPlace("วิลล่าเพชรบุรี", NearbyPlaceTypes.PROVINCE));
//            nearbyPlaces.add(new NearbyPlace("วิลล่านครปฐม", NearbyPlaceTypes.PROVINCE));
//            nearbyPlaceRepository.saveAll(nearbyPlaces);
//        }
        
        if(stationRepository.count() == 0L) {
            List<Station> stations = new ArrayList<>();
            stations.add(new Station(null, "สถานีท่าพระ", 13.7296593, 100.4740885, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีจรัญฯ 13", 13.74005671, 100.4707321, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีไฟฉาย", 13.7557305, 100.4693411, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางขุนนนท์", 13.76334485, 100.4733652, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางยี่ขัน", 13.77747388, 100.4853472, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสิรินธร", 13.783808, 100.4932301, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางพลัด", 13.79238778, 100.5048644, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางอ้อ", 13.79899246, 100.5097903, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางโพ", 13.80640723, 100.5211039, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางซื่อ", 13.80235091, 100.5410375, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีกำแพงเพชร", 13.79793257, 100.547936, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสวนจตุจักร", 13.80295124, 100.5533613, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีพหลโยธิน", 13.81296882, 100.5615673, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลาดพร้าว", 13.8065239, 100.572901, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีรัชดาภิเษก", 13.79895711, 100.5744781, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสุทธิสาร", 13.78945997, 100.5740167, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีห้วยขวาง", 13.77869774, 100.5734915, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศูนย์วัฒนธรรมแห่งประเทศไทย", 13.76630887, 100.5700702, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีพระราม 9", 13.75784279, 100.5652421, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีเพชรบุรี", 13.74919525, 100.5633537, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสุขุมวิท", 13.73738427, 100.5614168, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศูนย์การประชุมแห่งชาติสิริกิติ์", 13.72262377, 100.5599045, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีคลองเตย", 13.72229036, 100.5540033, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลุมพินี", 13.7257317, 100.5457784, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสีลม", 13.72928274, 100.5373135, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสามย่าน", 13.73222813, 100.5302684, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีหัวลำโพง", 13.73755186, 100.5171633, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีวัดมังกร", 13.74228753, 100.5099535, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสามยอด", 13.74705362, 100.5021701, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสนามไชย", 13.74434176, 100.4947367, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีอิสรภาพ", 13.73831268, 100.4857197, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางไผ่", 13.72473175, 100.4651442, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางหว้า", 13.72070709, 100.4578516, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีเพชรเกษม48", 13.71541044, 100.4454476, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีภาษีเจริญ", 13.7128901, 100.4342906, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางแค", 13.71189286, 100.4222739, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีหลักสอง", 13.7108635, 100.4094434, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีคลองบางไผ่", 13.89238779, 100.4082208, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีตลาดบางใหญ่", 13.88103687, 100.4092903, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสามแยกบางใหญ่", 13.87469609, 100.4193558, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางพลู", 13.87575986, 100.4337962, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางรักใหญ่", 13.87660724, 100.4449415, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางรักน้อยท่าอิฐ", 13.87480634, 100.45595, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีไทรม้า", 13.87047546, 100.4667018, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสะพานพระนั่งเกล้า", 13.87030425, 100.4801163, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีแยกนนทบุรี 1", 13.86593454, 100.4941053, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางกระสอ", 13.86165786, 100.5046064, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศูนย์ราชการนนทบุรี", 13.86018661, 100.5130529, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีกระทรวงสาธารณสุข", 13.8485518, 100.5147168, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีแยกติวานนท์", 13.83953803, 100.514973, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีวงศ์สว่าง", 13.82986686, 100.5264918, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางซ่อน", 13.82010348, 100.5324499, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีเตาปูน", 13.80614838, 100.5307586, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลาดพร้าว", 13.80697302, 100.5747539, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีภาวนา", 13.80013312, 100.5841789, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีโชคชัย 4", 13.79423668, 100.5944237, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลาดพร้าว 71", 13.78703755, 100.6075445, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลาดพร้าว 83", 13.78367139, 100.6136253, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีมหาดไทย", 13.77843808, 100.6232072, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีลาดพร้าว 101", 13.77469684, 100.6299138, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีบางกะปิ", 13.76903632, 100.6397694, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีแยกลำสาลี", 13.76181247, 100.6454616, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีกรีฑา", 13.75040076, 100.6448085, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีหัวหมาก", 13.73614434, 100.6410568, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีกลันตัน", 13.72527482, 100.6417763, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีนุช", 13.71111235, 100.6442758, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีนครินทร์ 38", 13.70113658, 100.6465176, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสวนหลวง ร.9", 13.69086507, 100.6470192, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีอุดม", 13.67664325, 100.6462085, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีเอี่ยม", 13.66761207, 100.6450458, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีลาซาล", 13.65496793, 100.6421205, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีแบริ่ง", 13.64355462, 100.6362095, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีด่าน", 13.63320965, 100.6300014, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีศรีเทพา", 13.62994906, 100.6227147, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีทิพวัล", 13.63677534, 100.6099328, StationTypeConstant.MRT));
            stations.add(new Station(null, "สถานีสำโรง", 13.64500554, 100.5966143, StationTypeConstant.MRT));
            
            stations.add(new Station(null, "สถานีหมอชิต", 13.80262297, 100.5538124, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสะพานควาย", 13.79386254, 100.5497496, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีอารีย์", 13.77954984, 100.5445779, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสนามเป้า", 13.77255111, 100.542072, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีอนุสาวรีย์ชัยสมรภูมิ", 13.76270271, 100.5370322, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพญาไท", 13.75695565, 100.5338228, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีราชเทวี", 13.75181733, 100.5315607, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสนามกีฬาแห่งชาติ", 13.74650803, 100.5291682, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสยาม", 13.74565407, 100.5342208, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีราชดำริ", 13.73945669, 100.5394511, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีศาลาแดง", 13.72851545, 100.5342387, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีช่องนนทรีย์", 13.72385423, 100.5293727, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสุรศักดิ์", 13.71930141, 100.5215673, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสะพานตากสิน", 13.7188135, 100.5141766, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีชิดลม", 13.74409858, 100.5430322, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเพลินจิต", 13.74302972, 100.5491559, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีนานา", 13.74063159, 100.5553355, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีอโศก", 13.73701993, 100.560425, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพร้อมพงษ์", 13.73051676, 100.5696098, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีทองหล่อ", 13.72429562, 100.5784631, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเอกมัย", 13.71955261, 100.5851253, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพระโขนง", 13.71526531, 100.5911801, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีอ่อนนุช", 13.70562929, 100.6010353, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีกรุงธนบุรี", 13.72093655, 100.5025308, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีวงเวียนใหญ่", 13.72107132, 100.495218, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีบางจาก", 13.69671589, 100.605357, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีปุณณวิถี", 13.68930755, 100.6089676, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีอุดมสุข", 13.6798494, 100.6094764, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีบางนา", 13.66812092, 100.6046455, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีแบริ่ง", 13.66115213, 100.6017976, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีโพธิ์นิมิตร", 13.71921979, 100.4859694, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีตลาดพลู", 13.71403433, 100.4763857, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีวุฒากาศ", 13.71222176, 100.4726205, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีบางหว้า", 13.72078877, 100.4577966, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีห้าแยกลาดพร้าว", 13.81668984, 100.5620546, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพหลโยธิน 24", 13.82413711, 100.566461, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีรัชโยธิน", 13.82975669, 100.5697176, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีมหาวิทยาลัยเกษตรศาสตร์", 13.84225543, 100.5771187, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเสนานิคม", 13.83639349, 100.5736196, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีกรมทหารราบที่ 11", 13.86764879, 100.5920557, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีบางบัว", 13.85601494, 100.5851953, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีกรมป่าไม้", 13.85025175, 100.5818259, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเจริญนคร", 13.72646504, 100.509057, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีคลองสาน", 13.73038781, 100.5076304, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเซนต์หลุยส์", 13.72081548, 100.5266994, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีคูคต", 13.93238496, 100.6465697, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีแยก คปอ.", 13.92504891, 100.6258727, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพิพิธภัณฑ์กองทัพอากาศ", 13.9178731, 100.6216492, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีโรงพยาบาลภูมิพลอดุลยเดช", 13.91058043, 100.6173142, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสะพานใหม่", 13.8964893, 100.609037, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสายหยุด", 13.88841275, 100.6042717, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีพหลโยธิน 59", 13.88251503, 100.6007986, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีวัดพระศรีมหาธาตุ", 13.87525635, 100.596715, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสำโรง", 13.64656537, 100.5958207, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีปู่เจ้า", 13.63738772, 100.5920527, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีช้างเอราวัณ", 13.62147272, 100.590181, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีโรงเรียนนายเรือ", 13.60848025, 100.5949182, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีปากน้ำ", 13.60216596, 100.597103, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีศรีนครินทร์", 13.59196551, 100.6090107, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีแพรกษา", 13.58421744, 100.6078495, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีสายลวด", 13.57779717, 100.6054316, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีเคหะฯ", 13.56773098, 100.6077108, StationTypeConstant.BTS));
            stations.add(new Station(null, "สถานีกรุงธนบุรี", 13.72106499, 100.5037059, StationTypeConstant.BTS));

            stationRepository.saveAll(stations);
        }
        
        if (villaRepository.count() == 0L){
            List<Villa> villas = List.of(
                    new Villa(null, "วิลล่ากรุงเทพมหานคร", "กรุงเทพ"),
                    new Villa(null, "วิลล่าชลบุรี", "ชลบุรี"),
                    new Villa(null, "วิลล่าพัทยา", "ชลบุรี"),
                    new Villa(null, "วิลล่าระยอง", "ระยอง"),
                    new Villa(null, "วิลล่าภูเก็ต", "ภูเก็ต"),
                    new Villa(null, "วิลล่ากระบี่", "กระบี่"),
                    new Villa(null, "วิลล่าสงขลา", "สงขลา"),
                    new Villa(null, "วิลล่าสมุย", "สุราษฎร์ธานี"),
                    new Villa(null, "วิลล่าสุราษฎร์ธานี", "สุราษฎร์ธานี"),
                    new Villa(null, "วิลล่านครศรีธรรมราช", "นครศรีธรรมราช"),
                    new Villa(null, "วิลล่าขอนแก่น", "ขอนแก่น"),
                    new Villa(null, "วิลล่าอุดรธานี", "อุดรธานี"),
                    new Villa(null, "วิลล่านครราชสีมา", "นครราชสีมา"),
                    new Villa(null, "วิลล่าเชียงใหม่", "เชียงใหม่"),
                    new Villa(null, "วิลล่าประจวบคีรีขันธ์", "ประจวบคีรีขันธ์"),
                    new Villa(null, "วิลล่าหัวหิน", "ประจวบคีรีขันธ์"),
                    new Villa(null, "วิลล่าราชบุรี", "ราชบุรี"),
                    new Villa(null, "วิลล่านนทบุรี", "นนทบุรี"),
                    new Villa(null, "วิลล่าปทุมธานี", "ปทุมธานี"),
                    new Villa(null, "วิลล่านครปฐม", "นครปฐม"),
                    new Villa(null, "วิลล่าสมุทรปราการ", "สมุทรปราการ"),
                    new Villa(null, "วิลล่ากาญจนบุรี", "กาญจนบุรี"),
                    new Villa(null, "วิลล่าเพชรบุรี", "เพชรบุรี")
            );

            villaRepository.saveAll(villas);

        }
    }
}

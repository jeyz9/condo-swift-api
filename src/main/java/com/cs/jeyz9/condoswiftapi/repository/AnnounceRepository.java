package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.dto.AnnounceApproveDTO;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnounceRepository extends JpaRepository<Announce, Long> {
    @Query("SELECT a FROM Announce a WHERE a.user.id = :userId")
    List<Announce> findAllByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Announce a ORDER BY a.announcementDate DESC")
    List<Announce> findAll();

    @Query(value = """
    SELECT COUNT(*)
    FROM announces a
    JOIN map_point m ON m.announce_id = a.id
    JOIN announce_types at ON a.announce_type_id = at.id
    WHERE at.type_name = 'คอนโด' AND (
        6371 * acos(
            cos(radians(:lat)) * cos(radians(m.lat)) *
            cos(radians(m.lng) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(m.lat))
        )
    ) <= :radius
    """, nativeQuery = true)
    long countListingsNear(@Param("lat") double lat,
                           @Param("lng") double lng,
                           @Param("radius") double radius);
    
    @Query(value = """
        SELECT COUNT(*)
            FROM announces a
            INNER JOIN announce_types at ON a.announce_type_id = at.id
        WHERE at.type_name = 'วิลล่า' AND a.location LIKE CONCAT('%', :province, '%')
    """, nativeQuery = true)
    long countVillaInProvince(@Param("province") String province);
    
    @Query(value = """
        SELECT
            a.id,
            a.title,
            a.price,
            a.remark,
            a.announcement_date,
            a.area_size,
            a.announce_type_id,
            a.approve_id,
            a.approve_by,
            a.bathroom_count,
            a.bedroom_count,
            a.location,
            a.sale_type_id,
            a.user_id,
            a.approve_date,
            a.has_convenience_store,
            a.has_pool,
            a.has_elevator,
            a.has_security,
            a.has_fitness,
            a.has_parking,
            a.province_id,
            s.name AS station_name,
            s.lat AS station_lat,
            s.lng AS station_lng
        FROM announces a
                 JOIN map_point m ON m.announce_id = a.id
                 JOIN announce_types at ON a.announce_type_id = at.id
                 LEFT JOIN (
            SELECT name, lat, lng
            FROM stations
            WHERE (:stationName IS NULL OR name ILIKE '%' || :stationName || '%')
            LIMIT 1
        ) s ON TRUE
        WHERE 
            (:stationName IS NULL OR :stationName = '')
                OR (
                       6371 * acos(
                               cos(radians(s.lat)) * cos(radians(m.lat)) *
                               cos(radians(m.lng) - radians(s.lng)) +
                               sin(radians(s.lat)) * sin(radians(m.lat))
                              )
                       ) <= 1.5
    """, nativeQuery = true)
    List<Announce> findAnnounceNearStation(@Param("stationName") String stationName);
    
    @Query(value = """
        SELECT a.id AS id,
               a.title AS title,
               u.name AS agentName,
               at.type_name AS type,
               a.price AS price,
               asa.status_name AS status,
               a.announcement_date AS announcementDate,
               a.approve_date AS approveDate,
               ai.image_url AS image,
               a.remark
        FROM announces a
        JOIN announce_state_approve asa ON a.approve_id = asa.id
        JOIN users u ON u.id = a.user_id
        JOIN announce_types at ON at.id = a.announce_type_id
        LEFT JOIN (
            SELECT DISTINCT ON (announce_id)
                announce_id,
                image_url
            FROM announce_images
            ORDER BY announce_id, id ASC
        ) ai ON ai.announce_id = a.id
        WHERE asa.status_name = 'APPROVED'
        ORDER BY a.approve_date DESC;
    """, nativeQuery = true)
    List<AnnounceApproveDTO> findAnnounceApprove();

    @Query(value = """
        SELECT a.id AS id,
               a.title AS title,
               u.name AS agentName,
               at.type_name AS type,
               a.price AS price,
               asa.status_name AS status,
               a.announcement_date AS announcementDate,
               a.approve_date AS approveDate,
               ai.image_url AS image,
               a.remark
        FROM announces a
        JOIN announce_state_approve asa ON a.approve_id = asa.id
        JOIN users u ON u.id = a.user_id
        JOIN announce_types at ON at.id = a.announce_type_id
        LEFT JOIN (
            SELECT DISTINCT ON (announce_id)
                announce_id,
                image_url
            FROM announce_images
            ORDER BY announce_id, id ASC
        ) ai ON ai.announce_id = a.id
        WHERE asa.status_name = 'PENDING'
        ORDER BY a.approve_date DESC;
    """, nativeQuery = true)
    List<AnnounceApproveDTO> findAnnouncePending();

    @Query(value = """
        SELECT a.id AS id,
               a.title AS title,
               u.name AS agentName,
               at.type_name AS type,
               a.price AS price,
               asa.status_name AS status,
               a.announcement_date AS announcementDate,
               a.approve_date AS approveDate,
               ai.image_url AS image,
               a.remark
        FROM announces a
                 JOIN announce_state_approve asa ON a.approve_id = asa.id
                 JOIN users u ON u.id = a.user_id
                 JOIN announce_types at ON at.id = a.announce_type_id
                 LEFT JOIN (
                    SELECT DISTINCT ON (announce_id)
                        announce_id,
                        image_url
                    FROM announce_images
                    ORDER BY announce_id, id ASC
                ) ai ON ai.announce_id = a.id
        WHERE asa.status_name = 'APPROVED' OR asa.status_name = 'REJECTED'
        ORDER BY a.approve_date DESC;
    """, nativeQuery = true)
    List<AnnounceApproveDTO> findAnnounceHistory();
}

package com.cs.jeyz9.condoswiftapi.repository;

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
}

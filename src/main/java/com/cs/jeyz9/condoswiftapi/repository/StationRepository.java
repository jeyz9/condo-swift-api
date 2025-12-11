package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.dto.StationsDTO;
import com.cs.jeyz9.condoswiftapi.models.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    
//    @Query(value = """
//        SELECT s.id, s.name, s.station_type FROM stations s;
//    """, nativeQuery = true)
//    List<StationsDTO> findAllStationSelector();
}
package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.dto.AgentDTO;
import com.cs.jeyz9.condoswiftapi.models.AnnounceAgent;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnounceAgentRepository extends JpaRepository<AnnounceAgent, Long> {
    
    @Query(value = """
        SELECT COUNT(*) FROM announce_agents ag WHERE ag.announce_id = :announceId AND ag.agent_id = :agentId; 
    """, nativeQuery = true)
    int checkAgentRequestAnnounce(@Param("announceId") Long announceId, @Param("agentId") Long agentId);
    
    @Modifying
    @Query(value = """
        DELETE FROM announce_agents ag WHERE ag.id = :announceAgentId AND ag.agent_id = :agentId;
    """, nativeQuery = true)
    void deleteAnnounceAgentsByIdAndAgentId(@Param("announceAgentId") Long announceAgentId, @Param("agentId") Long agentId);
    
    @Query(value = """
        SELECT * FROM announce_agents WHERE agent_id = :agentId;
    """, nativeQuery = true)
    List<AnnounceAgent> findAllByAgentId(@Param("agentId") Long agentId);
    
    @Query(value = """
        SELECT u.id, u.name, u.description, u.image, u.phone, u.line_id, (u.phone_verified AND u.email_verified) AS is_verify
        FROM announce_agents aa
        JOIN users u ON u.id = aa.agent_id
        WHERE announce_id = :announceId;
    """, nativeQuery = true)
    List<AgentDTO> findAnnounceAgentByAnnounceId(@Param("announceId") Long announceId);
    
    @Query(value = """
        SELECT * FROM announce_agents ag WHERE ag.announce_id = :announceId AND ag.agent_id = :agentId;
    """, nativeQuery = true)
    Optional<AnnounceAgent> findByAnnounceIdAndAgentId(@Param("announceId") Long announceId, @Param("agentId") Long agentId);
}

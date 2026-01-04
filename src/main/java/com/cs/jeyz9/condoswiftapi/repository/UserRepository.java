package com.cs.jeyz9.condoswiftapi.repository;

import com.cs.jeyz9.condoswiftapi.dto.ShowUserDetailsDTO;
import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
    
    List<User> findUserByRoles(Set<Role> roles);
    
    @Query(value = """
        SELECT id, name, description, email, phone, line_id FROM users WHERE email = :email;
    """, nativeQuery = true)
    Optional<ShowUserDetailsDTO> findUserDetailsById(@Param("email") String email);
    
    @Modifying
    @Query(value = """
        INSERT INTO user_role(user_id, role_id) VALUES (:userId, :roleId);
""", nativeQuery = true)
    void saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    @Modifying
    @Query(value = """
        DELETE FROM user_role WHERE user_id = :userId AND role_id = :roleId;
    """, nativeQuery = true)
    void deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 
        FROM user_role
        WHERE user_id = :userId 
          AND role_id = :roleId
    )
""", nativeQuery = true)
    boolean existsUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}

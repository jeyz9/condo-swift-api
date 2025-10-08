package com.cs.jeyz9.condoswiftapi.seeders;

import com.cs.jeyz9.condoswiftapi.models.Role;
import com.cs.jeyz9.condoswiftapi.models.RoleName;
import com.cs.jeyz9.condoswiftapi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    
    @Autowired
    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) {
        if(roleRepository.count() == 0L) {
            List<Role> roles = new ArrayList<>();
            roles.add(new Role(RoleName.ADMIN));
            roles.add(new Role(RoleName.MODERATOR));
            roles.add(new Role(RoleName.AGEN));
            roles.add(new Role(RoleName.USER));
            roleRepository.saveAll(roles);
        }
    }
}

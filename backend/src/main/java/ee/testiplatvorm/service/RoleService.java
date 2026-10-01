package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.role.dto.RoleResponse;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.Role;
import ee.testiplatvorm.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAllRoleResponses();
    }

    public Role getValidRoleBy(Integer roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new PrimaryKeyNotFoundException("roleId", roleId));
    }
}

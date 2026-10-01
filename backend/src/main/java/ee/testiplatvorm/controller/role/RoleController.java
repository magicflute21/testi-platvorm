package ee.testiplatvorm.controller.role;

import ee.testiplatvorm.controller.role.dto.RoleResponse;
import ee.testiplatvorm.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/api/roles")
    @Operation(summary = "Tagastab kõik rollid (nt kasutaja lisamise vormi valikute jaoks).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public List<RoleResponse> findAllRoles() {
        return roleService.findAllRoles();
    }
}

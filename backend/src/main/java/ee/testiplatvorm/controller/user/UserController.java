package ee.testiplatvorm.controller.user;


import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users")
    public List<UserResponse> findAllUsers() {

        List<UserResponse> allUsersResponses = userService.findAllUsers();
        return allUsersResponses;
    }

    @DeleteMapping("/api/users/{userId}")
    @Operation(summary = "Kustutab kasutaja ja jätab staatusesse inactive tähisega I.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ei leidnud primary keyd 'userId' väärtusega ?",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}



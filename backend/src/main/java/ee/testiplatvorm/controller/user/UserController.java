package ee.testiplatvorm.controller.user;


import ee.testiplatvorm.controller.user.dto.NewUserRequest;
import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.controller.user.dto.UserStatusRequest;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/api/users")
    @Operation(summary = "Lisab uue kasutaja koos parooli ja profiiliga (ees- ja perekonnanimi), valitud rolliga ja aktiivse staatusega (A) ning lisab ta valitud gruppidesse (groupIds, võib olla tühi).")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Vigane sisend (nt puudub e-post, parool, nimi või roll, e-post on vales vormingus, parool on lühem kui 6 märki), 'errorCode': INCORRECT_INPUT",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Selle e-postiga kasutaja on juba olemas, 'message': Selle e-posti aadressiga kasutaja on juba olemas, 'errorCode': EMAIL_ALREADY_EXISTS",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ei leidnud primary keyd 'roleId' või 'groupId' väärtusega ?",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        userService.addUser(newUserRequest);
    }

    @PatchMapping("/api/users/{userId}/status")
    @Operation(summary = "Muudab kasutaja staatust (A - aktiivne, P - ootel, I - kustutatud).")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Vigane staatus, 'errorCode': INCORRECT_INPUT",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ei leidnud primary keyd 'userId' väärtusega ?",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void updateUserStatus(@PathVariable Integer userId, @RequestBody @Valid UserStatusRequest userStatusRequest) {
        userService.updateUserStatus(userId, userStatusRequest.getStatus());
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



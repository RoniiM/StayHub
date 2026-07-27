package com.stayhub.controller;

import com.stayhub.dto.UserResponse;
import com.stayhub.entity.User;
import com.stayhub.exception.ErrorResponse;
import com.stayhub.security.CurrentUser;
import com.stayhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Authenticated-user self-service operations.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Become a host", description = "Grants the ROLE_HOST role to the authenticated user, in addition to their existing roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Host role granted"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/become-host")
    public ResponseEntity<UserResponse> becomeHost(@CurrentUser User currentUser) {
        return ResponseEntity.ok(userService.becomeHost(currentUser));
    }
}

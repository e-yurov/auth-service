package com.rc.mentorship.authservice.controller;

import com.rc.mentorship.authservice.dto.request.UserUpdateRequest;
import com.rc.mentorship.authservice.dto.response.OfficeIdResponse;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.exception.details.ErrorDetails;
import com.rc.mentorship.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Пользователи")
@SecurityRequirement(name = "Keycloak")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Получение всех пользователей"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех пользователей",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Page<UserResponse>> findAll(
            @Parameter(name = "pageNumber", description = "Номер страницы")
            @RequestParam(defaultValue = "0")
            Integer pageNumber,
            @Parameter(name = "pageSize", description = "Размер страницы")
            @RequestParam(defaultValue = "10")
            Integer pageSize
    ) {
        return ResponseEntity.ok(userService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @Operation(
            summary = "Получение пользователя по id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено пользователя по данному id",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetails.class)
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, null) or hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<UserResponse> findById(
            @Parameter(name = "id", in = ParameterIn.PATH)
            @PathVariable("id")
            UUID id
    ) {
        UserResponse response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/office-id")
    public ResponseEntity<OfficeIdResponse> findOfficeIdByKeycloakId(
            @RequestParam
            String keycloakId
    ) {
        OfficeIdResponse response = userService.getOfficeIdByKeycloakId(keycloakId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновление пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено пользователя по данному id",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetails.class)
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(
            @Parameter(name = "id", in = ParameterIn.PATH)
            @PathVariable("id")
            UUID id,
            @RequestBody
            UserUpdateRequest updateRequest
    ) {
        updateRequest.setId(id);
        UserResponse response = userService.update(updateRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удаление пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление пользователя",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema
                            )
                    }
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(name = "id", in = ParameterIn.PATH)
            @PathVariable("id")
            UUID id
    ){
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}

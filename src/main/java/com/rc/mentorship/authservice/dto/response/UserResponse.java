package com.rc.mentorship.authservice.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserResponse implements Serializable {
    private UUID id;
    private String name;
    private String email;
    private List<String> roles;
}

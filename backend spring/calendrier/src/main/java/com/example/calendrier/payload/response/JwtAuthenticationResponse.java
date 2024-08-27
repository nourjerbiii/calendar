package com.example.calendrier.payload.response;

import com.example.calendrier.entity.User;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private User.ERole role;
}

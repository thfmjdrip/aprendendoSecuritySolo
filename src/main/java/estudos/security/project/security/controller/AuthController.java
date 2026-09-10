package estudos.security.project.security.controller;

import estudos.security.project.security.entities.Dto.LoginRequest;
import estudos.security.project.security.entities.Dto.LoginResponse;
import estudos.security.project.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService service;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse>authLoginToken(@RequestBody LoginRequest loginRequest){
        var user = service.findByUserEmail(loginRequest.email());
        service.isLoginCorrect(loginRequest,bCryptPasswordEncoder);
        var now = Instant.now();
        var expiresIn = 300L;
        var scopes = Optional.ofNullable(user.getRoleUser())
                .map(role -> role.getName().toUpperCase())
                .orElse("");
        var  claims = JwtClaimsSet.builder()
                .issuer("mydb")
                .subject(user.getId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scopes",scopes)
                .issuedAt(now).build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(jwtValue,expiresIn));

    }

}

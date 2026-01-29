package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.auth.LoginRequest;
import qtedu.Impact_design.api.dto.request.auth.SignupRequest;
import qtedu.Impact_design.api.dto.response.auth.TokenResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.common.response.SuccessOnlyResponse;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse<TokenResponse>> login(
            @RequestBody LoginRequest request
    ) {
        JwtToken jwtToken = authService.login(request.getLoginId(), request.getPassword());
        TokenResponse response = TokenResponse.of(
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken().getToken()
        );
        return ResponseHelper.success(response);
    }

    @PostMapping("/teacher/login")
    public ResponseEntity<HttpResponse<TokenResponse>> teacherLogin(
            @RequestBody LoginRequest request
    ) {
        JwtToken jwtToken = authService.teacherLogin(request.getLoginId(), request.getPassword());
        TokenResponse response = TokenResponse.of(
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken().getToken()
        );
        return ResponseHelper.success(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> signup(
            @RequestBody SignupRequest request
    ) {
        authService.signup(request.getLoginId(), request.getPassword(), request.getCode());
        return ResponseHelper.successOnly();
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> logout(
            @CurrentUser UserId userId
    ) {
        authService.logout(userId);
        return ResponseHelper.successOnly();
    }
}

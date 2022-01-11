package beside.sunday8turtle.pickabookserver.modules.user.controller;

import beside.sunday8turtle.pickabookserver.common.exception.BaseException;
import beside.sunday8turtle.pickabookserver.common.exception.IllegalStatusException;
import beside.sunday8turtle.pickabookserver.common.exception.InvalidParamException;
import beside.sunday8turtle.pickabookserver.common.response.CommonResponse;
import beside.sunday8turtle.pickabookserver.common.response.ErrorCode;
import beside.sunday8turtle.pickabookserver.common.security.CustomSecurityException;
import beside.sunday8turtle.pickabookserver.common.util.RedisUtil;
import beside.sunday8turtle.pickabookserver.config.jwt.JwtTokenProvider;
import beside.sunday8turtle.pickabookserver.modules.user.domain.User;
import beside.sunday8turtle.pickabookserver.modules.user.dto.TokenRequestDTO;
import beside.sunday8turtle.pickabookserver.modules.user.dto.UserPostRequestDTO;
import beside.sunday8turtle.pickabookserver.modules.user.dto.UserPostResponseDTO;
import beside.sunday8turtle.pickabookserver.modules.user.dto.UserSignUpRequestDTO;
import beside.sunday8turtle.pickabookserver.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @PostMapping("/signup")
    public CommonResponse signup(@RequestBody UserSignUpRequestDTO dto) {
        userService.getUserByEmail(dto.getEmail()).ifPresent(m -> { throw new IllegalStatusException("이미 존재하는 회원입니다."); });
        userService.registerUser(dto);
        return CommonResponse.success();
    }

    @PostMapping("")
    public CommonResponse<UserPostResponseDTO> login(@RequestBody UserPostRequestDTO dto) {
        Optional<User> user = userService.getUserByEmailAndPassword(dto.getEmail(), dto.getPassword());
        user.orElseThrow(() -> new InvalidParamException());
        String accessToken = jwtTokenProvider.generateToken(String.valueOf(user.get().getEmail()), user.get().getRoleList());
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.get().getEmail()), user.get().getRoleList());
        redisUtil.setValues(refreshToken, user.get().getEmail(), JwtTokenProvider.refreshTokenValidSecond);
        return CommonResponse.success(new UserPostResponseDTO(accessToken, jwtTokenProvider.getExpireDate(accessToken), refreshToken));
    }

    @PostMapping("/reissue")
    public CommonResponse<UserPostResponseDTO> reissue(@RequestBody TokenRequestDTO dto) {
        if(!redisUtil.hasValues(dto.getRefreshToken())) {
            throw new CustomSecurityException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }
        String userEmail = redisUtil.getValues(dto.getRefreshToken());
        String reissueToken = jwtTokenProvider.generateToken(userEmail, Arrays.asList("ROLE_USER"));
        return CommonResponse.success(
                new UserPostResponseDTO(
                        reissueToken,
                        jwtTokenProvider.getExpireDate(reissueToken),
                        dto.getRefreshToken()));
    }

    @DeleteMapping("/logout")
    public CommonResponse logout(@RequestBody TokenRequestDTO dto) {

        if(redisUtil.hasValues(dto.getRefreshToken())) {
            redisUtil.delValues(dto.getRefreshToken());
        }

        return CommonResponse.success();
    }

}

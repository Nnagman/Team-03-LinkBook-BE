package com.prgrms.team03linkbookbe.unit.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.prgrms.team03linkbookbe.common.exception.NoDataException;
import com.prgrms.team03linkbookbe.interest.dto.InterestDto;
import com.prgrms.team03linkbookbe.interest.entity.Field;
import com.prgrms.team03linkbookbe.interest.entity.Interest;
import com.prgrms.team03linkbookbe.user.dto.RegisterRequestDto;
import com.prgrms.team03linkbookbe.user.dto.RegisterResponseDto;
import com.prgrms.team03linkbookbe.user.dto.UserUpdateRequestDto;
import com.prgrms.team03linkbookbe.user.dto.UserResponseDto;
import com.prgrms.team03linkbookbe.user.entity.User;
import com.prgrms.team03linkbookbe.user.exception.DuplicatedEmailException;
import com.prgrms.team03linkbookbe.user.exception.LoginFailureException;
import com.prgrms.team03linkbookbe.user.repository.UserRepository;
import com.prgrms.team03linkbookbe.user.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @DisplayName("register 메서드 : ")
    @Nested
    class Resister {

        String email = "user@gmail.com";
        String password = "user123!";
        User user = User.builder()
            .id(1L)
            .email(email)
            .password("$2a$10$VgKG4LYAucDmh0PoSxjD6OaW8ADf7VUMf5ysPsQr5vh1QoVI7yXu6")
            .build();

        RegisterRequestDto requestDto = RegisterRequestDto.builder()
            .email(email)
            .password(password)
            .build();

        @Test
        @DisplayName("사용자 회원가입 할 수 있다.")
        void SUCCESS_RESISTER_TEST() {
            // Given
            given(userRepository.existsByEmail(email)).willReturn(false);
            given(passwordEncoder.encode(password)).willReturn(
                "$2a$10$VgKG4LYAucDmh0PoSxjD6OaW8ADf7VUMf5ysPsQr5vh1QoVI7yXu6");
            given(userRepository.save(any(User.class))).willReturn(user);

            // When
            RegisterResponseDto responseDto = userService.register(requestDto);

            // Then
            assertThat(responseDto.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입을 할 수 없다. 400 예외")
        void DUPLICATED_EMAIL_EXCEPTION_TEST() {
            given(userRepository.existsByEmail(email)).willReturn(true);

            // When Then
            assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(DuplicatedEmailException.class);
        }
    }

    @DisplayName("login 메서드 : ")
    @Nested
    class Login {

        String email = "user@gmail.com";
        String credentials = "user123!";
        String password = "$2a$10$VgKG4LYAucDmh0PoSxjD6OaW8ADf7VUMf5ysPsQr5vh1QoVI7yXu6";
        User user = User.builder()
            .id(1L)
            .email(email)
            .password(password)
            .build();

        @Test
        @DisplayName("사용자 로그인 할 수 있다.")
        void LOGIN_TEST() {
            // Given
            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
            given(passwordEncoder.matches(credentials, password)).willReturn(true);

            // When
            User user = userService.login(email, credentials);

            // Then
            assertThat(user.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 이메일로 로그일 할 수 없다.")
        void NO_EXIST_USER() {
            // Given
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // When Then
            assertThatThrownBy(() -> userService.login(email, credentials))
                .isInstanceOf(LoginFailureException.class);
        }

        @Test
        @DisplayName("비밀번호가 틀리면 로그인 할 수 없다.")
        void CHECK_PASSWORD_FAILURE() {
            // Given
            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
            given(passwordEncoder.matches(credentials, password)).willReturn(false);

            // When Then
            assertThatThrownBy(() -> userService.login(email, credentials))
                .isInstanceOf(LoginFailureException.class);
        }

    }


    @DisplayName("findByEmail 메서드 : ")
    @Nested
    class FindByEmail {

        String email = "user@gmail.com";
        String password = "user123!";
        User user = User.builder()
            .id(1L)
            .email(email)
            .password("$2a$10$VgKG4LYAucDmh0PoSxjD6OaW8ADf7VUMf5ysPsQr5vh1QoVI7yXu6")
            .build();

        @Test
        @DisplayName("이메일로 사용자 정보를 찾아서 반환할 수 있다.")
        void FIND_BY_EMAIL_TEST() {
            // Given
            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

            // When
            UserResponseDto responseDto = userService.findByEmail(email);

            // Then
            assertThat(responseDto.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 이메일로 조회할 수 없다.")
        void NO_EXIST_USER() {
            // Given
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // When Then
            assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(NoDataException.class);
        }
    }

    @DisplayName("updateUser 메서드 : ")
    @Nested
    class UpdateUser {
        InterestDto interestRequestDto = InterestDto.builder()
            .field(Field.backEndDeveloper)
            .build();
        List<InterestDto> interestRequestDtos = List.of(interestRequestDto);

        String name = "updateName";
        String url = "updateUrl";
        String introduce = "updateIntroduce";
        UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder()
            .name(name)
            .image(url)
            .introduce(introduce)
            .interests(interestRequestDtos)
            .build();

        String email = "user@gmail.com";

        Interest interest = Interest.builder()
            .field(Field.frontEndDeveloper)
            .build();

        User user = User.builder()
            .id(1L)
            .email(email)
            .password("$2a$10$VgKG4LYAucDmh0PoSxjD6OaW8ADf7VUMf5ysPsQr5vh1QoVI7yXu6")
            .build();


        @Test
        @DisplayName("사용자의 정보를 수정할 수 있다.")
        void UPDATE_USER_TEST() {
            // Given
            user.addInterest(interest);
            given(userRepository.findByEmailFetchJoinInterests(email))
                .willReturn(Optional.of(user));

            // When
            userService.updateUser(updateRequestDto, email);


            // Then
            assertThat(user.getInterests().get(0).getField()).isEqualTo(Field.backEndDeveloper);
            assertThat(user.getName()).isEqualTo(name);
            assertThat(user.getImage()).isEqualTo(url);
            assertThat(user.getIntroduce()).isEqualTo(introduce);
        }

        @Test
        @DisplayName("존재하지 않는 사용자라면 수정할 수 없다.")
        void NO_EXIST_EXCEPTION () {
            // Given
            given(userRepository.findByEmailFetchJoinInterests(email))
                .willReturn(Optional.empty());

            // When Then
            assertThatThrownBy(() -> userService.updateUser(updateRequestDto, email))
                .isInstanceOf(NoDataException.class);
        }
    }


}

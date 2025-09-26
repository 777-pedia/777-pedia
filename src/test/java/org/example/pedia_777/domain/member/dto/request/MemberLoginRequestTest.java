package org.example.pedia_777.domain.member.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberLoginRequestTest {

    private Validator validator;


    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("로그인 시 이메일을 입력하지 않으면 예외가 발생한다.")
    public void occurEmailBlankException() {
        //given
        String email = null;
        String password = "test";

        MemberLoginRequest memberRequest = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains("이메일은 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인 시 이메일 형식이 올바르지 않을 경우 예외가 발생한다.")
    public void occurEmailFormatWrongException() {
        //given
        String email = "test@@test.com";
        String password = "test";

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("잘못된 이메일 형식입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인 시 이메일이 비어있을 경우 예외가 발생한다.")
    public void occurEmailEmptyException() {
        //given
        String email = "";
        String password = "test";

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("이메일은 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인 시 이메일을 입력할 경우 예외가 발생하지 않는다.")
    public void notOccurEmailSuccessException() {
        //given
        String email = "test@test.com";
        String password = "0000";

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains()
                .hasSize(0);
    }

    @Test
    @DisplayName("로그인 시 비밀번호를 입력하지 않으면 예외가 발생한다.")
    public void occurPasswordBlankException() {
        //given
        String email = "test@test.com";
        String password = null;

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("비밀번호는 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 비어있을 경우 예외가 발생한다.")
    public void occurPasswordEmptyException() {
        //given
        String email = "test@test.com";
        String password = "";

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("비밀번호는 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("로그인 시 비밀번호를 입력 할 경우 예외가 발생하지 않는다.")
    public void occurPasswordSuccessException() {
        //given
        String email = "test@test.com";
        String password = "0000";

        MemberLoginRequest requestDto = new MemberLoginRequest(email, password);

        //when
        Set<ConstraintViolation<MemberLoginRequest>> violations = validator.validate(requestDto);

        //then
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains()
                .hasSize(0);
    }
}
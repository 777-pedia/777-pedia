package org.example.pedia_777.domain.member.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRequestTest {

    private Validator validator;


    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("회원가입 시 이메일을 작성하지 않은 경우 예외가 발생한다.")
    public void occurEmailBlankException() {
        //given
        String email = null;
        String password = "test";
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains("이메일은 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("회원가입 시 올바르지 않은 이메일 형식으로 작성 시 예외가 발생한다.")
    public void occurEmailFormatWrongException() {
        //given
        String email = "test@@test.com";
        String password = "test";
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains("잘못된 이메일 형식입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("올바른 이메일을 입력 할 경우 예외가 발생하지 않는다.")
    public void notOccurEmailSuccessException() {
        //given
        String email = "test@test.com";
        String password = "test";
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains()
                .hasSize(0);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호를 작성하지 않은 경우 예외가 발생한다.")
    public void occurPasswordBlankException() {
        //given
        String email = "test@test.com";
        String password = null;
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains("비밀번호는 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("올바른 비밀번호를 입력 할 경우 예외가 발생하지 않는다.")
    public void notOccurPasswordSuccessException() {
        //given
        String email = "test@test.com";
        String password = "test";
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains()
                .hasSize(0);
    }

    @Test
    @DisplayName("회원가입 시 닉네임을 작성하지 않은 경우 예외가 발생한다.")
    public void occurNicknameBlankException() {
        //given
        String email = "test@test.com";
        String password = "test";
        String nickname = null;

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains("닉네임은 필수값입니다.")
                .hasSize(1);
    }

    @Test
    @DisplayName("올바른 닉네임을 입력 할 경우 예외가 발생하지 않는다.")
    public void notOccurNicknameSuccessException() {
        //given
        String email = "test@test.com";
        String password = "test";
        String nickname = "tester";

        MemberRequest memberRequest = new MemberRequest(email, password, nickname);

        //when
        Set<ConstraintViolation<MemberRequest>> validate = validator.validate(memberRequest);

        //then
        assertThat(validate)
                .extracting(ConstraintViolation::getMessage)
                .contains()
                .hasSize(0);
    }
}
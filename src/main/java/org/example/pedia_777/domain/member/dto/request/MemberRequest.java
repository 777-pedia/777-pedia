package org.example.pedia_777.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MemberRequest(
        @NotBlank(message = "이메일은 필수값입니다.")
        @Email(message = "잘못된 이메일 형식입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수값입니다.")
        String password,

        @NotBlank(message = "닉네임은 필수값입니다.")
        String nickname) {
}


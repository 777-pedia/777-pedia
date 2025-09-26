package org.example.pedia_777.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.member.dto.request.MemberLoginRequest;
import org.example.pedia_777.domain.member.dto.request.MemberRequest;
import org.example.pedia_777.domain.member.dto.response.MemberLoginResponse;
import org.example.pedia_777.domain.member.dto.response.MemberResponse;
import org.example.pedia_777.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<GlobalApiResponse<MemberResponse>> signup(@Valid @RequestBody MemberRequest memberRequest) {
        MemberResponse signup = memberService.signup(memberRequest);
        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, signup);
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalApiResponse<MemberLoginResponse>> login(
            @Valid @RequestBody MemberLoginRequest memberLoginRequest) {
        MemberLoginResponse login = memberService.login(memberLoginRequest);

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, login);
    }
}

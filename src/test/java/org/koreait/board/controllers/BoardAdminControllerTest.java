package org.koreait.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.member.contants.Authority;
import org.koreait.member.test.annotations.MockMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@AutoConfigureMockMvc
public class BoardAdminControllerTest {

    @Autowired
    private MockMvc mockMock;

    @Autowired
    private ObjectMapper om;

    private RequestConfig form;


    @BeforeEach
    void init() {
        form = new RequestConfig();
        form.setBid("freetalk");
        form.setName("자유게시판");
    }
    
    @Test
    @MockMember(authority = {Authority.USER, Authority.ADMIN})
    @DisplayName("게시판 설정 등록 테스트")
    void boardRegisterTest() throws Exception {

        String body = om.writeValueAsString(form);

        // 게시판 등록 / 수정
        mockMock.perform(post("/admin/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        // 게시판 목록 조회
        mockMock.perform(get("/admin/config"))
                .andDo(print());
    }
}

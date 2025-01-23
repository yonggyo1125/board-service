package org.koreait.board.services;

import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.BoardData;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDeleteService {
    private final BoardInfoService infoService;
    private final BoardDataRepository boardRepository;
    private final RestTemplate restTemplate;
    private final Utils utils;

    public void delete(Long seq) {

        BoardData item = infoService.get(seq);
        String gid = item.getGid();

        // 파일 삭제 S
        String token = utils.getAuthToken();
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(token)) {
            headers.setBearerAuth(token);
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String apiUrl = utils.serviceUrl("file-service", "/deletes/" + item.getGid());
        restTemplate.exchange(URI.create(apiUrl), HttpMethod.DELETE, request, Void.class);

        // 파일 삭제 E

        boardRepository.delete(item);
        boardRepository.flush();

        // 비회원 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);
    }
}

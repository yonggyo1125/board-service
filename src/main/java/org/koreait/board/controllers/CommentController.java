package org.koreait.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.CommentData;
import org.koreait.board.services.BoardAuthService;
import org.koreait.board.services.comment.CommentDeleteService;
import org.koreait.board.services.comment.CommentInfoService;
import org.koreait.board.services.comment.CommentUpdateService;
import org.koreait.board.validators.CommentValidator;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final Utils utils;
    private final CommentValidator commentValidator;
    private final CommentUpdateService updateService;
    private final BoardAuthService authService;
    private final CommentInfoService infoService;
    private final CommentDeleteService deleteService;

    /**
     * 댓글 작성, 수정
     *
     * @return
     */
    @PostMapping("/save")
    public JSONData save(@RequestBody @Valid RequestComment form, Errors errors) {
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";
        if (mode.equals("edit")) { // 수정 권한 여부 체크
            commonProcess(form.getSeq());
        }
        
        commentValidator.validate(form, errors);
        
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        CommentData data = updateService.save(form);

        return new JSONData(data);
    }

    /**
     * 댓글 한개 조회
     *  - 댓글 수정시 기초 데이터(프론트앤드)
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {
        commonProcess(seq);

        CommentData item = infoService.get(seq);

        return new JSONData(item);
    }

    /**
     * 댓글 목록 조회
     *
     * @param seq : 게시글 번호
     * @return
     */
    @GetMapping("/list/{seq}")
    public JSONData list(@PathVariable("seq") Long seq) {

        List<CommentData> items = infoService.getList(seq);

        return new JSONData(items);
    }

    /**
     * 댓글 한개 삭제
     *
     * @param seq
     * @return
     */
    @DeleteMapping("/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {
        commonProcess(seq);

        CommentData item = deleteService.delete(seq);

        return new JSONData(item);
    }

    /**
     * 비회원 비밀번호 검증
     *  - 응답코드 204 : 검증 성공
     *  - 응답코드 401 : 검증 실패
     *
     * @params seq : 댓글 번호
     *
     *
     */
    @PostMapping("/password/{seq}")
    public ResponseEntity<Void> validateGuestPassword(@PathVariable("seq") Long seq, @RequestParam(name="password", required = false) String password) {
        if (!StringUtils.hasText(password)) {
            throw new BadRequestException(utils.getMessage("NotBlank.password"));
        }

        HttpStatus status = commentValidator.checkGuestPassword(password, seq) ? HttpStatus.NO_CONTENT : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).build();
    }

    /**
     * 공통 처리
     *
     * @param seq
     */
    private void commonProcess(Long seq) {
        authService.check("comment", seq); // 댓글 권한 체크
    }
}

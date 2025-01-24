package org.koreait.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.services.BoardDeleteService;
import org.koreait.board.services.BoardInfoService;
import org.koreait.board.services.BoardUpdateService;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.board.validators.BoardValidator;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final Utils utils;
    private final BoardValidator boardValidator;
    private final BoardConfigInfoService configInfoService;
    private final BoardUpdateService updateService;
    private final BoardInfoService infoService;
    private final BoardDeleteService deleteService;

    /**
     * 게시판 설정 한개 조회
     *
     * @param bid
     * @return
     */
    @GetMapping("/config/{bid}")
    public JSONData config(@PathVariable("bid") String bid) {
        Board board = configInfoService.get(bid);

        return new JSONData(board);
    }

    /**
     * 게시글 등록, 수정 처리
     *
     * @return
     */
    @PostMapping("/save")
    public JSONData save(@Valid @RequestBody RequestBoard form, Errors errors) {
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";
        commonProcess(form.getBid(), mode); // 공통 처리

        boardValidator.validate(form, errors);

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        BoardData data = updateService.process(form);

        return new JSONData(data);
    }

    /**
     * 게시글 한개 조회
     * - 글보기, 글 수정시에 활용될 수 있음(프론트앤드)
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {
        commonProcess(seq, "view");

        BoardData data = infoService.get(seq);

        return new JSONData(data);
    }

    /**
     * 게시글 목록 조회
     *
     * @param bid
     * @return
     */
    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid, @ModelAttribute BoardSearch search) {
        commonProcess(bid, "list");

        ListData<BoardData> data = infoService.getList(bid, search);

        return new JSONData(data);
    }

    /**
     * 게시글 한개 삭제
     *
     * @param seq
     * @return
     */
    @DeleteMapping("/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {
        commonProcess(seq, "delete");

        boardValidator.checkDelete(seq); // 댓글이 존재하면 삭제 불가
        deleteService.delete(seq);

        return null;
    }

    /**
     * 비회원 비밀번호 검증
     *  - 응답코드 204 : 검증 성공
     *  - 응답코드 401 : 검증 실패
     *
     * @params seq : 게시글 번호
     *
     *
     */
    @PostMapping("/password/{seq}")
    public ResponseEntity<Void> validateGuestPassword(@PathVariable("seq") Long seq, @RequestParam(name="password", required = false) String password) {
        if (!StringUtils.hasText(password)) {
            throw new BadRequestException(utils.getMessage("NotBlank.password"));
        }

        HttpStatus status = boardValidator.checkGuestPassword(password, seq) ? HttpStatus.NO_CONTENT : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).build();
    }

    /**
     * 게시글 번호로 공통 처리
     *
     * @param seq
     * @param mode
     */
    private void commonProcess(Long seq, String mode) {

    }

    /**
     * 게시판 아이디로 공통 처리
     *
     * @param bid
     * @param mode
     */
    private void commonProcess(String bid, String mode) {

    }
}

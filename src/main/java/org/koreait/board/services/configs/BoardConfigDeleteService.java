package org.koreait.board.services.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.QBoardData;
import org.koreait.board.repositories.BoardDataRepository;
import org.koreait.board.repositories.BoardRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigDeleteService {

    private final BoardRepository boardRepository;
    private final BoardDataRepository boardDataRepository;


    /**
     * 게시판 한개 삭제
     *
     * @param bid
     * @return
     */
    public Board process(String bid) {
        QBoardData boardData = QBoardData.boardData;
        if (boardDataRepository.count(boardData.board.bid.eq(bid)) > 0L) {
            return null; // 게시글이 존재하면 게시판 삭제 불가
        }

        Board board = boardRepository.findById(bid).orElse(null);
        if (board != null) {
            boardRepository.delete(board);
            boardRepository.flush();
        }

        return board;
    }

    /**
     * 여러 게시판 일괄 삭제
     *
     * @param bids
     * @return
     */
    public List<Board> process(List<String> bids) {

        List<Board> deleted = new ArrayList<>();
        for (String bid : bids) {
            Board item = process(bid);
            if (item != null) {
                deleted.add(item); // 삭제된 게시판 정보
            }
        }

        return deleted;
    }
}

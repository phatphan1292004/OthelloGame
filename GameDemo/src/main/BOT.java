package main;

import java.util.ArrayList;
import java.util.List;

public class BOT {
    private Board board;

    public BOT(Board board) {
        this.board = board;
    }

    // Đánh giá bàn cờ
    public int evaluateBoard(Board b) {
        int score = 0;
        int color = -1; // AI luôn chơi quân trắng

        // Đánh giá các ô cạnh và góc
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int piece = b.getState(i, j);  // Lấy quân cờ tại (i, j)
                score += piece * color;

                if (b.isSide(i, j)) {
                    score += 10 * piece * color;  // Cạnh bàn cờ
                }

                if (b.isCorner(i, j)) {
                    score += 40 * piece * color;  // Góc bàn cờ
                }
            }
        }

        // Cộng thêm 5 điểm cho mỗi nước đi hợp lệ
        score += b.getValidMoves(color).size() * 5;

        return score;
    }

    // Minimax với Alpha-Beta Pruning
    public int minimax(Board b, int depth, int maximizingColor, int alpha, int beta) {
        // Cơ sở dừng đệ quy
        if (depth == 0 || b.isGameOver()) {
            return evaluateBoard(b);  // Trả về đánh giá bàn cờ tại độ sâu hiện tại
        }

        int value;
        if (maximizingColor == -1) {  // AI chơi quân trắng
            value = Integer.MIN_VALUE;
            List<int[]> validMoves = b.getValidMoves(-1);  // AI chơi quân trắng
            for (int[] move : validMoves) {
                Board copyBoard = b.copyBoard();  // Tạo bản sao của bàn cờ
                copyBoard.setState(move[0], move[1], -1);  // Đặt quân của AI trên bản sao
                value = Math.max(value, minimax(copyBoard, depth - 1, 1, alpha, beta));
                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;  // Cắt tỉa nếu không có gì tốt hơn
            }
            return value;
        } else {
            value = Integer.MAX_VALUE;
            List<int[]> validMoves = b.getValidMoves(1);  // Đối thủ chơi quân đen
            for (int[] move : validMoves) {
                Board copyBoard = b.copyBoard();  // Tạo bản sao của bàn cờ
                copyBoard.setState(move[0], move[1], 1);  // Đặt quân của đối thủ trên bản sao
                value = Math.min(value, minimax(copyBoard, depth - 1, -1, alpha, beta));
                beta = Math.min(beta, value);
                if (beta <= alpha) break;  // Cắt tỉa nếu không có gì tốt hơn
            }
            return value;
        }
    }

    // Tìm nước đi tốt nhất cho AI
    public int[] getBestMove() {
        int bestMoveValue = Integer.MIN_VALUE;
        int[] bestMove = null;

        List<int[]> validMoves = board.getValidMoves(-1);  // AI chơi quân trắng
        for (int[] move : validMoves) {
            // Tạo bản sao của bàn cờ
            Board copyBoard = board.copyBoard();

            // Thực hiện nước đi của AI trên bản sao
            copyBoard.setState(move[0], move[1], -1);
            int moveValue = minimax(copyBoard, 3, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // Nếu nước đi này có giá trị tốt hơn, lưu lại
            if (moveValue > bestMoveValue) {
                bestMoveValue = moveValue;
                bestMove = move;
            }
        }

        return bestMove;
    }
}

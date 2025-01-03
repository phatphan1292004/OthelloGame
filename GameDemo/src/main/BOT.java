package main;

import java.util.ArrayList;
import java.util.List;

public class BOT {
	private Board board;

	public BOT() {
		
	}
	
	public BOT(Board board) {
		this.board = board;
	}

	// Đánh giá bàn cờ
	public int heuristic(Board b) {
		int score = 0;
		int color = -1; // AI
		int totalBlack = 0;
		int totalWhite = 0;

		// Đánh giá các ô cạnh và góc
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int tile = b.getState(i, j);
				score += tile * color;

				if (b.isSide(i, j)) {
					score += 10 * tile * color; // Cạnh bàn cờ
				}

				if (b.isCorner(i, j)) {
					score += 20 * tile * color; // Góc bàn cờ
				}

				if (tile == color) {
					totalWhite++; // số quân ai tăng 1
				} else if (tile == -color) {
					totalBlack++; // số quân đen tăng 1
				}
			}
		}

		// Tính độ chênh lệch giữa quân trắng và quân đen
		int tmp = totalWhite - totalBlack;
		score += tmp * 2;
		score += b.getValidMoves(color).size() * 5;

		return score;
	}

	// Minimax
	public int minimax1(Board b, int depth, boolean maxmin) {
		if (depth == 0 || b.isGameOver()) {
			return heuristic(b);
		}

		if (maxmin) { // MAX
			int tmp = Integer.MIN_VALUE;
			List<int[]> validMoves = b.getValidMoves(-1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], -1);
				int value = minimax1(cloneBoard, depth - 1, false);
				tmp = Math.max(tmp, value);
			}
			return tmp;
		} else { // MIN
			int tmp = Integer.MAX_VALUE;
			List<int[]> validMoves = b.getValidMoves(1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], 1);
				int value = minimax1(cloneBoard, depth - 1, true);
				tmp = Math.min(tmp, value);
			}
			return tmp;
		}
	}

	public int alphaBeta(Board b, int depth, boolean maxmin, int alpha, int beta) {
		if (depth == 0 || b.isGameOver()) {
			return heuristic(b);
		}

		if (maxmin) { // MAX
			int tmp = Integer.MIN_VALUE;
			List<int[]> validMoves = b.getValidMoves(-1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], -1);
				int value = alphaBeta(cloneBoard, depth - 1, false, alpha, beta);
				tmp = Math.max(tmp, value);
				alpha = Math.max(alpha, tmp);
				if (beta <= alpha)
					break;
			}
			return tmp;
		} else { // MIN
			int tmp = Integer.MAX_VALUE;
			List<int[]> validMoves = b.getValidMoves(1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], 1);
				int value = alphaBeta(cloneBoard, depth - 1, true, alpha, beta);
				tmp = Math.min(tmp, value);
				beta = Math.min(beta, tmp);
				if (beta <= alpha)
					break;
			}
			return tmp;
		}
	}

	public int minimax(Board b, int depth, boolean maxmin, int alpha, int beta) {
		if (depth == 0 || b.isGameOver()) {
			return heuristic(b);
		}

		if (maxmin) { // MAX
			int tmp = Integer.MIN_VALUE;
			List<int[]> validMoves = b.getValidMoves(-1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], -1);
				int value = minimax(cloneBoard, depth - 1, false, alpha, beta);

				if (value > tmp) {
					tmp = value;
				}

				alpha = Math.max(alpha, tmp);
				if (beta <= alpha)
					break;
			}
			return tmp;
		} else { // MIN
			int tmp = Integer.MAX_VALUE;
			List<int[]> validMoves = b.getValidMoves(1);
			for (int[] item : validMoves) {
				Board cloneBoard = b.copyBoard();
				cloneBoard.setState(item[0], item[1], 1);
				int value = minimax(cloneBoard, depth - 1, true, alpha, beta);

				if (value < tmp) {
					tmp = value;
				}

				alpha = Math.min(alpha, tmp);
				if (beta <= alpha)
					break;
			}
			return tmp;
		}

	}

	// Tìm nước đi tốt nhất cho AI
	public int[] getBestMove() {
		int bestMoveValue = Integer.MIN_VALUE;
		int[] bestMove = null;

		List<int[]> validMoves = board.getValidMoves(-1);
		for (int[] move : validMoves) {
			// Tạo bản sao của bàn cờ
			Board copyBoard = board.copyBoard();

			// Thực hiện nước đi của AI trên bản sao
			copyBoard.setState(move[0], move[1], -1);
			int moveValue = minimax(copyBoard, 3, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

			if (moveValue > bestMoveValue) {
				bestMoveValue = moveValue;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	// Đo thời gian và bộ nhớ cho Minimax
	public void thoiGianMinimax(int depth) {

        System.gc();

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

        minimax(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        long endTime = System.nanoTime();
        long timeTaken = (endTime - startTime) / 1000000; // milliseconds
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();


        System.out.println("Minimax - Thời gian chạy: " + timeTaken + " milliseconds");
        System.out.println("Bộ nhớ trước khi chạy: " + usedMemoryBefore / 1024 + " KB");
        System.out.println("Bộ nhớ sau khi chạy: " + usedMemoryAfter / 1024 + " KB");
        System.out.println("Bộ nhớ sử dụng: " + (usedMemoryAfter - usedMemoryBefore) / 1024 + " KB");
    }

    // Đo thời gian và bộ nhớ cho Alpha-Beta
    public void thoiGianAlphaBeta(int depth) {
        System.gc();

        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Gọi thuật toán Alpha-Beta
        alphaBeta(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        long endTime = System.nanoTime();
        long timeTaken = (endTime - startTime) / 1000000; // milliseconds
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();

        // In kết quả
        System.out.println("Alpha-Beta - Thời gian chạy: " + timeTaken + " milliseconds");
        System.out.println("Bộ nhớ trước khi chạy: " + usedMemoryBefore / 1024 + " KB");
        System.out.println("Bộ nhớ sau khi chạy: " + usedMemoryAfter / 1024 + " KB");
        System.out.println("Bộ nhớ sử dụng: " + (usedMemoryAfter - usedMemoryBefore) / 1024 + " KB");
    }  


    public static void main(String[] args) {
        // Khởi tạo bàn cờ và tạo đối tượng BOT
        Board b = new Board();
        BOT bot = new BOT(b);

        // Đo thời gian và bộ nhớ cho Minimax và Alpha-Beta
        bot.thoiGianMinimax(6);
        bot.thoiGianAlphaBeta(6);
    }
}

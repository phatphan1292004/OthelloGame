package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int[][] boardState;
    private int currentPlayer;
    private Tile tile;

    // Mảng các hướng di chuyển (cả thẳng và chéo)
    private int[][] offsets = {
        {0, 1}, {1, 0}, {0, -1}, {-1, 0}, // (dọc, ngang)
        {1, 1}, {1, -1}, {-1, -1}, {-1, 1} // (chéo)
    };

    public Board() {
        boardState = new int[8][8];

        // Thiết lập mặc định cho bàn cờ
        boardState[3][3] = -1; // Quân trắng
        boardState[3][4] = 1;  // Quân đen
        boardState[4][3] = 1;  // Quân đen
        boardState[4][4] = -1; // Quân trắng

        currentPlayer = 1; // Khởi tạo người chơi là đen (1)
    }

    public void setState(int row, int col, int state) {
        if (boardState[row][col] == 0) {  
            boardState[row][col] = state;
            flipPieces(row, col, state);  
            switchPlayer();  
        }
    }
    
    public Tile getTile(int row, int col) {
        if (isInBounds(row, col)) {
            return new Tile(row, col, boardState[row][col]);
        }
        return null;
    }


    public void printBoardState() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(boardState[i][j] + " ");  
            }
            System.out.println();
        }
    }

    // Lật quân cờ đối phương
    public void flipPieces(int row, int col, int player) {
        // Kiểm tra theo tất cả các hướng
        for (int[] offset : offsets) {
            int x = row + offset[0];
            int y = col + offset[1];
            List<int[]> toFlip = new ArrayList<>(); // Danh sách các quân cờ sẽ bị lật

            // Lặp qua các ô trong hướng này để tìm quân đối phương
            while (x >= 0 && x < 8 && y >= 0 && y < 8 && boardState[x][y] == -player) {
                toFlip.add(new int[]{x, y});
                x += offset[0]; // Di chuyển sang ô tiếp theo theo hướng x
                y += offset[1]; // Di chuyển sang ô tiếp theo theo hướng y
            }

            // Nếu tìm thấy quân của người chơi ở cuối chuỗi, lật quân cờ đối phương
            if (x >= 0 && x < 8 && y >= 0 && y < 8 && boardState[x][y] == player) {
                // Lật tất cả các quân cờ đối phương bị kẹp
                for (int[] flip : toFlip) {
                    boardState[flip[0]][flip[1]] = player;  
                }
            }
        }
    }

    // Lấy trạng thái của ô
    public int getState(int row, int col) {
        return boardState[row][col];
    }

    // Chuyển người chơi
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? -1 : 1;
    }

    // Lấy ra người chơi gần nhất
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    // Hàm đếm số lượng quân trắng, quân đen trên bàn cờ   
    public int countTileInBoard(int player) {
        int count = 0;

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                if (boardState[i][j] == player) {
                    count++;  
                }
            }
        }
        
        return count;
    }
    
    public String checkWinner() {
        int blackCount = countTileInBoard(1);
        int whiteCount = countTileInBoard(-1);
        
        if (blackCount > whiteCount) {
            return "Black wins!";
        } else if (whiteCount > blackCount) {
            return "White wins!";
        } else {
            return "It's a draw!";
        }
    }
    
    public boolean isGameOver() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j] == 0) {
                    return false;
                }
            }
        }
        return true; 
    }
    
    public boolean isMoveValid(int row, int col, int player) {
        if (boardState[row][col] != 0) {
            return false;
        }

        // Kiểm tra tất cả các hướng (ngang, dọc, chéo)
        for (int[] offset : offsets) {
            int dx = offset[0];  // Hướng theo hàng (row)
            int dy = offset[1];  // Hướng theo cột (col)

            int x = row + dx;
            int y = col + dy;

            boolean opponentFound = false;

            // Duyệt theo hướng, tìm quân đối phương
            while (isInBounds(x, y) && boardState[x][y] == -player) {
                opponentFound = true;
                x += dx;
                y += dy;
            }

            // Nếu tìm thấy quân đối phương, kiểm tra xem có gặp quân của chính người chơi không
            if (opponentFound && isInBounds(x, y) && boardState[x][y] == player) {
                return true;  // Nước đi hợp lệ
            }
        }

        return false;  
    }

    // Lấy ra danh sách các nước đi hợp lệ    
    public List<int[]> getValidMoves(int player) {
        List<int[]> validMoves = new ArrayList<>();

        // Duyệt qua tất cả các ô trên bàn cờ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Kiểm tra nếu ô trống và nước đi hợp lệ cho người chơi này
                if (boardState[i][j] == 0 && isMoveValid(i, j, player)) {
                    validMoves.add(new int[]{i, j});  
                }
            }
        }

        return validMoves;
    }

    // Hàm kiểm tra nếu một ô có nằm trong giới hạn bàn cờ không
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isSide(int row, int col) {
        return (row == 0 || row == 7 || col == 0 || col == 7);
    }

    public boolean isCorner(int row, int col) {
        return (row == 0 && col == 0) || (row == 0 && col == 7) || (row == 7 && col == 0) || (row == 7 && col == 7);
    }

    public void reset() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardState[i][j] = 0;
            }
        }

        boardState[3][3] = -1; 
        boardState[3][4] = 1;  
        boardState[4][3] = 1;  
        boardState[4][4] = -1; 

        currentPlayer = 1; 
    }

    public Board copyBoard() {
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(this.boardState[i], 0, newBoard.boardState[i], 0, 8);
        }
        return newBoard;
    }
}

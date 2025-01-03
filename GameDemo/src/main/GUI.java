package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GUI extends JFrame implements ActionListener {
	private final int WIDTH = 600;
	private final int HEIGHT = 600;
	private final int SIZE = 8;
	private JMenuBar menuBar;
	private JMenu file, game, subMenu;
	private JMenuItem quit, reset, newPlayer, newAI;

	private Tile[][] buttonGrid = new Tile[SIZE][SIZE];
	private JPanel buttonPanel = new JPanel();
	private Board board = new Board();
	private boolean playingWithAI = false;

	private JPanel infoPanel;
	private JLabel blackCountLabel;
	private JLabel whiteCountLabel;
	private JLabel turnLabel;
	private JPanel welcomePanel;
	private JLabel welcomeLabel;
	private BOT bot;

	public GUI() {
		setupMenu();
		setupInfoPanel();
		setupWelcomePanel();

		buttonPanel.setLayout(new GridLayout(SIZE, SIZE));
		this.setTitle("Othello");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void setupMenu() {
		file = new JMenu("File");
		subMenu = new JMenu("New");
		newPlayer = new JMenuItem("Game vs Human");
		newPlayer.addActionListener(this);
		newAI = new JMenuItem("Game vs AI");
		newAI.addActionListener(this);
		subMenu.add(newPlayer);
		subMenu.add(newAI);
		quit = new JMenuItem("Quit");
		quit.addActionListener(this);
		file.add(subMenu);
		file.add(quit);

		game = new JMenu("Game");
		reset = new JMenuItem("Reset");
		reset.addActionListener(this);
		game.add(reset);

		menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(game);
		this.setJMenuBar(menuBar);
	}

	public void setupInfoPanel() {
		infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());
		blackCountLabel = new JLabel("Black: 0");
		whiteCountLabel = new JLabel("White: 0");
		infoPanel.add(blackCountLabel);
		infoPanel.add(whiteCountLabel);

		turnLabel = new JLabel("Turn: Black");
		turnLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.add(infoPanel, BorderLayout.NORTH);
		this.add(turnLabel, BorderLayout.SOUTH);
	}

	public void setupWelcomePanel() {
		welcomePanel = new JPanel();
		welcomePanel.setLayout(new BorderLayout());
		welcomeLabel = new JLabel("OthelloGame", SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Serif", Font.BOLD, 32));
		welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
		this.add(welcomePanel, BorderLayout.CENTER);
	}

	public void initBoard() {
		blackCountLabel.setText("Black: " + board.countTileInBoard(1));
		whiteCountLabel.setText("White: " + board.countTileInBoard(-1));
		buttonPanel.removeAll();

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (buttonGrid[i][j] == null) {
					buttonGrid[i][j] = new Tile(this, i, j);
				}
				buttonGrid[i][j].setPreferredSize(new Dimension(40, 40));
				buttonPanel.add(buttonGrid[i][j]);
			}
		}

		this.remove(welcomePanel);
		this.add(buttonPanel, BorderLayout.CENTER);
		SwingUtilities.updateComponentTreeUI(this);
		repaintBoard();

		if (playingWithAI) {
			bot = new BOT(board); // Khởi tạo bot khi chơi với AI
		}
	}

	public void resetButtonGrid() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int state = board.getState(i, j);
				buttonGrid[i][j].setState(state);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == newPlayer) {
			playingWithAI = false;
			board.reset();
			initBoard();
		}

		if (e.getSource() == newAI) {
			playingWithAI = true;
			board.reset();
			initBoard();
		}

		if (e.getSource() == quit) {
			System.exit(0);
		}

		if (e.getSource() == reset) {
			board.reset();
			initBoard();
		}
	}

	public void onClick(int row, int col) {
		// Kiểm tra xem ô này có hợp lệ để đặt quân không
		if (board.isMoveValid(row, col, board.getCurrentPlayer())) {
			// Đặt quân cờ ở ô đã chọn
			board.setState(row, col, board.getCurrentPlayer());

			repaintBoard(); // Cập nhật lại giao diện

			// Cập nhật số lượng quân đen và quân trắng trên màn hình
			int blackCount = board.countTileInBoard(1);
			int whiteCount = board.countTileInBoard(-1);
			System.out.println("After player's move - Black Count: " + blackCount + " White Count: " + whiteCount);

			blackCountLabel.setText("Black: " + blackCount);
			whiteCountLabel.setText("White: " + whiteCount);

			// Cập nhật lượt chơi sau khi người chơi di chuyển
			if (board.getCurrentPlayer() == 1) {
				turnLabel.setText("Turn: Black");
			} else {
				turnLabel.setText("Turn: White");
			}

			// Kiểm tra nếu không còn nước đi hợp lệ cho cả hai người chơi
			List<int[]> blackMoves = board.getValidMoves(1);
			List<int[]> whiteMoves = board.getValidMoves(-1);

			// Nếu cả hai người chơi không có nước đi hợp lệ, kết thúc trò chơi
			if (blackMoves.isEmpty() || whiteMoves.isEmpty()) {
				String result = board.checkWinner();
				JOptionPane.showMessageDialog(this, result);
				return;
			}

			// Kiểm tra trò chơi đã kết thúc chưa
			if (board.isGameOver()) {
				String result = board.checkWinner();
				JOptionPane.showMessageDialog(this, result); // Hiển thị kết quả
				return; // Kết thúc nếu trò chơi đã kết thúc
			}

			// Nếu đang chơi với AI và lượt của AI (quân trắng)
			if (playingWithAI && board.getCurrentPlayer() == -1) {
				// Tạo một luồng mới để thêm delay trước khi gọi aiMove()
				new Thread(() -> {
					try {
						Thread.sleep(1000); // Delay 3 giây
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Gọi phương thức aiMove() trên luồng giao diện
					SwingUtilities.invokeLater(this::aiMove);
				}).start();
			}

		} else {
			System.out.println("Invalid move!"); // In ra thông báo nếu người chơi chọn nước đi không hợp lệ
		}
	}

	public void aiMove() {
		int[] aiMove = bot.getBestMove();
		if (aiMove != null) {
			 Tile aiTile = buttonGrid[aiMove[0]][aiMove[1]];
		        aiTile.setBack(Color.YELLOW);

		        // Sử dụng Timer để đổi lại màu sau 2 giây
		        javax.swing.Timer timer = new javax.swing.Timer(2000, new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		                aiTile.setBack(new Color(45, 174, 82)); // Trả lại màu nền ban đầu
		            }
		        });
		        timer.setRepeats(false); 
		        timer.start(); 

			board.setState(aiMove[0], aiMove[1], -1); // AI là người chơi thứ hai (quân trắng)

			repaintBoard(); // Cập nhật lại giao diện

			// Cập nhật số lượng quân
			blackCountLabel.setText("Black: " + board.countTileInBoard(1));
			whiteCountLabel.setText("White: " + board.countTileInBoard(-1));

			// Cập nhật lượt chơi sau khi AI thực hiện nước đi
			// Chắc chắn rằng sau khi AI thực hiện xong, chuyển lượt cho người chơi
			if (board.getCurrentPlayer() == 1) {
				turnLabel.setText("Turn: Black");
			} else {
				turnLabel.setText("Turn: White");
			}

			// Kiểm tra nếu không còn nước đi hợp lệ cho cả hai người chơi

			List<int[]> blackMoves = board.getValidMoves(1); // Nước đi hợp lệ của quân đen
			List<int[]> whiteMoves = board.getValidMoves(-1); // Nước đi hợp lệ của quân trắng

			// In ra số lượng nước đi hợp lệ của quân đen
			System.out.println("Black's valid moves: " + blackMoves.size());

			// In ra số lượng nước đi hợp lệ của quân trắng
			System.out.println("White's valid moves: " + whiteMoves.size());

			// Nếu cả hai người chơi không có nước đi hợp lệ, kết thúc trò chơi
			if (blackMoves.isEmpty() || whiteMoves.isEmpty()) {
				String result = board.checkWinner();
				JOptionPane.showMessageDialog(this, result);
				return;
			}

			// Kiểm tra xem trò chơi đã kết thúc chưa
			if (board.isGameOver()) {
				String result = board.checkWinner();
				JOptionPane.showMessageDialog(this, result); // Hiển thị kết quả
				return; // Kết thúc nếu trò chơi đã kết thúc
			}
		}
	}

	// Load lại bàn cờ
	public void repaintBoard() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Tile t = buttonGrid[i][j];
				t.setState(board.getState(i, j));
				t.setHighlighted(board.isMoveValid(i, j, board.getCurrentPlayer()));
			}
		}
	}
}

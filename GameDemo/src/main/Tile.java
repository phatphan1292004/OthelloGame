package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Tile extends JButton {
    private int row;
    private int col;
    private GUI gui;
    private int state; // 1 black, -1 white, 0 empty
    private Color bg = new Color(45, 174, 82);
	private Board board;

    // Constructor for Tile
    public Tile(GUI gui, int row, int col) {
        this.gui = gui;
        this.row = row;
        this.col = col;
        this.setBackground(bg);  // Default background color
        super.setBorder(new LineBorder(Color.BLACK));
        this.setPreferredSize(new Dimension(40, 40));
        this.addActionListener(e -> gui.onClick(row, col));
    }
    
    public void setBack(Color color) {
    	this.setBackground(color);
    	this.repaint();
    }
    
    public Tile(int row, int col, int state) {
        this.row = row;
        this.col = col;
        this.state = state;
    }

    public Tile(int row, int col) {
    	this.row = row;
    	this.col = col;
    }
    
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getState() {
        return state;
    }
    

    public void setState(int state) {
    	this.state = state;
        if (state == 1) {
            // Đặt biểu tượng quân cờ đen
            super.setIcon(new ImageIcon("src/blackPiece.gif"));
        } else if (state == -1) {
            // Đặt biểu tượng quân cờ trắng
            super.setIcon(new ImageIcon("src/whitePiece.png"));
        }else {
        	super.setIcon(null);
        }
        this.repaint();
    }
    
    
    
    public void setHighlighted(boolean mode) {
        if (mode) {
            super.setBorder(new LineBorder(Color.WHITE));
        } else {
        	super.setBorder(new LineBorder(Color.BLACK));
        }
    }
}

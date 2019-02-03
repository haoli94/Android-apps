// Board.java
package edu.stanford.cs108.tetris;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	
	private int[] widths;
	private int[] heights;
	
	// These are for undo operation
	private int[] undoWidths;
	private int[] undoHeights;
	private boolean[][] undoGrid;
	
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		for (int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				grid[i][j] = false;
			}
		}
		
		widths = new int[height];
		heights = new int[width];
		
		undoWidths = new int[height];
		undoHeights = new int[width];
		undoGrid = new boolean[width][height];
		
		// YOUR CODE HERE
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		int max = 0;
		for (int i=0;i<width;i++) {
			if (heights[i] > max) max = heights[i];
		}
		return max;
//		int max = -1;
//		for (int i=0;i<width;i++) {
//			for (int j=0;j<height;j++) {
//				if (grid[i][j]==true) {
//					if (j>max) max=j;
//				}
//			}
//		}
//		if (max<0) return 0;
//		return max+1; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
//
//
//			if (DEBUG) {
				int[] checkWidths = new int[this.height];
				int[] checkHeights = new int[this.width];
				int checkMaxHeight = 0;
				for (int i = 0; i < width;i++) {
					for (int j = 0; j < height; j++) {
						if (grid[i][j]) {
							checkWidths[j]++;
							checkHeights[i] = j + 1; // There can be some empty positions!
							checkMaxHeight = Math.max(checkMaxHeight, checkHeights[i]);
						}
					}
				}
				if (!Arrays.equals(checkHeights, heights)) {
					throw new RuntimeException("The heights array has error!");
				} else if (!Arrays.equals(checkWidths, widths)) {
					throw new RuntimeException("The widths array has error!");
				} else if (checkMaxHeight != getMaxHeight()) {
					throw new RuntimeException("maxHeight is wrong!");
				}
				// YOUR CODE HERE
			}

//
//			// YOUR CODE HERE
//			int MaxH = 0;
//			for (int x=0;x<width;x++) {
//				if (checkHeight(x) != (heights[x])) {
//					throw new RuntimeException("Check Heights Failed");
//				}
//			}
//			for (int y=0;y<height;y++) {
//				if (checkRowWidth(y) != (widths[y])) {
//					throw new RuntimeException("Check Widths Failed");
//				}
//			}
//
//
//			for (int x=0;x<width;x++) {
//				if (MaxH < heights[x]) {
//					MaxH = heights[x];
//				}
//			}
//			if (MaxH != checkMaxHeight()) {
//				throw new RuntimeException("Check getMaxHeight Failed");
//			}
//
//		}
	}
	
	private int checkHeight(int x) {
		int max = -1;
		for (int j=0;j<height;j++) {
			if (grid[x][j]==true) {
				if (j>max) max=j;
			}
		}
		if (max<0) return 0;
		return max+1;		
	}
	
	private int checkRowWidth(int y) {
		int filled = 0;
		for (int i=0;i<width;i++) {
			if (grid[i][y]==true) filled++;
		}
		return filled; 
	}
	
	private int checkMaxHeight() {
		int max = -1;
		for (int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				if (grid[i][j]==true) {
					if (j>max) max=j;
				}
			}
		}
		if (max<0) return 0;
		return max+1;
	}
	
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int skirtLen = skirt.length;
		int landedY = 0;
		for (int i = 0;i<skirtLen;i++) {
			int Y = getColumnHeight(x+i)-skirt[i];
			if (Y > landedY) landedY = Y;
		}
		return landedY; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
//		int max = -1;
//		for (int j=0;j<height;j++) {
//			if (grid[x][j]==true) {
//				if (j>max) max=j;
//			}
//		}
//		if (max<0) return 0;
//		return max+1; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return widths[y];
//		int filled = 0;
//		for (int i=0;i<width;i++) {
//			if (grid[i][y]==true) filled++;
//		}
//		return filled; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if ((x<0)||(x>width)) {
			return true;
		}else if((y<0)||(y>height)) {
			return true;
		}
		return grid[x][y]; // YOUR CODE HERE
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		
		int result = PLACE_OK;
		commit();
		committed = false;
		for (TPoint point : piece.getBody()) {
			int deltaX = point.x;
			int deltaY = point.y;
			if (x+deltaX < 0||x+deltaX>width-1||y+deltaY<0|y+deltaY>height-1) {
				return PLACE_OUT_BOUNDS;
			}
			else if (grid[x+deltaX][y+deltaY] == true) {
				return PLACE_BAD;
			}
		}
		for (TPoint point : piece.getBody()) {
			int deltaX = point.x;
			int deltaY = point.y;
			grid[x+deltaX][y+deltaY] = true;
			widths[y+deltaY] += 1;
			if (heights[x+deltaX] <= y+deltaY+1) {
				heights[x+deltaX] = y+deltaY+1;
			}
			boolean Filled = true;
			for (int j = 0; j < width; j++) {
				if (grid[j][y+deltaY]==false) {
					Filled = false;
				}
			}
			if (Filled == true) {
				result = PLACE_ROW_FILLED;
			}
		}
		sanityCheck();
		// YOUR CODE HERE
//		System.out.println(result);
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		int y = 0;
		int maxHeight = getMaxHeight();
//		System.out.println(maxHeight);

		while (y<maxHeight) {
			boolean remove = true;
			if (getRowWidth(y) != width) {
				remove = false;
			}
//			for (int x = 0; x < width; x++) {
//				if(grid[x][y]!=true) {
//					remove = false;
//				}
//			}
			if (remove==true) {
				for (int removeY = y;removeY < maxHeight-1;removeY++) {
					for (int i = 0; i < width; i++) {
						grid[i][removeY] = grid[i][removeY+1];
					}
					widths[removeY] = widths[removeY+1];
				}
				widths[maxHeight-1] = 0;
				rowsCleared++;
				for (int i = 0; i < width; i++) {
					grid[i][maxHeight-1] = false;
					heights[i]--;
				}
				maxHeight = getMaxHeight();
//				System.out.println(maxHeight);
				continue;
			}
		y++;
		}
		// YOUR CODE HERE
		sanityCheck();
		committed = false;
		return rowsCleared;
	}
//here we use getMaxHeight to keep track the height we need to clear


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	
	public void undo() {
		if(!committed) {
			
			System.arraycopy(undoWidths, 0, widths, 0, widths.length);
			System.arraycopy(undoHeights, 0, heights, 0, heights.length);
			for (int i = 0; i < width; i++) {
				System.arraycopy(undoGrid[i], 0, grid[i], 0,undoGrid[i].length);
			}
		}
		commit();
		// YOUR CODE HERE
	}
	
	
	
	private void backUp() {
		System.arraycopy(widths, 0, undoWidths, 0, widths.length);
		System.arraycopy(heights, 0, undoHeights, 0, heights.length);
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, undoGrid[i], 0, grid[i].length);
		}
	}

	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
		backUp();
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}



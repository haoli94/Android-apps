// Board.java
package edu.stanford.cs108.tetris;

import java.util.ArrayList;
import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board {
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	
	private int[] widths;// This array stores the number of filled blocks in each row.
	private int[] heights;// This array stores the number of filled blocks in each column.
	private int maxHeight;// This data attribute stores the current max height.


	// These are for undo operation
	private int[] undoWidths;
	private int[] undoHeights;
	private boolean[][] undoGrid;
	private int undoMaxHeight;
	
	
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
		//Initialize the quick palying data attributes
		widths = new int[height];
		heights = new int[width];
		maxHeight = 0;
		//Initialize the undo data
		undoWidths = new int[height];
		undoHeights = new int[width];
		undoGrid = new boolean[width][height];
		
		// YOUR CODE HERE
	}


	public Board() {

		this(8,20);
	}
	//This is a default constructor
	
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
		return maxHeight;
		//Quickly return the stored max height info
	}
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
//	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			//Initialize the sanityCheck data attributes
			int[] sanityWidths = new int[height];
			int[] sanityHeights = new int[width];
			int sanityMaxHeight = 0;

			for (int i = 0; i < width;i++) {
				for (int j = 0; j < height; j++) {
					if (grid[i][j]) {
						//Compute widths in each row, heights in each column and the max height through iteration
						sanityWidths[j]++;
						sanityHeights[i] = j + 1; // Some of the spots may not be filled
						if (sanityHeights[i] > sanityMaxHeight){
							sanityMaxHeight = sanityHeights[i];
						}
					}
				}
			}

			if (Arrays.equals(sanityWidths, widths) == false) {
				throw new RuntimeException("The widths sanityCheck failed!");
			}//SanityCheck for the consistency of widths

			if (Arrays.equals(sanityHeights, heights)== false) {
				throw new RuntimeException("The heights sanityCheck failed!");
			}//SanityCheck for the consistency of heights

			if (sanityMaxHeight != maxHeight) {
				throw new RuntimeException("The maxHeight sanityCheck failed!");
			}//SanityCheck for the consistency of the maxHeight

			// YOUR CODE HERE
		}
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
		// The landed position would be the highest one which means largest Y
		return landedY; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
		//Quickly return the height of a specific column
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
		//Quickly return the width of a specific row
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
		if ((x < 0) || (x > width)) {
			return true;
		} else if ((y < 0) || (y > height)) {
			return true;
		} else {
			return grid[x][y]; // YOUR CODE HERE
		}
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
		committed = false;
		for (TPoint point : piece.getBody()) {
			int deltaX = point.x;
			int deltaY = point.y;
			if (x+deltaX < 0||x+deltaX>width-1||y+deltaY<0|y+deltaY>height-1) {
				return PLACE_OUT_BOUNDS;
			}
			//Check if PLACE_OUT_BOUNDS
			else if (grid[x+deltaX][y+deltaY] == true) {
				return PLACE_BAD;
			}
			//Check if PLACE_BAD, already occupied
		}
		for (TPoint point : piece.getBody()) {
			int deltaX = point.x;
			int deltaY = point.y;
			grid[x+deltaX][y+deltaY] = true;
			//Update the location of a piece point on the board
			widths[y+deltaY] += 1;
			//Update the widths info
			if (heights[x+deltaX] <= y+deltaY+1) {
				heights[x+deltaX] = y+deltaY+1;
				//Update the heights info
				//Some of the positions on the board may not be filled
				maxHeight = Math.max(maxHeight,heights[x+deltaX]);
				//Update the max height info
			}


			if (widths[y+deltaY] == width){
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
        committed = false;
		ArrayList<Integer> clearList = new ArrayList<>();
		//Initialize the list to store the rows that we are going to clear
		for (int y = 0; y < getMaxHeight(); y++) {
			if (getRowWidth(y) == width) {
				clearList.add(y);
			}
		}
		rowsCleared = clearList.size();
		
		//The number of rows that we are going to clear
		int start = 0;
		for (int r = 0; r < getMaxHeight(); r++) {
			if (!clearList.contains(r)) {
				for (int i = 0; i < width; i++) {
					grid[i][start] = grid[i][r];
				}
				widths[start] = widths[r];
				//Change the board widths info of the rows shifted down
				start++;
			}
		}
		//Clear the rows that are in the list by shift the rows above down
		
		for(int j = start; j < getMaxHeight(); j++) {
			for(int i = 0; i < width; i++) {
				grid[i][j] = false;
			}
			widths[j] = 0;
			// Reset their widths
		}
		//Fill the rows above the last row after clearing with false

		maxHeight = 0;

		for(int i = 0; i < width; i++) {
			boolean zero = true;
			for(int j = heights[i]-1; j >= 0; j--) {
				if(grid[i][j]) {
					heights[i] = j + 1;
					//Recalculate the height of specific column
					maxHeight = Math.max(maxHeight,heights[i]);
					//Recalculate the max height
					zero = false;
					break;
				}
			}
			
			//If the zero flag is true, this column is 0 high
			if (zero){
				heights[i] = 0;
			}
		}
		sanityCheck();
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
			//Undo the widths
			System.arraycopy(undoHeights, 0, heights, 0, heights.length);
			//Undo the heights
			for (int i = 0; i < width; i++) {
				System.arraycopy(undoGrid[i], 0, grid[i], 0,undoGrid[i].length);
			}
			//Undo the heights
			maxHeight = undoMaxHeight;
			//Undo the max height
		}
		//if committed then we can not undo and do nothing
		commit();
		// YOUR CODE HERE
	}
	
	
	
	private void backUp() {
		System.arraycopy(widths, 0, undoWidths, 0, widths.length);
		//Backup the widths
		System.arraycopy(heights, 0, undoHeights, 0, heights.length);
		//Backup the heights
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, undoGrid[i], 0, grid[i].length);
		}
		//Backup the grid
		undoMaxHeight = maxHeight;
		//Backup the max height
	}

	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
		backUp();
		//back up the current board info at the meanwhile
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



/*
* RSA5330.java
*
* Version:
* $Id: RSA5330.java,v 1.3 2014/03/12 03:16:46 rsa5330 Exp $
*
* Revisions:
* $Log: RSA5330.java,v $
* Revision 1.3  2014/03/12 03:16:46  rsa5330
* Finished with Project 2 Part1
*
* Revision 1.2  2014/03/11 23:29:13  rsa5330
* Now working on the display.
*
* Revision 1.1  2014/03/07 14:18:15  rsa5330
* Uploaded to CVS
*
*/
package Players.hash420;

import Engine.Logger;
import Interface.GobbletPart1;
import Interface.PlayerModule;
import Interface.PlayerMove;
import java.util.*;
import Interface.Coordinate;

/**
 * Gobblet
 * @author Ranbir Aulakh
 * @author Kemoy Campbell
 *
 */

public class hash420 implements PlayerModule, GobbletPart1 {
	
	private int playerID;
	private Logger log;
	private PlayerMove lastMove;
	private final int ROW = 4, COL = 4;
	private Stack<Piece>[][] board = new Stack[ROW][COL];
	private Stack<Piece>[] player1 = new Stack[3];//my partner's structure of how he keep player1's stack 
	private Stack<Piece>[] player2 = new Stack[3];
	
	
	/**
	 * Display, on standard output, a representation of how the physical game 
	 * would appear at this point in time. The format is as follows. On the 
	 * left-hand side is the board, row 0 at the top, column 0 on the left. 
	 * On the right-hand side is two rows of information about the players' 
	 * stacks. Player 1's stacks are next to the first row of the board and 
	 * player 2's are next to the third row. For both players, their #1 stacks 
	 * are to the left. Gobblets' sizes are numbered 1-4, owner numbers are 
	 * indicated in parentheses, and empty positions are indicated with brackets. 
	 * Here is an example. Player 1 has placed its first size-4 gobblet in (0,0). 
	 * Player 2 responded in kind but to position (1,1). Finally, Player 1 puts 
	 * the next gobblet from the same stack at position (2,2). This is what should
	 *  appear on standard output (System.out):
	 */
	@Override
	public void dumpGameState()
	{
		String[] printOut = new String[4];
		
		int chumpVariable;
		for(int i = 0; i < 4; i++)
		{
			printOut[i] = "";//don't want to print 'null'
			for(int x=0; x<4; x++)
			{
				chumpVariable = getTopOwnerOnBoard(i,x);//if this equals one there is a player
				printOut[i] += (chumpVariable==-1) ?
						"    []"
						: "  " +getTopSizeOnBoard(i,x) + "(" + chumpVariable + ")";
				
			}
		}
		
		for(int i = 0; i < 4; i+=2)//only first and 3rd
		{
			for(int x = 1; x < 4; x++)
			{
				chumpVariable = (i==0) ? getTopSizeOnStack(1, x) : 
					getTopSizeOnStack(2, x);
				printOut[i] += (chumpVariable == -1) ? 
						"  _" :
						"  " + chumpVariable;
			}
		}
		
		//print
		for(int i = 0; i < printOut.length; i++)
			System.out.println(printOut[i]);
	}

	/**
	 * Which player is this?
	 * @returns the numeric id of this module's player
	 */
	@Override
	public int getID() {
		return this.playerID;
	}
	
	/**
	 * Describe what is visible on the board at a given location.
	 * @param ROW - the row of interest on the board (0-based)
	 * @param col - the column of interest on the board (0-based)
	 * @return the ID of the player (1-2) that owns the piece on 
	 * top of the stack of pieces at the given location on the board.
	 * If the stack is empty, return -1.
	 */
	@Override
	public int getTopOwnerOnBoard(int arg0, int arg1) {
		/**
		 * check if the board is empty?
		 * 		return -1
		 * return board[][] peek?
		 */
		if(board[arg0][arg1].empty())
		{
			return -1;
		}
		return board[arg0][arg1].peek().getOwnerID(); 
	}
		
	/**
	 * Describe what is visible on the board at a given location.
	 * @param ROW - the row of interest on the board (0-based)
	 * @param col - the column of interest on the board (0-based)
	 * @return the size of the piece on top of the stack of pieces at 
	 * the given location on the board. If the stack is empty, -1.
	 */
	@Override
	public int getTopSizeOnBoard(int arg0, int arg1) {
		/*
		 * if the board(stack) is empty?
		 * 		throws an error
		 * then return board(stack).peek()?
		 */	
		if(board[arg0][arg1].empty())
		{//let us see then
			return -1;
		}		
		return board[arg0][arg1].peek().getSize();
	}
	
	/**
	 * Describe what remains on top of one of the stacks of 
	 * unplayed pieces of a certain player.
	 * @param playerID - the player ID (1-2)
	 * @param stackNum - the number of one of the player's unplayed 
	 * pieces stacks (1-based)
	 * @return the size of the piece on top of the identified stack of 
	 * unplayed pieces. If the identified stack is empty, -1.
	 */
	@Override
	public int getTopSizeOnStack(int arg0, int arg1) {
		/*
		 * if the board(stack) is empty
		 * 		throws an error
		 * then return player1.peek and player2.peek?
		 */
		int unplayedPieces = 0;
		
		if(arg0 == 1){
			if(player1[arg1 - 1].empty()){
				return -1;
			}
			unplayedPieces = player1[arg1 - 1].peek().getSize();
		}
		else if(arg0 == 2){
			if(player2[arg1 - 1].empty()){
				return -1;
			}
			unplayedPieces = player2[arg1 - 1].peek().getSize();
		}
		return unplayedPieces;
	}
	
	/**
	 * Initializes the player module.
	 * param logger - reference to the logger class
	 * @param playerId - the id of this player (1 or 2) This method must be implemented for all parts of the project.
	 */
	@Override
	public void init(Logger arg0, int arg1) {		
		this.log = arg0;
		this.playerID = arg1;
		
		for (int i = 0; i < ROW; i++){
		    for (int j = 0; j < COL; j++){
		        board[i][j] = new Stack<Piece>();
		        //System.out.print(board[i][j]);
		    }
		    //System.out.println();
		}
		
		for (int x = 0; x < 3; x++){
			player1[x] = new Stack<Piece>();
			player2[x] = new Stack<Piece>();
			for(int y = 1; y <= 4; y++){
				/*
				 * make each stack once
				 */				
				
				Piece size = new Piece(y, 1);
				Piece size1 = new Piece(y, 2);
				
				player1[x].push(size);
				player2[x].push(size1);
			}
		}
	}
	
	/**
	 * Notifies the player of a valid move that was just made. This is 
	 * called when any player (including this one) has made a move 
	 * in the game. The move has already been validated.
	 * @param move - the move This method must be implemented for all parts of the project.
	 */
	@Override
	public void lastMove(PlayerMove arg0) {
		this.lastMove = arg0;
		int pop = 0;
		Piece popFromboard;
		
		//System.out.println("THE STACK: " + lastMove.getStack());
		if(lastMove.getPlayerId() == 1){
			if(lastMove.getStartRow() == -1 && lastMove.getStartCol() == -1){
				pop =  player1[lastMove.getStack() - 1].pop().getSize();
				board[lastMove.getEndRow()][lastMove.getEndCol()].push(new Piece(pop, lastMove.getPlayerId()));
			}
			else{
				popFromboard = board[lastMove.getStartRow()][lastMove.getStartCol()].pop();
				board[lastMove.getEndRow()][lastMove.getEndCol()].push(new Piece(popFromboard.getSize(), popFromboard.getOwnerID()));				
			}
		}
		if(lastMove.getPlayerId() == 2){
			if(lastMove.getStartRow() == -1 && lastMove.getStartCol() == -1){
				pop =  player2[lastMove.getStack() - 1].pop().getSize();
				board[lastMove.getEndRow()][lastMove.getEndCol()].push(new Piece(pop, lastMove.getPlayerId()));
			}
			else{
				popFromboard = board[lastMove.getStartRow()][lastMove.getStartCol()].pop();
				board[lastMove.getEndRow()][lastMove.getEndCol()].push(new Piece(popFromboard.getSize(), popFromboard.getOwnerID()));				
			}
		}
	}
	
	/**
	 * Called when it's this player's turn to make a move. The player module 
	 * should choose a move, construct a valid PlayerMove object that represents
	 * it, and return it. This player module's "state" should not be updated here.
	 * This module's lastMove() function will get called after the engine verifies 
	 * the move. This way the update of state always occurs in the same place.
	 * @param an object representing this player module's move This method can be 
	 * stubbed out for part 1. (Suggestion: "throw new UnsupportedOperationException()")
	 */
	@Override
	public PlayerMove move() 
	{

		PlayerMove move = null;
		int stack = 0;
		int piece = 0;
		Coordinate start = new Coordinate(-1,-1);
		Coordinate end = new Coordinate(-1,-1);
		boolean moved = false;

		/*---------------------------
		 * SIMPLE AI..Nothing fancy
		 * can still loose easily
		 * but decided to implement
		 * this since the hash420 is
		 * too dumb without it
		 *-------------------------*/
		//selecting the correct player's stack
		if(playerID==1)
		{
			//adding the piece to the board from the stack
			if(moved==false)
			{
				for(int i=1; i<(player1.length +1); i++)
				{
					//if the stack is not empty take that stack
					if( !( player1[i-1].isEmpty() ) )
					{

						piece= this.getTopSizeOnStack(playerID, i);
						stack = i;
						break;
					}
					else
					{
						if(i==player1.length+1)
						{
							stack = 0;
						}
					}

				}

				/*---------------------------
				 * SIMPLE AI..Nothing fancy
				 * can still loose easily
				 * but decided to implement
				 * this since the hash420 is
				 * too dumb without it
				 *-------------------------*/
				Coordinate temp = new Coordinate(-1,-1);
				//is there any rows in the horizional threatning?
				for(int col = 0; col<board.length && !moved; col++)
				{
					int empty=0;
					int threat = 0;

					for(int row = 0; row<board.length; row++)
					{
						if(this.getTopOwnerOnBoard(col, row)!=1 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							threat++;
						}

						//I am on this row as well. No worries :-)
						else if(this.getTopOwnerOnBoard(col, row)==1)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							end = temp;
							moved = true;

						}
					}

				}
				//is there any threating in the rows vertical?
				for(int row = 0; row<board.length && !moved; row++)
				{
					int empty=0;
					int threat = 0;

					for(int col = 0; col<board.length; col++)
					{
						if(this.getTopOwnerOnBoard(col, row)!=1 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==1)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							end = temp;
							moved = true;
						}
					}

				}

				//any threating in the diagonal 1
				//is there any threating in the rows vertical?
				if(!moved)
				{
					int col = 0, row = 3;
					int empty=0, threat = 0;
					while(row>=0 && col<board.length)
					{
						if(this.getTopOwnerOnBoard(col, row)!=1 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							System.out.println("Threat at : " + col + " " + row);
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==1)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);

							System.out.println (" We arrived at this empty location : " + temp);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							System.out.println("We arrive here you moron come on");
							System.out.println("Threat : " + threat + " Empty: " +empty + " Coordinate : " + temp );
							end = temp;
							moved = true;
						}
						col++; row--;
					}
				}

				//is there any threat in the diagonal 2?
				if(!moved)
				{
					int col = 0, row = 0;
					int empty=0, threat = 0;
					while(row>board.length && col<board.length)
					{
						if(this.getTopOwnerOnBoard(col, row)!=1 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							System.out.println("Threat at : " + col + " " + row);
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==1)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);

							System.out.println (" We arrived at this empty location : " + temp);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							System.out.println("We arrive here you moron come on");
							System.out.println("Threat : " + threat + " Empty: " +empty + " Coordinate : " + temp );
							end = temp;
							moved = true;
						}
						col++; row++;
					}
				}


				//END OF SIMPLE AI
				//how about a boolean condition 
				for(int col = 0; col<board.length && !moved;  col++)
				{
					for(int row=0; row<board.length; row++)
					{
						//an empty row on the board is found
						if(this.getTopSizeOnBoard(col, row)==-1)
						{
							start = new Coordinate(-1,-1);
							end =  new Coordinate(col, row);
							moved= true;
							break;
						}
					}
				}
			}


			//come up with moves that will make the board full

			/*----------------------------------
			 * CURRENT BOARD STATE              |
			 *                                  |
			 *---------------------------------*/
			//can i current use any of my pieces on the board  to gobble the opponent's piece?
			//so i would have a boolean to stop the other loops? 
			if(moved==false)
			{
				for(int col=0; col<board.length && !moved; col++)
				{
					for(int row = 0; row<board.length &&!moved; row++)
					{
						int owner = this.getTopOwnerOnBoard(row, col);
						if(owner==1)
						{
							int myPiece = this.getTopSizeOnBoard(row, col);
							//compare it against everything on the board

							for(int boardCol = 0; boardCol<board.length && !moved; boardCol++)
							{
								for(int boardRow = 0; boardRow<board.length; boardRow++)
								{
									//this piece is the opponent's piece
									if(this.getTopOwnerOnBoard(boardRow, boardCol)!=1 && this.getTopOwnerOnBoard(boardRow, boardCol)!=-1 )
									{
										int opponentPiece = this.getTopSizeOnBoard(boardRow, boardCol);
										//is my piece greater than the opponet?
										if(myPiece>opponentPiece)
										{
											System.out.println(boardRow +" "+ boardCol);
											stack = 0;

											piece= myPiece;
											start = new Coordinate(row, col);
											end = new Coordinate(boardRow, boardCol);
											move = new PlayerMove(playerID, stack, piece, start, end);
											moved =  true;
											break;
										}
									}

								}
							}
						}
					}
				}
			}

		}//end of if player 1

		//player 2
		//selecting the correct player's stack
		if(playerID==2)
		{
			System.out.println("PLAYER 2 HERE!");
			//adding the piece to the board from the stack
			if(moved==false)
			{
				for(int i=1; i<(player2.length +1); i++)
				{
					//if the stack is not empty take that stack
					if( !( player2[i-1].isEmpty() ) )
					{

						piece= this.getTopSizeOnStack(playerID, i);
						stack = i;
						break;
					}
					else
					{
						if(i==player2.length+1)
						{
							stack = 0;
						}
					}

				}

				/*---------------------------
				 * SIMPLE AI..Nothing fancy
				 * can still loose easily
				 * but decided to implement
				 * this since the hash420 is
				 * too dumb without it
				 *-------------------------*/
				Coordinate temp = new Coordinate(-1,-1);
				//is there any rows in the horizional threatning?
				for(int col = 0; col<board.length && !moved; col++)
				{
					int empty=0;
					int threat = 0;

					for(int row = 0; row<board.length; row++)
					{
						if(this.getTopOwnerOnBoard(col, row)!=2 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							threat++;
						}

						//I am on this row as well. No worries :-)
						else if(this.getTopOwnerOnBoard(col, row)==2)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							end = temp;
							moved = true;

						}
					}

				}
				//is there any threating in the rows vertical?
				for(int row = 0; row<board.length && !moved; row++)
				{
					int empty=0;
					int threat = 0;

					for(int col = 0; col<board.length; col++)
					{
						if(this.getTopOwnerOnBoard(col, row)!=2 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==2)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							end = temp;
							moved = true;
						}
					}

				}

				//any threating in the diagonal 1
				//is there any threating in the rows vertical?
				if(!moved)
				{
					int col = 0, row = 3;
					int empty=0, threat = 0;
					while(row>=0 && col<board.length)
					{
						if(this.getTopOwnerOnBoard(col, row)!=2 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							System.out.println("Threat at : " + col + " " + row);
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==2)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);

							System.out.println (" We arrived at this empty location : " + temp);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							System.out.println("We arrive here you moron come on");
							System.out.println("Threat : " + threat + " Empty: " +empty + " Coordinate : " + temp );
							end = temp;
							moved = true;
						}
						col++; row--;
					}
				}

				//is there any threat in the diagonal 2?
				if(!moved)
				{
					int col = 0, row = 0;
					int empty=0, threat = 0;
					while(row>board.length && col<board.length)
					{
						if(this.getTopOwnerOnBoard(col, row)!=2 && this.getTopOwnerOnBoard(col, row)!=-1)
						{
							System.out.println("Threat at : " + col + " " + row);
							threat++;
						}

						//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
						else if(this.getTopOwnerOnBoard(col, row)==2)
						{
							break;//come out of the loop and check the next horizional row
						}

						else
						{

							empty++;
							temp = new Coordinate(col, row);

							System.out.println (" We arrived at this empty location : " + temp);
						}

						//get the location of the next empty space to defend myself
						if(threat==3 && empty==1)
						{
							System.out.println("We arrive here you moron come on");
							System.out.println("Threat : " + threat + " Empty: " +empty + " Coordinate : " + temp );
							end = temp;
							moved = true;
						}
						col++; row++;
					}
				}


				//END OF SIMPLE AI for player 1
				//how about a boolean condition 
				for(int col = 0; col<board.length && !moved;  col++)
				{
					for(int row=0; row<board.length; row++)
					{
						//an empty row on the board is found
						if(this.getTopSizeOnBoard(col, row)==-1)
						{
							start = new Coordinate(-1,-1);
							end =  new Coordinate(col, row);
							moved= true;
							break;
						}
					}
				}
			}


			//come up with moves that will make the board full

			/*----------------------------------
			 * CURRENT BOARD STATE              |
			 *                                  |
			 *---------------------------------*/
			//can i current use any of my pieces on the board  to gobble the opponent's piece?
			//so i would have a boolean to stop the other loops? 
			if(moved==false)
			{
				for(int col=0; col<board.length && !moved; col++)
				{
					for(int row = 0; row<board.length &&!moved; row++)
					{
						int owner = this.getTopOwnerOnBoard(row, col);
						if(owner==2)
						{
							int myPiece = this.getTopSizeOnBoard(row, col);
							//compare it against everything on the board

							for(int boardCol = 0; boardCol<board.length && !moved; boardCol++)
							{
								for(int boardRow = 0; boardRow<board.length; boardRow++)
								{
									//this piece is the opponent's piece
									if(this.getTopOwnerOnBoard(boardRow, boardCol)!=2 && this.getTopOwnerOnBoard(boardRow, boardCol)!=-1 )
									{
										int opponentPiece = this.getTopSizeOnBoard(boardRow, boardCol);
										//is my piece greater than the opponet?
										if(myPiece>opponentPiece)
										{
											System.out.println(boardRow +" "+ boardCol);
											stack = 0;

											piece= myPiece;
											start = new Coordinate(row, col);
											end = new Coordinate(boardRow, boardCol);
											move = new PlayerMove(playerID, stack, piece, start, end);
											moved =  true;
											break;
										}
									}

								}
							}
						}
					}
				}
			}


			//for some reason it seems as if my code wont exist the loop when I break it

		}//end of if player 1
		if(moved)
		{
			move = new PlayerMove(playerID, stack, piece, start, end);
		}


		return move;
	}
	
	/**
	 * Notifies the player that another player has been invalidated. In a 2 player game, 
	 * this means the other player is now out of the game and this one just has to play 
	 * the game alone to completion to win.
	 * @param * @param an object representing this player module's move This method can be 
	 * stubbed out for part 1. (Suggestion: "throw new UnsupportedOperationException()")
	 */
	@Override
	public void playerInvalidated(int arg0) {
		System.out.println("Invalid move PLAYER " + arg0+ " HAS BEEN KICKED OUT");
		
	}

}

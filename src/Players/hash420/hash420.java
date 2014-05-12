package Players.hash420;

/*
 * hash420.java
 *
 * Version:
 * $Id:
 *
 * Revisions:
 * $Log:
 *
*/

import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;
import Engine.Logger;
import Interface.Coordinate;
import Interface.GobbletPart1;
import Interface.PlayerModule;
import Interface.PlayerMove;


/**
 * Gobblet 4x4 Board
 * @author Kemoy Campbell
 * @author Ranbir Aulakh
 */

//sometime 
public class hash420 implements PlayerModule, GobbletPart1 
{
	private int playerId;
	
	private Logger logger;

	//USE CONSTANTS FOR THE BOARD IMPLEMENT
	private final int ROW = 4, COL = 4;

	//BOARD STACK
	private Stack<Piece> board[][] = new Stack[ROW][COL];//can I see if i can help


	//PLAYER STACKS
	private Stack<Integer>[] player1Stack = new Stack[3];
	private Stack<Integer>[] player2Stack = new Stack[3];
	private Stack<Integer>[] generalStack = new Stack[3];

	//THREAT LOCATION MEMORY TRACKER
	private ArrayList<Gobblet> playerThreat = new ArrayList<Gobblet>();

	//MoveArray is used to store moves desired to perform by smartMove(opponentCoord)
	private ArrayList<Gobblet> moveArray;

	private ArrayList<Gobblet> twoInARowThreat;

	//win arrayList
	private ArrayList<Gobblet>winArray;


	//global variables that is used heavily inside of the move function especially in defending and offensive
	private final int emptyPos = -1;
	private Coordinate temp;
	private Coordinate end;
	private boolean moved;
	private boolean picked;
	private Coordinate start;
	private PlayerMove move;
	private int stack=0;
	private int piece;
	private int movesMade = 0;
	private Random rand = new Random();
	private boolean runSmartMove;

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
		return this.playerId;
	}

	/**
	 * Describe what is visible on the board at a given location.
	 * @param row - the row of interest on the board (0-based)
	 * @param col - the column of interest on the board (0-based)
	 * @return the ID of the player (1-2) that owns the piece on
	 * top of the stack of pieces at the given location on the board.
	 * If the stack is empty, return -1.
	 */
	public int getTopOwnerOnBoard(int row, int col) {
		if(board[row][col].isEmpty()){
			return -1;
		}
		else{
			return board[row][col].peek().getPlayerId();
		}
	}

	/**
	 * Describe what is visible on the board at a given location.
	 * @param ROW - the row of interest on the board (0-based)
	 * @param col - the column of interest on the board (0-based)
	 * @return the size of the piece on top of the stack of pieces at 
	 * the given location on the board. If the stack is empty, -1.
	 */
	public int getTopSizeOnBoard(int row, int col) {
		if(board[row][col].isEmpty()){
			return -1;
		}
		else{
			return board[row][col].peek().getPieceValue();
		}
	}
	
	/**
	 * Describe what remains on top of one of the stacks of unplayed pieces of a certain player.
	 * @param playerID - the player ID (1-2)
	 * @param stackNum - the number of one of the player's unplayed pieces stacks (1-based)
	 * @return the size of the piece on top of the identified stack of unplayed pieces.
	 * If the identified stack is empty, -1.
	 */
	public int getTopSizeOnStack(int playerID, int stackNum) 
	{
		int size=0;
		int stack = stackNum-1;
		//System.out.println("stack is: " +stackNum);
		if(playerID==1){
			if(player1Stack[stack].isEmpty()){
				return -1;
			}
			else{
				size= (int)player1Stack[stack].peek();
			}
		}

		if(playerID==2){
			if(player2Stack[stack].isEmpty()){
				return -1;
			}
			else{
				size= (int)player2Stack[stack].peek();
			}
		}
		return size;
	}

	/**
	 * Initializes the player module.
	 * param logger - reference to the logger class
	 * @param playerId - the id of this player (1 or 2) This method must be implemented for all parts of the project.
	 */
	public void init(Logger logger, int playerId) 
	{
		this.logger = logger;
		this.playerId = playerId;

		//initalize the board and create an empty stack at each location
		for(int row=0; row<ROW; row++){
			for(int col=0; col<COL; col++ ){
				board[row][col] = new Stack<Piece>();
				//board[row][col].push(new Piece(playerId,0));
			}
		}

		//SET UP THE PLAYERS WITH THEIR REPRESENTATION PIECES
		//Each players have three pieces of stack
		for(int i=0;i<3;i++){
			player1Stack[i] = new Stack<Integer>();
			player2Stack[i] = new Stack<Integer>();
		}

		for(int i=0;i<3;i++){
			for(int j=1;j<=4;j++){
				player1Stack[i].push(j);
				player2Stack[i].push(j);
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
	public void lastMove(PlayerMove move) {
		int offBoardCol = -1;//default position for offboard col
		int offBoardRow = -1;//default position for offboard row

		//getting the essential information from the move object. This variable is not required
		//However it make it easy to calls the variables instead of having to do move.something each time I am trying
		//to do some actions
		int id = move.getPlayerId();
		int stack = move.getStack();
		int startRow = move.getStartRow();
		int startCol = move.getStartCol();
		int endRow = move.getEndRow();
		int endCol = move.getEndCol();
		int stackPos = 0;
		Piece popPieceFromBoard;

		//if player id is 1
		if(id==1){	
			//the piece need to move is off the board
			if(startRow==offBoardRow && startCol==offBoardCol){
				//System.out.println("The stack here is: "+stack);
				stackPos = player1Stack[stack-1].pop();
				board[endRow][endCol].push(new Piece(id,stackPos));
			}
			else{
				//the piece is already on the board
				popPieceFromBoard = board[startRow][startCol].pop();
				//System.out.println("PIECE POPPED :"+popPieceFromBoard.getPieceValue());
				//System.out.println(stack - 1);
				board[endRow][endCol].push(new Piece(popPieceFromBoard.getPlayerId(),popPieceFromBoard.getPieceValue()));
			}
		}
		//if player id is 2
		if(id==2){	
			//the piece need to move is off the board
			if(startRow==offBoardRow && startCol==offBoardCol){

				stackPos = (int)player2Stack[stack-1].pop();
				board[endRow][endCol].push(new Piece(id,stackPos));
			}
			else{
				//the piece is already on the board
				popPieceFromBoard = board[startRow][startCol].pop();
				board[endRow][endCol].push(new Piece(popPieceFromBoard.getPlayerId(),popPieceFromBoard.getPieceValue()));
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
		//necessary variable and initializations
		move = null;
		stack = 0;
		piece = 0;
		start = new Coordinate(-1,-1);
		Coordinate opponentCoord = new Coordinate(-1,-1);
		Coordinate myCoord = new Coordinate(-1,-1);
		moved = false;
		temp = new Coordinate(-1,-1);
		end = new Coordinate(-1,-1);
		moveArray = new ArrayList<Gobblet>();
		generalStack = stackSelection(playerId);
		runSmartMove = false;
		int myEnemy = Opponent();
		picked = false;


		//Am i the first to go?
		isFirstToGo();
		/*-------------------------------------------------------------------------
		 * selecting a stack to move..starting with one since the stack is an 1 based
		 *----------------------------------------------------------------------------*/
		for(int i = 1;i<(generalStack.length +1) &&!moved; i++ )
		{
			//a stack is found
			if(!(generalStack[i-1].isEmpty()))
			{
				stack = i;
				piece = this.getTopSizeOnStack(playerId, stack);
				start = new Coordinate(-1,-1);
				break;
			}
		}//end of selecting the correct stack

		//updating the memory in the list to correct reflect the game
		updateThreatLocationList();

		/*--------------------------------
		 * WINNING STRATEGY FUNCTIONS
		 *---------------------------------*/
		horizontalWin(playerId,temp,opponentCoord);
		verticalWinning(playerId,temp,opponentCoord);
		diagonalWin(playerId,temp,opponentCoord);
		defendTwoInARowHorizVerticalByBlockingFirstCell();
		//twoInARowThreatDanger();

		/*--------------------------------
		 * DEFENDING STRATEGY FUNCTIONS
		 *---------------------------------*/
		defendHorizontal(playerId,temp,opponentCoord,  myCoord, myEnemy);
		defendVertical(playerId,temp,opponentCoord, myCoord, myEnemy);
		defendingDiagonal(playerId, temp,opponentCoord, myCoord,myEnemy);

		/*-----------------------------------------------------------------------------------------
		 * LOOK AT THE BOARD FOR:
		 *   ANY OF MY TWO PIECE IN  A ROW AND TWO EMPTY THEN ADD IT TO MOVE ARRAY LSIT
		 *   ANY OF MY TWO PIECE IN A ROW AND 1 OPPONENT PIECE THAT IS NOT 4 AND ADD IT TO THE LIST
		 *------------------------------------------------------------------------------------------*/
		smartMoves(opponentCoord);
		/*---------------------------------------------------
		 * RANDOMLY SELECT ONE OF THE MOVES THAT WAS CREATED
		 * USING SMARTMOVE(OPPONENTCOORD) FUNCTION
		 *----------------------------------------------------*/
		executeSmartMove();

		/*-----------------------------------------------------
		 * IF I HAVENT MADE ANY MOVES ABOVE AND THE TOTAL MOVES
		 * I HAVE MADE ON THE BOARD IS LESS THAN 3 THAN RANDOMLY
		 * FIND AN EMPTY SPOT AND MAKE THE MOVE
		 *-----------------------------------------------------*/
		if(!moved && movesMade<=2)
		{
			int randomCol = (int)(Math.random() *(board.length-1));
			int randomRow = (int)(Math.random() *(board.length-1));
			int randomStack =(int)(Math.random() *(generalStack.length-1)+1);

			//the spot on the board is not available
			while(this.getTopOwnerOnBoard(randomRow, randomCol)!=emptyPos)
			{
				randomCol = (int)(Math.random() *(board.length-1));
				randomRow = (int)(Math.random() *(board.length-1));
			}

			//such stack is not available
			while(this.getTopSizeOnStack(playerId, randomStack)==emptyPos)
			{
				randomStack =(int)(Math.random() *(generalStack.length-1)+1);
			}

			start = new Coordinate(-1,-1);
			end = new Coordinate(randomRow, randomCol);
			piece = this.getTopSizeOnStack(playerId, randomStack);
			stack = randomStack;
			moved = true;
		}

		/*------------------------------------------------------------
		 * THIS FUNCTION MAKE MOVES ONTO THE FIRST EMPTY SPOT OF THE
		 * BOARD IF NO MOVES HAS NOT OCCURS PREVIOUSLY AND THE RANDOM
		 * MOVE ABOVE CANNOT OCCURS BECAUSE MOVESMADE CONDITION IS NOT
		 * TRUE. THIS IS DONE WITHOUT GOBBLE THE OPPONENT
		 *--------------------------------------------------------------*/
		moveWithoutGobbleTheOpponent();

		/*-------------------------------------------
		 * USE A VALID PIECE AND GOBBLE THE OPPONENT
		 *-------------------------------------------*/
		gobbleOpponentFromBoard();

		/*-------------------------
		 * PEFORMS THE APPROPRIATE
		 * MOVE ONCE MOVED==TRUE
		 *-------------------------*/
		if(moved)
		{
			move = new PlayerMove(playerId, stack,piece, start, end);//the blue was my code playing itself using the AI i made sweet! look through 
			movesMade++;
		}

		return move;
	}

	/**
	 * This function takes a look at the current state of board for the following conditions:
	 *  I have two pieces in a row and there are two empties spot
	 *  I have two piece and one of my opponent is on the spot but that piece is not 4
	 *  
	 *  The conditions are then added inside an array List calls moveArray which will
	 *  create a list of best move objects.
	 *  This code only execute when both condition are true
	 *  @param OpponentCoord - takes the coordinate of the opponent's position default is (-1,-1)
	 */
	public void smartMoves(Coordinate opponentCoord)
	{
		Coordinate emptyCoord = new Coordinate(-1,-1);
		//check the horizontal for possible smart moves
		for(int row = 0; row<board.length; row++)
		{
			int threat = 0, mine = 0, empty1 = 0;
			for(int col = 0; col<board.length; col++)
			{
				//increment the opponent piece when found
				if(this.getTopOwnerOnBoard(row, col)!=playerId && this.getTopOwnerOnBoard(row, col)!=emptyPos)
				{
					threat++;
					//keep track of the last known enemy location
					opponentCoord = new Coordinate(row,col);
				}
				//increment my piece when found
				else if(this.getTopOwnerOnBoard(row, col)==playerId)
				{
					mine++;
				}
				//a empty location is found
				else if(this.getTopOwnerOnBoard(row, col)==emptyPos)
				{
					empty1++;
					//keep track of the last known empty spot
					emptyCoord = new Coordinate(row,col);
				}
				if(mine == 2 && empty1 == 2)
				{
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));	
				}
				else if(mine==2 && threat==1 && empty1==1)
				{
					if(this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol())!=4)
					{
						moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
					}

				}
				else if(mine==1 && empty1==3)
				{
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
				}
			}
		}//end of horizontal checking

		//veritcal checking

		for(int row = 0; row<board.length; row++)
		{
			int empty = 0, threat = 0, mine = 0;
			for(int col = 0; col<board.length; col++)
			{
				if(this.getTopOwnerOnBoard(col,row)!=playerId && this.getTopOwnerOnBoard(col,row)!=emptyPos)
				{
					threat++;
					opponentCoord = new Coordinate(col,row);
				}
				else if(this.getTopOwnerOnBoard(col,row)==emptyPos)
				{
					empty++;
					emptyCoord = new Coordinate(row,col);
				}
				else if(this.getTopOwnerOnBoard(col, row)==playerId)
				{
					mine++;
				}
				if(mine == 2 && empty == 2)
				{
					moveArray.add(new Gobblet(playerId, emptyCoord.getCol(), emptyCoord.getRow()));	
				}
				else if(mine==2 && threat==1 && empty==1)
				{
					if(this.getTopSizeOnBoard(opponentCoord.getCol(), opponentCoord.getRow())!=4)
					{
						moveArray.add(new Gobblet(playerId, emptyCoord.getCol(), emptyCoord.getRow()));
					}

				}
				else if(empty==3 && mine==1)
				{
					moveArray.add(new Gobblet(playerId, emptyCoord.getCol(), emptyCoord.getRow()));
				}
			}
		}//end of vertical checking

		/*-----------------------------------
		 * Diagonal checking
		 *----------------------------------*/
		//Diagonal 1
		int row1 = 0, col1 = 3;
		int empty=0;
		int mine = 0;
		int threat=0;
		while(col1>=0 && row1<board.length)
		{
			//my piece
			if(this.getTopOwnerOnBoard(row1, col1)== playerId)
			{
				mine++;
			}

			//empty spot
			else if(this.getTopOwnerOnBoard(row1,col1)==emptyPos)
			{
				empty++;
				emptyCoord = new Coordinate(row1,col1);
			}
			//opponent's
			else if(this.getTopOwnerOnBoard(row1, col1)!=playerId && this.getTopOwnerOnBoard(row1, col1)!=emptyPos)
			{
				threat++;
				opponentCoord = new Coordinate(row1,col1);

			}
			if(mine == 2)
			{
				//there are two empty spots or at the least 1 threat
				if(empty == 2)
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
				else if(threat ==1 && empty==1)
					if(this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol())!=4)
						moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
					else if(empty==3 && mine==1)
					{
						moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
					}
			}
			//add to the moveArray List
			row1++; col1--;
		}//end of diagonal 1 checking

		//Diagonal2 checking
		int row2 = 0, col2 = 0;
		int empty2=0;
		int mine2 = 0;
		int threat2=0;
		while(col2>=0 && row2<board.length)
		{
			//my piece
			if(this.getTopOwnerOnBoard(row2, col2)== playerId)
			{
				mine2++;
			}

			//empty spot
			else if(this.getTopOwnerOnBoard(row2,col2)==emptyPos)
			{
				empty2++;
				emptyCoord = new Coordinate(row2, col2);
			}
			//opponent's
			else if(this.getTopOwnerOnBoard(row2, col2)!=playerId && this.getTopOwnerOnBoard(row2, col2)!=emptyPos)
			{
				threat2++;
				opponentCoord = new Coordinate(row2,col2);

			}
			if(mine2 == 2)
			{
				//there are two empty spots or at the least 1 threat
				if(empty2==3 && mine2==3)
				{
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
				}
				else if(empty2 == 2)
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
				else if(threat2 ==1 && empty2==1)
					if(this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol())!=4)
						moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
			}
			//add to the moveArray List
			row2++; col2++;
		}//end of diagonal checking2
		if(moveArray.size()>=1)
		{
			runSmartMove = true;
		}
	}

	/**
	 * This method uses a boolean to check to see if I am the first person to go and make a move
	 * by taking my highest piece from the stack and just randomly put it anywhere.
	 * 
	 * If the condition are true then this function will make a random moves.
	 */
	public void isFirstToGo()
	{
		boolean isEmpty = true;
		for(int row = 0; row<board.length; row++)
		{
			for(int col = 0; col<board.length; col++)
			{
				if(this.getTopOwnerOnBoard(row, col)!=emptyPos)
					isEmpty = false;
			}
		}

		//empty is true
		if(isEmpty)
		{
			int randomCol = rand.nextInt(board.length-1);
			int randomRow = rand.nextInt(board.length-1);
			int randomStack =rand.nextInt(player2Stack.length)+ 1;

			start = new Coordinate(-1,-1);
			end = new Coordinate(randomRow, randomCol);
			piece = this.getTopSizeOnStack(playerId, randomStack);
			stack = randomStack;
			moved = true;
		}
	}//end of is finding out if i am first to go

	/**
	 * This function selects the correct player stack
	 * @param id - the player's id
	 * @return player's stack object
	 */
	public Stack[] stackSelection(int id)
	{
		if(id==1)
		{
			return player1Stack;
		}
		return player2Stack;
	}//end of selecting the correct player's stack

	/**
	 * This method randomly generates a moves to make if there were any
	 * created by smartMove(opponentCoord) function.
	 */
	public void executeSmartMove()
	{
		if(!moved)
		{
			if(runSmartMove)
			{
				if(moveArray.size()==1)
				{

					end = new Coordinate(moveArray.get(0).getRow(),moveArray.get(0).getCol());
				}
				else
				{
					int moveRandom = (int)(Math.random() * (moveArray.size()-1));
					end = new Coordinate(moveArray.get(moveRandom).getRow(),moveArray.get(moveRandom).getCol());
				}
				start = new Coordinate(-1,-1);
				moved = true;
			}
		}
	}//end of executeSmartMove

	/**
	 * This function defends threat in the horizontal.
	 * It checks horizontal if there are any threat then place the highest piece on the 
	 * empty location and add that threat location to the memory address.
	 * @param id - player's id
	 * @param temp - a default temp coordinate (-1,-1)
	 * @param opponentCoord -  the opponent coordinate default (-1,-1)
	 * @param myCoord - player's coordinate default (-1,-1)
	 * @param myEnemy - the opponent player id
	 */
	public void defendHorizontal(int id, Coordinate temp, Coordinate opponentCoord, Coordinate myCoord, int myEnemy)
	{
		for(int row = 0; row < board.length && !moved; row++)
		{
			int empty = 0, mine = 0, threat = 0;
			for(int col = 0; col < board.length && !moved; col++)
			{
				if(this.getTopOwnerOnBoard(row, col) != id && this.getTopOwnerOnBoard(row, col) != emptyPos)
				{
					threat++;
					opponentCoord = new Coordinate(row, col);
				}
				else if(this.getTopOwnerOnBoard(row, col) == id)
				{
					mine++;
					myCoord = new Coordinate(row, col);
				}
				else if(this.getTopOwnerOnBoard(row, col) == emptyPos)
				{
					empty++;
					temp = new Coordinate(row, col);
				}

				if(threat == 3 && empty == 1)
				{
					playerThreat.add(new Gobblet(playerId, temp.getRow(), temp.getCol()));
					int highest = highestPiece(id);
					
					int desired = 4;
					generalStack = stackSelection(id);
					boolean execute = false;
					if(!execute)
					{
						if(highest ==  desired)
						{
							for(int i =1; i<generalStack.length + 1; i++ )
							{
								if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
								{
									stack = i;
									start = new Coordinate(-1,-1);
									piece = this.getTopSizeOnStack(id, stack);
									end = temp;
									playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
									moved = true;
									execute = true;
									break;
								}      
							}//end of picking highest stack	
						}
					}
					else if(!execute)
					{
						pickAndDefendFromBoard();
						if(picked == true)
						{
							end = temp;
							moved = true;
							break;
						}
					}
					//just sucks it up I lost
					if(!execute)
					{
						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
								moved = true;
								execute = true;
								break;

							}      
						}//end of picking highest stack
					}
				}//if threat is 3

				if(threat==3 && mine==1)
				{
					if(this.getTopSizeOnBoard(myCoord.getRow(), myCoord.getCol())!=4)
					{
						gobbleSelfPreventWin(myCoord);
					}
				}
			}//col
		}//row
	}//end of horizontal threating
	
	/**
	 * This function checks to see if there is any threat in the vertical and defends it. 
	 * It places a highest guarding piece on the empty spot and add the threat location
	 * to the memory
	 * @param id - player's id
	 * @param temp - the temp coordinate default(-1,-1)
	 * @param opponentCoord - the opponent's coordinate default(-1,-1)
	 * @param myCoord - the player's coordinate default(-1,-1)
	 * @param myEnemy - the opponent's player id
	 */
	public void defendVertical(int id, Coordinate temp, Coordinate opponentCoord, Coordinate myCoord, int myEnemy)
	{
		for(int row = 0; row<board.length && !moved; row++)
		{
			int threat = 0, empty = 0, mine = 0;
			for(int col = 0; col<board.length &&!moved; col++)
			{
				//enemy spot is found
				if(this.getTopOwnerOnBoard(col, row)!=id && this.getTopOwnerOnBoard(col, row)!=emptyPos)
				{
					threat++;
					opponentCoord= new Coordinate(col,row);
				}//end of found enemy spot
				else if(this.getTopOwnerOnBoard(col, row)==id)
				{
					mine++;
					myCoord = new Coordinate(col,row);     
				}
				else if(this.getTopOwnerOnBoard(col, row)==emptyPos)
				{
					empty++;
					temp = new Coordinate(col,row);
				}

				//use my highest piece and defend
				if(threat == 3 && empty == 1)
				{
					playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
					generalStack = stackSelection(id);
					int highest = highestPiece(id);
					int desired = 4;
					boolean execute = false;
					
					if(!execute)
					{
						if(highest ==  desired)
						{
							for(int i =1; i<generalStack.length + 1; i++ )
							{
								if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
								{
									stack = i;
									start = new Coordinate(-1,-1);
									piece = this.getTopSizeOnStack(id, stack);
									end = temp;
									playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
									moved = true;
									execute = true;
									break;

								}      
							}//end of picking highest stack
						}
					}
					else if(!execute )
					{
						pickAndDefendFromBoard();
						if(picked == true)
						{
							end = temp;
							moved = true;
							execute = true;
							break;
						}
					}
					
					//last choice just sucks it up i lost
					if(!execute)
					{
						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
								moved = true;
								execute = true;
								break;
							}      
						}//end of picking highest stack
					}
				}
				if(threat==3 && mine==1)
				{
					if(this.getTopSizeOnBoard(myCoord.getRow(), myCoord.getCol())!=4)
					{
						gobbleSelfPreventWin(myCoord);
					}
				}
			}
		}      
	}//end of checking vertical for threats
	
	/**
	 * This function checks for threats in the diagonal. If a threat is found the empty spot is guarded
	 * and the threat location is added to the memory
	 * @param id - player's id
	 * @param temp - the temp coordinate default(-1,-1)
	 * @param opponentCoord - the opponent's coordinate default(-1,-1)
	 * @param myCoord - the player's coordinate default(-1,-1)
	 * @param myEnemy - the opponent's player id
	 */
	public void defendingDiagonal(int id, Coordinate temp,Coordinate opponentCoord,Coordinate myCoord,int myEnemy)
	{
		if(!moved)
		{
			int row = 0, col = 3;
			int empty=0, threat = 0;
			int mine = 0;
			while(col>=0 && row<board.length)
			{
				if(this.getTopOwnerOnBoard(row,col)!=playerId && this.getTopOwnerOnBoard(row,col)!=emptyPos)
				{
					threat++;
				}
				//i am on this location
				else if(this.getTopOwnerOnBoard(row, col)==playerId)
				{
					mine++;
					myCoord = new Coordinate(row,col);
				}

				else
				{
					empty++;
					temp = new Coordinate(row,col);
				}

				//get the location of the next empty space to defend myself
				if(threat == 3 && empty == 1)
				{
					playerThreat.add(new Gobblet(playerId, temp.getRow(), temp.getCol()));
					int highest = highestPiece(id);
					generalStack = stackSelection(id);
					boolean execute = false;
					int desired = 4;
					
					if(!execute)
					{
						if(highest == desired)
						{
							for(int i =1; i<generalStack.length + 1; i++ )
							{
								if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
								{
									//System.out.println("Look here Ranbir");
									stack = i;
									start = new Coordinate(-1,-1);
									piece = this.getTopSizeOnStack(id, stack);
									end = temp;
									playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
									moved = true;
									execute = true;
									break;
								}      
							}//end of picking highest stack
						}
					}
					else if(!execute)
					{
						pickAndDefendFromBoard();
						if(picked == true)
						{
							end = temp;
							moved = true;
							execute = true;
							break;
						}
					}
					//last choice to just suck it up
					if(!execute)
					{
						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								//System.out.println("Look here Ranbir");
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
								moved = true;
								execute = true;
								break;
							}      
						}//end of picking highest stack
					}
				}
				if(threat==3 && mine==1)
				{
					if(this.getTopSizeOnBoard(myCoord.getRow(), myCoord.getCol())!=4)
					{
						gobbleSelfPreventWin(myCoord);
					}
				}
				row++; col--;
			}
		}

		//Diagonal 2
		if(!moved)
		{
			int col = 0, row = 0;
			int empty=0, threat = 0, mine=0;
			while(row<board.length && col<board.length)
			{
				if(this.getTopOwnerOnBoard(row,col)!=playerId && this.getTopOwnerOnBoard(row,col)!=emptyPos)
				{
					threat++;
				}

				//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
				else if(this.getTopOwnerOnBoard(row,col)==playerId)
				{
					mine++;
					myCoord = new Coordinate(row,col);
				}

				else
				{
					empty++;
					temp = new Coordinate(row,col);
				}

				//get the location of the next empty space to defend myself
				if(threat == 3 && empty == 1)
				{
					playerThreat.add(new Gobblet(playerId, temp.getRow(), temp.getCol()));
					int highest = highestPiece(id);
					int desired = 4;
					boolean execute = false;
					generalStack = stackSelection(id);
					
					if(!execute)
					{
						if(highest == desired)
						{
							for(int i =1; i<generalStack.length + 1; i++ )
							{
								if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
								{
									stack = i;
									start = new Coordinate(-1,-1);
									piece = this.getTopSizeOnStack(id, stack);
									end = temp;
									playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
									moved = true;
									execute = true;
									break;
								}      
							}//end of picking highest stack
						}
					}
					else if(!execute)
					{
						pickAndDefendFromBoard();
						if(picked == true)
						{
							end = temp;
							moved = true;
							execute = true;
							break;
						}
					}
					
					//last choice to just pick any one that i can use to defend myself
					if(!execute)
					{
						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								playerThreat.add(new Gobblet(playerId,temp.getRow(),temp.getCol()));
								moved = true;
								execute = true;
								break;
							}      
						}//end of picking highest stack
					}
				}
				if(threat==3 && mine==1)
				{
					if(this.getTopSizeOnBoard(myCoord.getRow(), myCoord.getCol())!=4)
					{
						gobbleSelfPreventWin(myCoord);
					}
				}
				col++; row++;
			}
		}      
	}//end of diagonal defending function

	/**
	 * This function picks a safe highest piece from the board
	 * and use it to defense
	 */
	public void pickAndDefendFromBoard()
	{
		for(int row = 0; row<board.length&&!picked &&!moved; row++)
		{
			for(int col = 0; col<board.length; col++)
			{
				int highest = 4;
				//find my piece and this piece is 4
				if(this.getTopOwnerOnBoard(row, col)==playerId && this.getTopSizeOnBoard(row, col)==highest)
				{
					//check to ensure that this piece is not defending anything
					if(!playerThreat.contains(new Gobblet(playerId,row,col)))
					{
						//System.out.println("Reached????");
						playerThreat.add(new Gobblet(playerId,row,col));
						piece = this.getTopSizeOnBoard(row, col);
						start = new Coordinate(row,col);
						stack = 0;
						picked = true;
						break;
					}
				}
			}
		}
	}

	/**
	 * This function pick a piece from the board and use it in winning
	 * the game.
	 */
	public void pickAndWinFromBoard(int opponentPiece, Coordinate opponentCoord)
	{
		boolean quit = false;
		for(int i=0; i<board.length &&!quit; i++)
		{
			for(int j = 0; j<board.length; j++)
			{
				//this is me and my piece is greater than the opponent's
				if(this.getTopOwnerOnBoard(i, j)==playerId)
				{
					if(this.getTopSizeOnBoard(i, j)>opponentPiece && !winArray.contains(new Gobblet(playerId,i,j)))
					{
						stack = 0; 
						piece = this.getTopSizeOnBoard(i, j);
						start = new Coordinate(i,j);
						end = new Coordinate(opponentCoord.getRow(),opponentCoord.getCol());
						moved = true;
						quit = true;
						break;
					}
				}
			}
		}
	}

	/**
	 * This picks a piece from the board(4) and gobble my smaller piece 
	 * to prevent the opponent from winning 
	 */
	public void gobbleSelfPreventWin(Coordinate myCoord)
	{
		boolean quit = false;
		//use one of the piece from the stack instead
		for(int i = 1; i<generalStack.length +1; i++)
		{
			if(!generalStack[i-1].isEmpty())
			{
				if(this.getTopSizeOnStack(playerId, i)==4)
				{
					stack = i;
					piece = this.getTopSizeOnStack(playerId, stack);
					start = new Coordinate(-1,-1);
					end = new Coordinate(myCoord.getRow(),myCoord.getCol());
					playerThreat.add(new Gobblet(playerId,end.getRow(),end.getCol()));
					moved = true;
					quit = true;
					break;
				}
			}
		}
		
		if(quit== false)
		{
			for(int i=0; i<board.length &&!quit; i++)
			{
				for(int j = 0; j<board.length; j++)
				{
					//this is me and my piece greater than the one guidiing my opponent
					if(this.getTopOwnerOnBoard(i, j)==playerId)
					{
						if(this.getTopSizeOnBoard(i, j)==4 && !playerThreat.contains(new Gobblet(playerId,i,j)))
						{
							stack = 0; 
							piece = this.getTopSizeOnBoard(i, j);
							start = new Coordinate(i,j);
							end = new Coordinate(myCoord.getRow(),myCoord.getCol());
							playerThreat.add(new Gobblet(playerId,end.getRow(),end.getCol()));
							moved = true;
							quit = true;
							break;

						}
					}

				}

			}
			
		}
	}

	/**
	 * This function returns who is the opponent
	 * @return an integer represent the opponent's player id
	 */
	public int Opponent()
	{
		if(playerId == 1)
			return 2;
		return 1;
	}

	/**
	 * This method is used to select the highest piece from the stack.
	 * @return highest piece current available on the stack.
	 */
	public int highestPiece(int id)
	{
		int highest  = 0;
		for(int i = 1; i < generalStack.length+1; i++)
		{
			if(!generalStack[i-1].isEmpty())
			{
				if(this.getTopSizeOnStack(id,  i) > highest)
				{
					highest= this.getTopSizeOnStack(id, i);
				}
			}
		}
		return highest;
	}

	/**
	 * This function checks if it is possible for the player to perform a quick win in horizontal row
	 * hence prevent if from going to defends first and let the opponent win
	 * 
	 * @param id - the player's id
	 * @param temp - a temp coordinate default (-1,-1)
	 * @param opponentCoord -  the opponent coordinate
	 */
	public void horizontalWin(int id, Coordinate temp, Coordinate opponentCoord)
	{
		for(int row = 0; row<board.length&&!moved; row++)
		{
			int empty = 0, mine=0,opp = 0; 
			winArray = new ArrayList<Gobblet>();
			for(int col = 0; col<board.length; col++)
			{
				//my piece
				if(this.getTopOwnerOnBoard(row, col)==id)
				{
					mine++;
					winArray.add(new Gobblet(playerId,row,col));

				}

				//empty spot
				else if(this.getTopOwnerOnBoard(row, col)==emptyPos)
				{
					empty++;
					temp= new Coordinate(row, col);//keep track of the last empty location

				}
				//opponent piece
				else if(this.getTopOwnerOnBoard(row, col)!=playerId)
				{
					opponentCoord = new Coordinate(row,col);
					opp++;
				}

				//i can win
				if( mine == 3 && empty==1)
				{
					//System.out.println("We have got to horizontal win");
					end = temp;
					moved = true;
					break;
				}

				if(mine== 3 && opp == 1 && movesMade>2)
				{
					int opponentPiece = this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol());
					pickAndWinFromBoard(opponentPiece,opponentCoord);
				}
				//gobble the opponent and win
			}
		}
	}//end of horizontial win

	/**
	 * This function checks if it is possible for a player to win quickly by placing piece
	 * in the vertical rows thus prevent it from going to defending not realizing that it can 
	 * win via vertical
	 * 
	 * @param id - the player's id
	 * @param temp - a temp coordinate default (-1,-1)
	 * @param opponentCoord -  the opponent coordinate
	 */
	public void verticalWinning(int id, Coordinate temp, Coordinate opponentCoord)
	{
		for(int row = 0; row<board.length &&!moved; row++)
		{
			int empty=0, mine=0,opp=0;
			winArray = new ArrayList<Gobblet>();

			for(int col=0; col<board.length; col++)
			{
				if(this.getTopOwnerOnBoard(col, row)==playerId)
				{
					mine++;
					winArray.add(new Gobblet(playerId, col,row));
				}
				//a empty spot is found
				else if(this.getTopOwnerOnBoard(col,row)==emptyPos)
				{
					empty++;
					temp = new Coordinate(col,row);//keep track of the most recently empty location
				}
				else if(this.getTopOwnerOnBoard(col, row)!=playerId && this.getTopOwnerOnBoard(col,row)!=emptyPos)
				{
					opp++;
					opponentCoord = new Coordinate(col,row);
				}

				//defend the empty location if it is threatning
				if(mine==3 && empty==1)
				{
					end = temp;
					moved = true;
					break;
				}

				if(mine==3 && opp==1 && movesMade>2 )
				{
					int opponentPiece = this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol());
					pickAndWinFromBoard(opponentPiece, opponentCoord);
				}

			}//end of col
		}//end of row

	}//end of winning vertical
	
	/**
	 * This function checks to see if it is possible for the player to perform a quick win in the diagonal rows thus 
	 * preventing it from going to defend not realizing that it can win via diagonal
	 * 
	 * @param id - the player's id
	 * @param temp - a temp coordinate default (-1,-1)
	 * @param opponentCoord -  the opponent coordinate
	 */
	public void diagonalWin(int id, Coordinate temp, Coordinate opponentCoord)
	{
		if(!moved)
		{
			int row = 0, col = 3;
			int empty=0;
			int mine = 0;
			int opp = 0;
			winArray = new ArrayList<Gobblet>();
			while(col>=0 && row<board.length)
			{
				if(this.getTopOwnerOnBoard(row, col)==id && this.getTopOwnerOnBoard(row,col)!=emptyPos)
				{
					mine++;
					winArray.add(new Gobblet(playerId,row,col));
				}

				else if(this.getTopOwnerOnBoard(row,col)==emptyPos)
				{
					empty++;
					temp = new Coordinate(row,col);
				}
				else if(this.getTopOwnerOnBoard(row, col)!=playerId && this.getTopOwnerOnBoard(row, col)!=emptyPos)
				{
					opp++;
					opponentCoord = new Coordinate(row,col);
				}

				//get the location of the next empty space to win
				if(mine==3 && empty==1)
				{
					end = temp;
					moved = true;
				}
				if(mine==3 && opp==1 && movesMade>2 )
				{
					int opponentPiece = this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol());
					pickAndWinFromBoard(opponentPiece, opponentCoord);
				}

				row++; col--;
			}
		}

		//is there any threat in the diagonal 2?
		if(!moved)
		{
			int col1 = 0, row1 = 0;
			int empty1=0, mine1=0;
			int opp1 = 0;
			winArray = new ArrayList<Gobblet>();
			while(row1<board.length && col1<board.length)
			{
				if(this.getTopOwnerOnBoard(row1,col1)==id&& this.getTopOwnerOnBoard(row1,col1)!=emptyPos)
				{
					mine1++;
					winArray.add(new Gobblet(playerId,row1,col1));
				}

				//I am on this row as well. No worries :-)..this is not true for every case but that is not important for part 2 :-)
				else if(this.getTopOwnerOnBoard(row1,col1)==emptyPos)
				{
					empty1++;
					temp = new Coordinate(row1,col1);
				}
				else if(this.getTopOwnerOnBoard(row1, col1)!=playerId && this.getTopOwnerOnBoard(row1, col1)!=emptyPos)
				{
					opp1++;
					opponentCoord = new Coordinate(row1,col1);
				}

				//get the location of the next empty space to win
				if(mine1==3 && empty1==1)
				{
					end = temp;
					moved = true;
					break;
				}
				if(mine1==3 && opp1==1 && movesMade>2 )
				{
					int opponentPiece = this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol());
					pickAndWinFromBoard(opponentPiece, opponentCoord);
				}
				col1++; row1++;
			}
		}	
	}//end of diagonal win function

	/**
	 * This function made a move on the board without gobble the opponent
	 */
	public void moveWithoutGobbleTheOpponent()
	{
		//System.out.println("GOT TO HERE?");
		
		
		//System.out.println("stack: " + stack);
		//System.out.println("moved: " + moved);
		if(stack!=0 && !moved)
		{
			//System.out.println("This is trying to executed...");
			//search for an empty spot on the board
			for(int row = 0; row<board.length&&!moved; row++)
			{
				for(int col = 0; col<board.length; col++)
				{
					//an empty spot is found
					if(this.getTopOwnerOnBoard(row, col)==emptyPos)
					{
						start = new Coordinate(-1,-1);
						end = new Coordinate(row,col);
						moved = true;
						break;//break out of the loop from going through out all the board
					}
				}//end of col search
			}//end of row search	
		}//end of if stack is not empty

		//the stacks are empty and I can move a piece from the board and put it on a empty location
		if(stack == 0 &&!moved)
		{
			//System.out.println("Stack is 0 and we have got here");
			for(int row = 0; row<board.length &&!moved; row++)
			{
				for(int col = 0; col<board.length; col++)
				{
					//take one of my peice from the board
					if(this.getTopOwnerOnBoard(row, col)==playerId)
					{
						start = new Coordinate(row, col);
						piece = this.getTopSizeOnBoard(row, col);
						moved = true;
						break;
					}
				}
			}//end of finding one of my piece

			moved = false;
			//finding an empty spot to place the piece
			for(int row=0; row<board.length&&!moved; row++)
			{
				//an empty spot is found
				for(int col=0; col<board.length; col++)
				{
					if(this.getTopOwnerOnBoard(row, col)==emptyPos)
					{
						end = new Coordinate(row, col);
						moved = true;
						break;
					}
				}
			}//end of finding empty spot
		}//end of if stack is 0

	}//end of moving without gobble the opponent

	/**
	 * This function moves the piece from the board and includes
	 * gobbling the opponent. This is done with piece that is
	 * higher than the opponent's and that piece is not guarding
	 * anything
	 */
	public void gobbleOpponentFromBoard()
	{
		//go through the board
		for(int row = 0; row<board.length && !moved; row++)
		{
			Coordinate check = new Coordinate(-1,-1);
			for(int col = 0; col<board.length && !moved; col++)
			{
				//my piece is found
				if(this.getTopOwnerOnBoard(row, col)==playerId)
				{
					int myPiece = this.getTopSizeOnBoard(row, col);
					check = new Coordinate(row,col);
					//compare my piece against everything else on the board
					for(int compareRow = 0; compareRow<board.length && !moved; compareRow++)
					{
						for(int compareCol = 0; compareCol<board.length; compareCol++)
						{
							if(this.getTopOwnerOnBoard(compareRow, compareCol)!=playerId && this.getTopSizeOnBoard(compareRow, compareCol)!=emptyPos)
							{
								int opponentPiece = this.getTopSizeOnBoard(compareRow, compareCol);
								//is my piece greater than the opponent?
								if(myPiece > opponentPiece && !playerThreat.contains(new Gobblet(playerId,check.getRow(),check.getCol())))
								{
									piece = myPiece;
									stack = 0;
									start = new Coordinate(row, col);
									end = new Coordinate(compareRow, compareCol);
									moved = true;
									break;
								}
							}
						}
					}
				}	
			}
		}
	}//end of gobble the opponent from stack
	
	/**
	 * This method is used to update the ThreatLocation to ensure that it 
	 * reflect the correct states according to the board
	 */
	public void updateThreatLocationList()
	{
		boolean checkVertical = false, checkHorizontal = false, checkDiagonal1 = false,checkDiagonal2 = false;
		boolean quit = false;
		for(int check = 0; check< playerThreat.size(); check++)
		{
			int tempRow = playerThreat.get(check).getRow();

			//uses to compare in the diagonals
			int diagRow = playerThreat.get(check).getRow();
			int diagCol = playerThreat.get(check).getCol();

			//checking horizontal
			for(int row = tempRow; row<board.length &&!quit; row++)
			{
				int threat = 0;
				for(int col =0; col<board.length; col++)
				{
					//System.out.println("Current location: "+ row + ","+ col);
					if(this.getTopOwnerOnBoard(row, col)!=playerId && this.getTopOwnerOnBoard(row, col)!=emptyPos)
					{
						threat++;
					}
					else
					{
						if(threat==3)
							checkHorizontal = false;
						checkHorizontal = true;

						if(col==board.length-1)
						{
							quit = true;
							break;	
						}

					}	
				}
			}//end of horizontal update

			//checking vertical
			int tempCol = playerThreat.get(check).getCol();
			for(int row = tempCol; row<board.length &&!quit; row++)
			{
				int threat = 0;
				for(int col =0; col<board.length; col++)
				{
					if(this.getTopOwnerOnBoard(row, col)!=playerId && this.getTopOwnerOnBoard(row, col)!=emptyPos)
					{
						threat++;
					}
					else
					{
						if(threat==3)
							checkVertical = false;
						checkVertical = true;
						if(col==board.length-1)
						{
							quit = true;
							break;	
						}

					}	
				}
			}//end of vertical update

			//diagonal check
			//diagonal 1
			int row = 0, col = 3;
			int threat = 0;
			boolean possess1 = false;
			while(col>=0 && row<board.length)
			{
				if(this.getTopOwnerOnBoard(row,col)!=playerId && this.getTopOwnerOnBoard(row,col)!=emptyPos)
				{
					threat++;
				}
				//get the location of the next empty space to defend myself
				if(threat == 3)
				{
					checkDiagonal1= false;
				}
				if(row == diagRow && col == diagCol)
					possess1 = true;
				if(row==board.length-1&&threat<3 && col==0 && !possess1)
					checkDiagonal1 = true;
				row++; col--;
			}

			//diagonal2
			int boardCheck = 0;
			int threat1 = 0;
			boolean possess = false; // sol 5th floor is it then kk btw i want to check something nice I still have more lines to add i know I 
			while(boardCheck<board.length)
			{
				if(this.getTopOwnerOnBoard(boardCheck,boardCheck)!=playerId && this.getTopOwnerOnBoard(boardCheck,boardCheck)!=emptyPos)
				{
					threat1++;
				}

				if(threat1 == 3)
				{
					checkDiagonal2= false;
				}
				if(boardCheck == diagRow && boardCheck == diagCol)
					possess = true;
				if(boardCheck==board.length-1&&threat1<3 && !possess)
					checkDiagonal2 = true;
				boardCheck++;
			}

			//no longer threat
			if(checkVertical && checkHorizontal && checkDiagonal1 && checkDiagonal2 )
			{
				playerThreat.remove(check);
			}
		}
	}

	public void twoInARowThreatDanger()
	{
		//check the vertical
		for(int vertRow = 0; vertRow<board.length &&!moved; vertRow++)
		{
			twoInARowThreat = new ArrayList<Gobblet>();
			int isIn = 0;
			int vertThreat = 0;
			Coordinate doesContain = new Coordinate(-1,-1);
			for(int vertCol = 0; vertCol<board.length &&!moved; vertCol++)
			{
				//keeping track of the information
				twoInARowThreat.add(new Gobblet(playerId,vertCol,vertRow));
				if(this.getTopOwnerOnBoard(vertCol, vertRow)==playerId)
				{
					break;
				}
				if(this.getTopOwnerOnBoard(vertCol,vertRow)!=playerId && this.getTopOwnerOnBoard(vertCol,vertRow)!=emptyPos)
				{
					vertThreat++;
				}

				//we have got to the end of a vertical position
				if(vertCol==board.length-1 && vertThreat==2)
				{
					//checking the horizontal
					for(int horizRow = 0; horizRow<board.length&&!moved; horizRow++)
					{
						int horizThreat = 0;
						for(int horizCol = 0; horizCol<board.length &&!moved; horizCol++)
						{
							//if i am on this row the quit
							if(this.getTopOwnerOnBoard(horizRow, horizCol)==playerId)
							{
								break;
							}
							if(this.getTopOwnerOnBoard(horizRow,horizCol)!=playerId && this.getTopOwnerOnBoard(horizRow,horizCol)!=emptyPos)
							{
								horizThreat++;


							}
							//does the vertical contain this location as well?
							if(twoInARowThreat.contains(new Gobblet(playerId,horizRow,horizCol)))
							{
								doesContain = new Coordinate(horizRow,horizCol);

							}

							//we have got to the end of the horiz checking
							if(horizCol == board.length-1 && vertThreat == 2 && horizThreat==2)
							{
								//the player piece is not 4
								if(this.getTopSizeOnBoard(doesContain.getRow(),doesContain.getCol())!=4)
								{
									//pick my highest piece from the board and gobble that opponent.
									for(int pickRow = 0; pickRow<board.length && !moved; pickRow++)
									{
										for(int pickCol = 0; pickCol<board.length &&!moved; pickCol++)
										{
											if(this.getTopOwnerOnBoard(pickRow, pickCol)==playerId && this.getTopSizeOnBoard(pickRow, pickCol)==4)
											{
												if(!playerThreat.contains(new Gobblet(playerId,pickRow,pickCol)))
												{
													start = new Coordinate(pickRow,pickCol);
													stack = 0;
													piece = this.getTopSizeOnBoard(pickRow, pickCol);
													end = doesContain;
													moved = true;
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This function checks to see if the opponent is tricking me in making a move
	 * that spite blocking him He can still win
	 */
	public void defendTwoInARowHorizVerticalByBlockingFirstCell()
	{
		int  col = 0;
		int row  = 0;
		Coordinate coord = new Coordinate(-1,-1);
		for(int vertRow = 0; vertRow<board.length &&!moved; vertRow++)
		{
			int verThreat = 0;
			for(int vertCol = 0; vertCol <board.length &&!moved; vertCol++)
			{
				if(this.getTopOwnerOnBoard(vertRow, vertCol)==playerId && this.getTopSizeOnBoard(vertRow, vertCol)==4)
					break;
				if(this.getTopOwnerOnBoard(vertCol, vertRow)!=playerId && this.getTopOwnerOnBoard(vertCol, vertRow)!=emptyPos)
				{
					verThreat++;
				}

				//we have got to the end of the vertical column
				if(vertCol==board.length-1&&verThreat==3)
				{
					//now search the horizontal
					for(int horizRow = 0; horizRow< board.length &&!moved; horizRow++)
					{
						int horizThreat = 0;
						for(int horizCol = 0; horizCol<board.length &&!moved; horizCol++)
						{
							if(this.getTopOwnerOnBoard(horizRow, horizCol)==playerId && this.getTopSizeOnBoard(horizRow, horizCol)==4)
								break;
							if(this.getTopOwnerOnBoard(horizRow, horizCol)!=playerId && this.getTopOwnerOnBoard(horizRow, horizCol)!=emptyPos)
							{
								horizThreat++;
							}

							//a  horiz search has been completed
							if(horizCol==board.length-1 && horizThreat==3)
							{
								if(this.getTopSizeOnBoard(row, col)!=4)
								{

									//pick my highest piece from the board and gobble that opponent.
									for(int pickRow = 0; pickRow<board.length && !moved; pickRow++)
									{
										for(int pickCol = 0; pickCol<board.length &&!moved; pickCol++)
										{
											if(this.getTopOwnerOnBoard(pickRow, pickCol)==playerId && this.getTopSizeOnBoard(pickRow, pickCol)==4)
											{
												if(!playerThreat.contains(new Gobblet(playerId,pickRow,pickCol)))
												{
													start = new Coordinate(pickRow,pickCol);
													stack = 0;
													piece = this.getTopSizeOnBoard(pickRow, pickCol);
													end = new Coordinate(row,col);
													moved = true;
													break;
												}
											}
										}
									}
								}
							}
						}
						row++;
					}
				}
			}
			col++;
		}
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
		// TODO Auto-generated method stub
		System.out.println("Invalid move PLAYER " + arg0+ " HAS BEEN KICKED OUT");
		throw new UnsupportedOperationException(); 
	}

}


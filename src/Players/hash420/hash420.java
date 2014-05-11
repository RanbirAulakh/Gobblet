/**
 * $Id: KSC2650.java,v 1.3 2014/05/07 19:20:41 ksc2650 Exp $
 * $Log: KSC2650.java,v $
 * Revision 1.3  2014/05/07 19:20:41  ksc2650
 * *** empty log message ***
 *
 * Revision 1.2  2014/05/07 18:32:40  ksc2650
 * correct the error in the memory location
 *
 * Revision 1.1  2014/05/07 16:22:03  ksc2650
 * installed memory tracker
 *
 * Revision 1.4  2014/03/11 22:13:27  ksc2650
 * formating correct! DONE WITH THIS!! YEAH!!!
 *
 * Revision 1.3  2014/03/11 21:41:23  ksc2650
 * everything works! just need to fix the formating display
 *
 * Revision 1.2  2014/03/09 06:14:01  ksc2650
 * init function completed. Stack for the init need to be completed
 *
 * Revision 1.1  2014/03/09 02:59:22  ksc2650
 * Setting up...Let us go!
 */
package Players.hash420;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;

import Engine.Logger;
import Interface.Coordinate;
import Interface.GobbletPart1;
import Interface.PlayerModule;
import Interface.PlayerMove;
/**
 * @author Kemoy
 * 
 *
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

	//Moves arrayList;
	private ArrayList<Gobblet> moveArray;

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
	 * Display, on standard output, a representation of how the physical
	 * game would appear at this point in time.
	 */
	public void dumpGameState() 
	{
		//for the individual rows
		String row1="";
		String row2="";
		String row3 ="";
		String row4="";

		//for stacks
		String stackPlayer1="";
		String stackPlayer2="";

		//combination of all string to create the final display
		String display;


		/*------------------------------------------------*
		 *                                                *
		 *                                                *
		 *      BOARD DISPLAY SETUP                       *
		 *                                                *
		 *                                                *
		 *------------------------------------------------*/

		//the first row display
		for(int col=0; col<4; col++)
		{
			int owner = getTopOwnerOnBoard(0,col);
			int pieceVal = getTopSizeOnBoard(0,col);
			if(owner==-1)
			{
				row1+=String.format("%-4s","")+"[]";
			}
			else
			{
				row1+=String.format("%-2s","")+pieceVal +"("+owner+")";
			}


		}

		//the second row display
		for(int col=0; col<4; col++)
		{
			int owner = getTopOwnerOnBoard(1,col);
			int pieceVal = getTopSizeOnBoard(1,col);
			if(owner==-1)
			{
				row2+=String.format("%-4s","")+"[]";
			}
			else
			{
				row2+=String.format("%-2s","")+pieceVal +"("+owner+")";
			}

		}

		//the third  row display
		for(int col=0; col<4; col++)
		{
			int owner = getTopOwnerOnBoard(2,col);
			int pieceVal = getTopSizeOnBoard(2,col);
			if(owner==-1)
			{
				row3+=String.format("%-4s","")+ "[]";
			}
			else
			{
				row3+=String.format("%-2s","")+pieceVal +"("+owner+")";
			}

		}

		//the fourth row display
		for(int col=0; col<4; col++)
		{
			int owner = getTopOwnerOnBoard(3,col);
			int pieceVal = getTopSizeOnBoard(3,col);
			if(owner==-1)
			{
				row4+=String.format("%-4s","") + "[]";
			}
			else
			{
				row4+=String.format("%-2s","") +pieceVal +"("+owner+")" + "  ";
			}

		}

		/*------------------------------------------------*
		 *                                                *
		 *                                                *
		 *      STACK DISPLAY SETUP                       *
		 *                                                *
		 *                                                *
		 *------------------------------------------------*/

		//player1 stack
		for(int stack=1; stack<4; stack++)
		{
			int stackNo1;

			stackNo1 = getTopSizeOnStack(1, stack);
			//System.out.println("StackNO1: "+stackNo1);
			if(stackNo1==-1)
			{
				stackPlayer1+=String.format("%-2s","")+"_";
			}

			else
			{
				stackPlayer1+=String.format("%-2s","")+stackNo1;
			}
		}

		//player2 stack
		for(int stack=1; stack<4; stack++)
		{
			int stackNo2;
			stackNo2 = getTopSizeOnStack(2, stack);
			if(stackNo2==-1)
			{
				stackPlayer2+=String.format("%-2s","")+"_";
			}

			else
			{
				stackPlayer2+=String.format("%-4s","")+stackNo2;
			}
		}

		//combining all strings
		display=row1+""+stackPlayer1+"\n"+row2+"\n"+row3+""+stackPlayer2+"\n"+row4;

		System.out.println(display);
	}


	public int getID() {

		return playerId;
	}

	/**
	 * Describe what is visible on the board at a given location.
	 *
	 * @param row - the row of interest on the board (0-based)
	 * @param col - the column of interest on the board (0-based)
	 * @return the ID of the player (1-2) that owns the piece on
	 * top of the stack of pieces at the given location on the board.
	 * If the stack is empty, return -1.
	 */
	public int getTopOwnerOnBoard(int row, int col) {
		if(board[row][col].isEmpty())
		{
			return -1;

		}
		else
		{
			return board[row][col].peek().getPlayerId();
		}
	}

	/**
	 * Describe what is visible on the board at a given location.
	 */
	public int getTopSizeOnBoard(int row, int col) {
		if(board[row][col].isEmpty())
		{
			return -1;

		}
		else
		{
			return board[row][col].peek().getPieceValue();
		}
	}
	/**
	 * Describe what remains on top of one of the stacks of unplayed pieces of a certain player.
	 *
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
		if(playerID==1)
		{


			if(player1Stack[stack].isEmpty())
			{
				return -1;
			}
			else
			{
				size= (int)player1Stack[stack].peek();
			}

		}

		if(playerID==2)
		{
			if(player2Stack[stack].isEmpty())
			{
				return -1;
			}
			else
			{
				size= (int)player2Stack[stack].peek();
			}

		}
		return size;
	}


	/**
	 *This functions takes two parameters args0 and args1 which represent 
	 *logger and players'ID respectively. This is use to set up the initialization of
	 *the gobblet game
	 *
	 *@param Logger logger takes the log 
	 *
	 *@param takes the player's ID
	 */

	public void init(Logger logger, int playerId) 
	{
		this.logger = logger;
		this.playerId = playerId;

		//initalize the board and create an empty stack at each location
		for(int row=0; row<ROW; row++)
		{
			for(int col=0; col<COL; col++ )
			{
				board[row][col] = new Stack<Piece>();
				//board[row][col].push(new Piece(playerId,0));
			}
		}

		//SET UP THE PLAYERS WITH THEIR REPRESENTATION PIECES
		//Each players have three pieces of stack
		for(int i=0;i<3;i++)
		{
			player1Stack[i] = new Stack<Integer>();
			player2Stack[i] = new Stack<Integer>();

		}

		for(int i=0;i<3;i++)
		{
			for(int j=1;j<=4;j++)
			{

				player1Stack[i].push(j);
				player2Stack[i].push(j);
			}

		}
	}

	/**
	 * Notified the player of th last move made by the move function
	 * @param move the move object
	 */
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
		if(id==1)
		{	
			//the piece need to move is off the board
			if(startRow==offBoardRow && startCol==offBoardCol)
			{
				//System.out.println("The stack here is: "+stack);
				stackPos = player1Stack[stack-1].pop();
				board[endRow][endCol].push(new Piece(id,stackPos));
			}
			else
			{
				//the piece is already on the board
				popPieceFromBoard = board[startRow][startCol].pop();
				//System.out.println("PIECE POPPED :"+popPieceFromBoard.getPieceValue());
				//System.out.println(stack - 1);
				board[endRow][endCol].push(new Piece(popPieceFromBoard.getPlayerId(),popPieceFromBoard.getPieceValue()));
			}


		}


		//if player id is 2
		if(id==2)
		{	
			//the piece need to move is off the board
			if(startRow==offBoardRow && startCol==offBoardCol)
			{

				stackPos = (int)player2Stack[stack-1].pop();
				board[endRow][endCol].push(new Piece(id,stackPos));
			}
			else
			{
				//the piece is already on the board
				popPieceFromBoard = board[startRow][startCol].pop();
				board[endRow][endCol].push(new Piece(popPieceFromBoard.getPlayerId(),popPieceFromBoard.getPieceValue()));
			}

		}
	}

	/* (non-Javadoc)
	 * @see Interface.PlayerModule#move()
	 */
	@Override
	/**
	 * Construct a move object
	 */
	public PlayerMove move() 
	{
		//necessary variable and initalizations
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
		boolean boardEmpty;
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
		//WINNING
		horizontalWin(playerId,temp,opponentCoord);
		verticalWinning(playerId,temp,opponentCoord);
		diagonalWin(playerId,temp,opponentCoord);
		//DEFENDING STRATEGIES
		defendHorizontal(playerId,temp,opponentCoord,  myCoord, myEnemy);
		defendVertical(playerId,temp,opponentCoord, myCoord, myEnemy);
		defendingDiagonal(playerId, temp,opponentCoord, myCoord,myEnemy);

		//creating the possible moves
		//System.out.println("Size of SmartMoves before execution " + moveArray.size());
		smartMoves(opponentCoord);
		/*System.out.println("Size of SmartMoves " + moveArray.size());
		if(moveArray.size()>=1)
		{
			for(int i = 0; i<moveArray.size(); i++)
			{
				System.out.println("Coordinate: "+moveArray.get(i).getRow() + ","+moveArray.get(i).getCol());
			}
		}*/

		//randomly selecting one of the possible moves and execute if need
		executeSmartMove();
		if(!moved && movesMade<2)
		{
			System.out.println("got here?");
			int randomCol = (int)(Math.random() *(board.length-1));
			int randomRow = (int)(Math.random() *(board.length-1));
			int randomStack =(int)(Math.random() *(generalStack.length-1)+1);

			//the positions are not empty

			while(this.getTopOwnerOnBoard(randomRow, randomCol)!=emptyPos)
			{
				randomCol = (int)(Math.random() *(board.length-1));
				//System.out.println("final random for col" + randomCol);
				//randomRow = (int)(Math.random() *(board.length-1));
				System.out.println("final random for row" + randomRow);
			}
			while(this.getTopSizeOnStack(playerId, randomStack)==emptyPos)
			{
				randomStack =(int)(Math.random() *(generalStack.length-1)+1);
			}



			start = new Coordinate(-1,-1);
			end = new Coordinate(randomRow, randomCol);
			piece = this.getTopSizeOnStack(playerId, randomStack);
			stack = randomStack;
			System.out.println("Executed???");
			moved = true;


		}
		moveWithoutGobbleTheOpponent();
		gobbleOpponentFromBoard();

		if(moved)
		{
			move = new PlayerMove(playerId, stack,piece, start, end);//the blue was my code playing itself using the AI i made sweet! look through 
			movesMade++;
		}

		return move;


	}

	/**
	 * Generate smartMoves
	 * FUNCTION PASSED
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
					System.out.println("empty coordinate: " + emptyCoord.getRow()+","+emptyCoord.getCol());
					moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));	
				}
				else if(mine==2 && threat==1 && empty1==1)
				{
					if(this.getTopSizeOnBoard(opponentCoord.getRow(), opponentCoord.getCol())!=4)
					{
						moveArray.add(new Gobblet(playerId, emptyCoord.getRow(), emptyCoord.getCol()));
					}

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
					//System.out.println("empty coordinate: " + emptyCoord.getCol()+","+emptyCoord.getRow());
					moveArray.add(new Gobblet(playerId, emptyCoord.getCol(), emptyCoord.getRow()));	
				}
				else if(mine==2 && threat==1 && empty==1)
				{
					if(this.getTopSizeOnBoard(opponentCoord.getCol(), opponentCoord.getRow())!=4)
					{
						System.out.println("Empty coordinate for vertical: " + emptyCoord.getCol() +" "+emptyCoord.getRow() );
						moveArray.add(new Gobblet(playerId, emptyCoord.getCol(), emptyCoord.getRow()));
					}

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
				if(empty2 == 2)
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
			System.out.println("Executing...");
		}

	}

	/**
	 * This method checks to see if I am the first person to go and make a move
	 * by taking my highest piece from the stack and just randomly put it anywhere
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
	 * @param id
	 * @return
	 * FUNCTION PASSED
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
	 * method to run the smart move
	 * FUNCTION PASSED
	 */
	public void executeSmartMove()
	{
		if(!moved)
		{
			System.out.println("The boolean condition for smartMove is : " + moved);
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
				System.out.println("SmartMove coordinate : "+ end.getRow() +"," +end.getCol());
				moved = true;
			}
		}

	}//end of executeSmartMove

	/**
	 * This function defends threat in the horizontal 
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
					generalStack = stackSelection(id);
					pickAndDefendFromBoard();
					System.out.println("Temp before modified: "+temp.getRow()+","+temp.getCol());
					if(picked == true)
					{
						end = temp;
						moved = true;
						System.out.println("Picked is " + picked);
						break;
					}

					else
					{

						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								System.out.println("Look here Ranbir");
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								moved = true;
								break;

							}      
						}//end of picking highest stack
					}//end else


				}//if threat is 3
			}//col
		}//row
	}//end of horizontal threating
	/**
	 * This function checks to see if there is any threat in the vertical and defends it
	 * @param id
	 * @param temp
	 * @param opponentCoord
	 * @param myCoord
	 * @param myEnemy
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
					int highest = highestPiece(id);
					generalStack = stackSelection(id);
					pickAndDefendFromBoard();
					System.out.println("Temp before modified: "+temp.getRow()+","+temp.getCol());
					if(picked == true)
					{
						end = temp;
						moved = true;
						System.out.println("Picked is " + picked);
						break;
					}
					else
					{

						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								System.out.println("Look here Ranbir");
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								moved = true;
								break;

							}      
						}//end of picking highest stack
					}//end else
				}
			}
		}      
	}//end of checking vertical for threats
	/**
	 * This function checks for threats in the diagonal
	 * @param id
	 * @param temp
	 * @param opponentCoord
	 * @param myCoord
	 * @param myEnemy
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
					pickAndDefendFromBoard();
					System.out.println("Temp before modified: "+temp.getRow()+","+temp.getCol());
					if(picked == true)
					{
						end = temp;
						moved = true;
						System.out.println("Picked is " + picked);
						break;
					}

					else
					{

						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								System.out.println("Look here Ranbir");
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								moved = true;
								break;

							}      
						}//end of picking highest stack
					}//end else
				}
				row++; col--;
			}
		}//think I am gonna see if my prof free after my math class to fix this error.. costing me big time.. cant happen on the competition

		//is there any threat in the diagonal 2?//pi i am kidding... I am talking about u moding my code.. I am the author of ll moves in that code
		//when you randomize check if the stack exist because random will just give u any number between the one you want so suppose u randomize from 1 to 3 but stack 1 is now empty and u get random = 1 randomize it again because stack1 is empty so basically you say while(player1[random].isEmpty) random again
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
					pickAndDefendFromBoard();
					System.out.println("Temp before modified: "+temp.getRow()+","+temp.getCol());
					if(picked == true)
					{
						end = temp;
						moved = true;
						System.out.println("Picked is " + picked);
						break;
					}

					else
					{

						for(int i =1; i<generalStack.length + 1; i++ )
						{
							if(!generalStack[i-1].isEmpty() && this.getTopSizeOnStack(id, i)==highest )
							{
								System.out.println("Look here Ranbir");
								stack = i;
								start = new Coordinate(-1,-1);
								piece = this.getTopSizeOnStack(id, stack);
								end = temp;
								moved = true;
								break;

							}      
						}//end of picking highest stack
					}//end else
				}
				col++; row++;
			}
		}      
	}//end of diagonal defending function

	/**
	 * This function picks a piece from the board which is use in defendings
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
						System.out.println("Reached????");
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
	 * This function pick a piece from the board and use it in winning :-P
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
	 * This function returns who is the opponent
	 */
	public int Opponent()
	{
		if(playerId == 1)
			return 2;
		return 1;
	}

	/**
	 * This method is used to select the highest piece
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
	 * This fuction checks if it is possible for the player to perform a quick win in horisional row
	 * hence prevent if from going to defends first and let the opponent win
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
					System.out.println("We have got to horizontal win");
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
	 * This function checks if it is possible for a player to win quick via 
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
					System.out.println("Got to winning row");
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
	 * preventing it from going to defend
	 * @param id player's id
	 * @param temp will takes the empty row coordinate that the player can win and pass it to end
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
		if(stack!=0 && !moved)
		{
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
		if(stack==0 &&!moved)
		{
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


	/* (non-Javadoc)
	 * @see Interface.PlayerModule#playerInvalidated(int)
	 */
	@Override
	public void playerInvalidated(int arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(); 

	}

}


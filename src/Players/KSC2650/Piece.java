package Players.KSC2650;

/**
 * 
 * @author Kemoy
 * 
 *
 */
//This is a class that handle pieces
public class Piece 
{
	private int playerId;
	private int pieceValue;
	/*----------------------------------------------
	 *                                             *
	 *      CONSTRUCTOR                            *
	 *                                             *                                                                                     
	 *---------------------------------------------*/
	
	/**
	 * Refers to the Size as pieceValue and PlayerID as ownerID
	 * @param size - stack Size (1, 2, 3, or 4)
	 * @param ownerID - Owner ID (1 - 2)
	 */
	public Piece(int playerId, int pieceValue)
	{
		this.playerId = playerId;
		this.pieceValue = pieceValue;
	}
	
	
	/*----------------------------------------------
	 *                                             *
	 *      Accessor Methods                       *
	 *                                             *                                                                                     
	 *---------------------------------------------*/
	/**
	 * return the player's id
	 * @return playerId
	 */
	public int getPlayerId()
	{
		return playerId;
	}
	
	/**
	 * return the pieceVlaue(size)
	 * @return pieceValue
	 */
	public int getPieceValue()
	{
		return pieceValue;
	}

}

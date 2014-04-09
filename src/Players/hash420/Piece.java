/*
* Piece.java
*
* Version:
* $Id:
*
* Revisions:
* $Log:
*
*/
package Players.hash420;

/**
 * Piece Class for RSA5330 (aka PlayerModule)
 * @author Ranbir
 *
 */
public class Piece {
	
	private int size;
	private int ownerID;
	
	/**
	 * Refers to the Size as sizeStack and PlayerID as ownerID
	 * @param size - stack Size (1, 2, 3, or 4)
	 * @param ownerID - Owner ID (1 - 2)
	 */
	public Piece(int size, int ownerID){
		this.size = size;
		this.ownerID = ownerID;
	}
	
	/**
	 * Get the Size (1, 2, 3, or 4)
	 * @return size
	 */
	public int getSize(){
		return this.size;
	}
	
	/**
	 * Get the PlayerID ( 1 or 2)
	 * @return OwnerID
	 */
	public int getOwnerID(){
		return this.ownerID;
	}

}

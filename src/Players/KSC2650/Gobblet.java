package Players.KSC2650;

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


/**

 * @author Ranbir Aulakh 
 * @author Kemoy Campbell
 */
public class Gobblet {
	
	private int ownerID;
	private int col;
	private int row;
	
	/**
	 * Refers to the Size as sizeStack and PlayerID as ownerID
	 * @param size - stack Size (1, 2, 3, or 4)
	 * @param ownerID - Owner ID (1 - 2)
	 */
	public Gobblet(int ownerID, int row, int col)
	{
		this.ownerID = ownerID;
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Get the PlayerID ( 1 or 2)
	 * @return OwnerID
	 */
	public int getOwnerID(){
		return this.ownerID;
	}
	
	/**
	 * Get the row
	 * return row
	 */
	public int getRow()
	{
		return this.row;
	}
	
	/**
	 * Get the col
	 * return col
	 */
	public int getCol()
	{
		return this.col;
	}
	
	/**
	 * Overridding the equals method
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Gobblet))
		{
			return false;
		}
		Gobblet t = (Gobblet)o;
		if((this.row==t.row)&& (this.col == t.col) && this.ownerID == t.ownerID)
		{
			return true;
		}
		else
		{
			return false;
		}
			
	}




}


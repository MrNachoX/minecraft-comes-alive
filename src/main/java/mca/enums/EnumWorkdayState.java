package mca.enums;

import radixcore.modules.RadixMath;

public enum EnumWorkdayState 
{
	MOVE_INDOORS(1),
	WANDER(2),
	WATCH_CLOSEST_ANYTHING(3),
	WATCH_CLOSEST_PLAYER(4),
	IDLE(5),
	WORK(6);
	
	private int id;
	
	EnumWorkdayState(int id)
	{
		this.id = id;
	}
	
	public static EnumWorkdayState getById(int id)
	{
		for (EnumWorkdayState state : EnumWorkdayState.values())
		{
			if (state.id == id)
			{
				return state;
			}
		}
		
		//Always default to IDLE to prevent issues loading previous worlds on other versions of MCA.
		//ID to remember workday state on these worlds should be zero, and would previously trigger
		//this return of null.
		return EnumWorkdayState.IDLE;
	}
	
	public int getId()
	{
		return id;
	}
	
	public static EnumWorkdayState getRandom()
	{
		int idToReturn = RadixMath.getNumberInRange(1, 5);
		return getById(idToReturn);
	}
}

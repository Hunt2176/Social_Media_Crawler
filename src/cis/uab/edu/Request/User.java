package cis.uab.edu.Request;

import java.util.ArrayList;

/**
 * User class to store details associated with each account
 */
public class User
{
	boolean checked = false;
	ArrayList<Integer> friends = new ArrayList<>();
	
	public boolean addFriend(Integer idOfFriend)
	{
		if (!friends.contains(idOfFriend))
		{
			friends.add(idOfFriend);
			return true;
		}
		return false;
		
	}
}

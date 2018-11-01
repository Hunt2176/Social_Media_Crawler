package cis.uab.edu.Http;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to hold IDs and their checked status for crawling
 */
public class IdQueue
{
	// HashMap for corresponding IDs and whether they have been checked
	// True = Has been checked; False = Has not been checked;
	private HashMap<Integer, Boolean> values = new HashMap<>();
	
	//This ArrayList will contain all Profile and ALL of their Info
	public ArrayList<Profile> profileList = new ArrayList<>();
	
	// 2 HashMaps for Dijkstra's data *line 128
	//ArrayList is faster**
	private HashMap<Profile, Integer> DistancesChecked = new HashMap<>();
	private HashMap<Profile, Integer> DistancesUnchecked = new HashMap<>();
	
	//temporary profile to add profiles and their IDs to the profileList
	//*done in "add" method on line 116
	private Profile tempProfile;
	//private edu.uab.cs334.Profile sourceProfile;
	
	
	/**
	 * Checks if the value passed is stored within the values
	 *
	 * @param idToCheck Value to check
	 * @return If value exists
	 */
	public Boolean contains(Integer idToCheck)
	{
		return (values.get(idToCheck) != null);
	}
	
	/**
	 * Returns the next value stored from the keys that has not been checked
	 *
	 * @return Next value that has not been checked
	 */
	public Integer nextUnchecked()
	{
		for (Integer key : values.keySet())
		{
			if (values.get(key).equals(false)) return key;
		}
		return null;
	}
	
	/**
	 * Runs through amount of values whose value has not been set to checked
	 *
	 * @return Number of remaining values
	 */
	public Integer remainingUnchecked()
	{
		Integer toReturn = 0;
		for (Integer value : values.keySet())
		{
			if (!values.get(value)) ++toReturn;
		}
		
		return toReturn;
	}
	
	/**
	 * Total stored number of keys
	 *
	 * @return Total number of keys
	 */
	public Integer totalCount()
	{
		return values.keySet().size();
	}
	
	/**
	 * Checks if there is still a value that is not marked as checked
	 *
	 * @return If a value still exists that is unchecked
	 */
	public Boolean hasUncheckedValues()
	{
		for (Integer key : values.keySet())
		{
			if (values.get(key).equals(false)) return true;
		}
		return false;
	}
	
	/**
	 * Sets value ID passed in to having been checked
	 *
	 * @param id value ID to check
	 * @return If the change was successful
	 */
	public Boolean setToChecked(Integer id)
	{
		if (!contains(id)) return false;
		values.put(id, true);
		return true;
	}
	
	public void setToUnchecked(Integer id)
	{
		if (!contains(id)) return;
		values.put(id, false);
	}
	
	/**
	 * Adds the value to the queue and sets it as unchecked
	 *
	 * @param id Value ID to set
	 * @return If adding was successful
	 */
	public Profile add(Integer id)
	{
		if (id == null)
		{
			return null;
		} else if (!contains(id))
		{
			values.put(id, false);
			
			//creates new profile from new ID and adds it
			//to general Profile Info Array
			//add all of the ID values to a profile in the profileList
			tempProfile = new Profile(id);
			profileList.add(tempProfile);
			
			return tempProfile;
		}
		return getProfile(id);
	}
	
	public Profile getProfile(Integer id)
	{
		if (id == null) return null;
		for (Profile profile : profileList)
		{
			if (profile.getID() == id) return profile;
		}
		return null;
	}
	
	public int Dijkstra(Profile from, Profile to)
	{
		//Scott attempt
		HashMap<Profile, Integer> dist = new HashMap<>();
		HashMap<Profile, Profile> path = new HashMap<>();
		ArrayList<Profile> queue = new ArrayList<>();
		
		for (Profile node : profileList)
		{
			queue.add(node);
			dist.put(node, (node.equals(from))? 0 : Integer.MAX_VALUE);
			path.put(node, null);
		}
		
		while (queue.size() > 0)
		{
			Profile using = null;
			for (Profile value : queue)
			{
				if (using == null || dist.get(value) < dist.get(using))
				{
					using = value;
				}
			}
			
			queue.remove(using);
			
			for (Profile child : using.getFriends())
			{
				if (!queue.contains(child)) continue;
				long b = (dist.get(using).longValue() - dist.get(child).longValue());
				long alt = dist.get(child).longValue() + b;
				if (alt < dist.get(child))
				{
					dist.put(child, (new Long(alt).intValue()));
					path.put(child, using);
				}
			}
		}
		Profile next = getProfile(to.getID());
		while (next != null)
		{
			queue.add(next);
			next = path.get(next);
		}
		return (queue.size() - 1 == 0)? -1 : queue.size() - 1;
		//Scott Attempt
	}
}
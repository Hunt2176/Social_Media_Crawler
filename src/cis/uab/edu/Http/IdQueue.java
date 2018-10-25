package cis.uab.edu.Http;

import java.util.HashMap;
import java.util.Set;

/**
 * Used to hold IDs and their checked status for crawling
 */
public class IdQueue
{
	/**
	 * HashMap for corresponding IDs and whether they have been checked
	 * True = Has been checked; False = Has not been checked;
	 */
	private HashMap<Integer, User> values = new HashMap<>();
	
	/**
	 * Checks if the value passed is stored within the values
	 * @param idToCheck Value to check
	 * @return If value exists
	 */
	public Boolean contains(Integer idToCheck)
	{
		return (values.get(idToCheck) != null);
	}
	
	/**
	 * Returns the next value stored from the keys that has not been checked
	 * @return Next value that has not been checked
	 */
	public Integer nextUnchecked()
	{
		for (Integer key: values.keySet())
		{
			if (!values.get(key).checked) return key;
		}
		return null;
	}
	
	/**
	 * Runs through amount of values whose value has not been set to checked
	 * @return Number of remaining values
	 */
	public Integer remainingUnchecked()
	{
		Integer toReturn = 0;
		for (Integer value: values.keySet())
		{
			if (!values.get(value).checked) ++toReturn;
		}
		
		return toReturn;
	}
	
	/**
	 * Total stored number of keys
	 * @return Total number of keys
	 */
	public Integer totalCount()
	{
		return values.keySet().size();
	}
	
	/**
	 * Checks if there is still a value that is not marked as checked
	 * @return If a value still exists that is unchecked
	 */
	public Boolean hasUncheckedValues()
	{
		for (Integer key: values.keySet())
		{
			if (!values.get(key).checked) return true;
		}
		return false;
	}
	
	/**
	 * Sets value ID passed in to having been checked
	 * @param id value ID to check
	 * @return If the change was successful
	 */
	public Boolean setToChecked(Integer id)
	{
		User profile;
		if ((profile = values.get(id)) != null)
		{
			profile.checked = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Adds the value to the queue and sets it as unchecked
	 * @param id Value ID to set
	 * @return If adding was successful
	 */
	public Boolean add(Integer id)
	{
		if (id == null)
		{
			return false;
		}
		else if (values.get(id) == null)
		{
			values.put(id, new User());
			return true;
		}
		return false;
	}
	
	/**
	 * Adds friend to specified User profile and returns if successful
	 * @param addTo ID to add to
	 * @param friendId ID of friend to add
	 * @return If the operation was successful
	 */
	public Boolean addFriend(Integer addTo, Integer friendId)
	{
		User profile;
		if ((addTo != null && friendId != null) && (profile = values.get(addTo)) != null)
		{
			return profile.addFriend(friendId);
		}
		return false;
	}
	
	/**
	 * Gets the key set used with the values
	 * @return List of values used to get values
	 */
	public Set<Integer> getKeySet()
	{
		return values.keySet();
	}
	
	/**
	 * Returns profile for specified key passed
	 * @param id Key to get from
	 * @return User or null
	 */
	User profileForId(Integer id)
	{
		if (id == null) return null;
		return values.get(id);
	}
	
}


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
	
	//This ArrayList will contain all Profiles and ALL of their Info
	private ArrayList<Profiles> ProfileList = new ArrayList<>();

	// 2 HashMaps for Dijkstra's data *line 
	//ArrayList is faster**
	private HashMap<Profiles, Integer> DistancesChecked = new HashMap<>();
	private HashMap<Profiles, Integer> DistancesUnchecked = new HashMap<>();
	
	//temporary profile to add profiles and their IDs to the ProfileList
	//*done in "add" method on line 119
	private Profiles tempProfile;
	//private edu.uab.cs334.Profiles sourceProfile;
	
	
	/**
	 * Checks if the value passed is stored within the values
	 * @param idToCheck Value to check
	 * @return If value exists
	 */
	Boolean contains(Integer idToCheck)
	{
		return (values.get(idToCheck) != null);
	}
	
	/**
	 * Returns the next value stored from the keys that has not been checked
	 * @return Next value that has not been checked
	 */
	Integer nextUnchecked()
	{
		for (Integer key: values.keySet())
		{
			if (values.get(key).equals(false)) return key;
		}
		return null;
	}
	
	/**
	 * Runs through amount of values whose value has not been set to checked
	 * @return Number of remaining values
	 */
	Integer remainingUnchecked()
	{
		Integer toReturn = 0;
		for (Integer value: values.keySet())
		{
			if (!values.get(value)) ++toReturn;
		}
		
		return toReturn;
	}
	
	/**
	 * Total stored number of keys
	 * @return Total number of keys
	 */
	Integer totalCount()
	{
		return values.keySet().size();
	}
	
	/**
	 * Checks if there is still a value that is not marked as checked
	 * @return If a value still exists that is unchecked
	 */
	Boolean hasUncheckedValues()
	{
		for (Integer key: values.keySet())
		{
			if (values.get(key).equals(false)) return true;
		}
		return false;
	}
	
	/**
	 * Sets value ID passed in to having been checked
	 * @param id value ID to check
	 * @return If the change was successful
	 */
	Boolean setToChecked(Integer id)
	{
		if (!contains(id)) return false;
		values.put(id, true);
		return true;
	}
	
	/**
	 * Adds the value to the queue and sets it as unchecked
	 * @param id Value ID to set
	 * @return If adding was successful
	 */
	Boolean add(Integer id)
	{
		if (id == null)
		{
			return false;
		}
		else if (!contains(id))
		{
			values.put(id, false);
			
			//creates new profile from new ID and adds it 
			//to general Profile Info Array
			tempProfile = new Profiles();
			//add all of the ID values to a profile in the ProfileList
			tempProfile.setID(id);
			ProfileList.add(tempProfile);
			
			return true;
		}
		return false;
	}
	
	void Dijkstra(Profiles source) {
	//void Dijkstra(HashMap map, edu.uab.cs334.Profiles source) {

		//set the Distance values in the DistancesChecked HashMap to null (*or whatever value that shows the distance is undetermined. ex: -1, Undefined, infinite, etc)
		//keys are the Profiles, values are the Distances
		
		for (int i=0; i<ProfileList.size(); i++) {
			DistancesChecked.put(ProfileList.get(i), null);
		}
		
		//do the same for the DistancesUnchecked HashMap
		for (int i=0; i<ProfileList.size(); i++) {
			DistancesUnchecked.put(ProfileList.get(i), null);
		}
		
		//set source profile's distance to itself to 0
		DistancesChecked.put(source, 0);
		
		//remove the source profile from the DistancesUnchecked HashMap
		DistancesUnchecked.remove(source);
		
		//while not all the documented profiles are checked, Dijkstra's algorithm continues...
		while (!((DistancesChecked.size()) == 0)) {
			
			//temporary current profile being checked
			Profiles tempProfile;
			
			//temporary current profile's friends
			ArrayList<Profiles> tempFriends;
			
			//start off by setting tempProfile to the source profile
			tempProfile = source;
			
			//get the friends of the currently checked profile
			tempFriends = tempProfile.getFriends();
			
			//smallest distance to the source profile (*start off High)
			int minToSource = 9999;
			
			//closest profile to the source
			Profiles minProfile = tempFriends.get(0);
			
			//get the smallest distance/profile to the source
			for (int k=0; k<tempFriends.size(); k++) {
				
				//if a profile is found with a smaller distance to the source, it becomes the minimum
				if (tempFriends.get(k).getDistance()<minToSource) {
					minToSource = tempFriends.get(k).getDistance();
					minProfile = tempFriends.get(k);
				}
			}
			
			/*
			 * to work on weights later
			 */
			//temporary weight of the current profile;
			int profileWeight = tempProfile.getWeight();
			
			//find the friends of the source profile and iterate through them...
			for (int j=0; j<tempFriends.size(); j++) {
				
				//new distance to source
				int alternateDistance;
				
				//calculate new distanceToSource
				alternateDistance = minProfile.getDistance() + (minToSource - tempFriends.get(j).getDistance());
				
				//if a profile is found with a smaller distance to the source, it becomes the minimum
				if (alternateDistance<tempFriends.get(j).getDistance()) {
					alternateDistance = tempFriends.get(j).getDistance();
					//this becomes the new closest profile
					minProfile = tempFriends.get(j);
				}			
				
				//add the now checked profile to the DistancesChecked HashMap and remove it from DistancesUnchecked
				//DistancesChecked.put(tempProfile.getFriends().get(j), distanceToSource);
				//DistancesUnchecked.remove(tempProfile.getFriends().get(j));
				
				
				/*
				 * things to do:
				 */
				//add the shortest paths from a profile to the source to each profile's info
				//add profile weight in to the process
				//take in read values to their individual profiles from the crawling the site
				//add Dijkstra's into the main file
				//finish up things in Profile class
			}
		}
	}
}
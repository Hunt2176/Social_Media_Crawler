package cis.uab.edu.Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Used to hold IDs and their checked status for crawling
 */
public class IdQueue
{
	// HashMap for corresponding IDs and whether they have been checked
	// True = Has been checked; False = Has not been checked;
	private HashMap<Integer, Boolean> values = new HashMap<>();
	
	//This ArrayList will contain all Profiles and ALL of their Info
	public ArrayList<Profiles> ProfileList = new ArrayList<>();
	
	private HashMap<Profiles, Integer> ProfileHash = new HashMap<>();
	
	// 2 HashMaps for Dijkstra's data *line 128
	//ArrayList is faster**
	private HashMap<Profiles, Integer> DistancesChecked = new HashMap<>();
	private HashMap<Profiles, Integer> DistancesUnchecked = new HashMap<>();
	
	
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
	public Profiles add(Integer id)
	{
		if (id == null)
		{
			return null;
		} else if (!contains(id))
		{
			values.put(id, false);
			
			//creates new profile from new ID and adds it
			//to general Profile Info Array
			//add all of the ID values to a profile in the ProfileList
			Profiles tempProfile = new Profiles(id);
			ProfileList.add(tempProfile);
			ProfileHash.put(tempProfile, tempProfile.getID());
			return tempProfile;
		}
		return getProfile(id);
	}
	
	public Profiles getProfile(Integer id)
	{
		if (id == null) return null;
		for (Profiles profile : ProfileList)
		{
			if (profile.getID() == id) return profile;
		}
		return null;
	}
	
	public int Dijkstra(Profiles from, Profiles to)
	{
		
		//set the Distance values in the DistancesChecked HashMap to null (*or whatever value that shows the distance is undetermined. ex: -1, Undefined, infinite, etc)
		//keys are the Profiles, values are the Distances
		
		PriorityQueue<Profiles> profileQueue = new PriorityQueue<>((Profiles a, Profiles b) -> Integer.compare(a.getDistance(), b.getDistance()));
		
		Profiles tempProfile;
		
		for (int i = 0; i < ProfileList.size(); i++)
		{
			Profiles p = ProfileList.get(i);
			if (p == from)
			{
				p.setDistance(0);
			} else
			{
				p.setDistance(Integer.MAX_VALUE);
			}
			profileQueue.add(p);
		}
		
		int tempDistance = 0;
		tempProfile = from;
		ArrayList<Profiles> tempFriends = tempProfile.getFriends();
		
		while (profileQueue.size() != 0)
		{
			for (int j = 0; j < tempFriends.size(); j++)
			{
				tempFriends.get(j).setDistance(tempDistance++);
				
				for (int l=0; l<tempProfile.getShortestPath().size();){
					tempFriends.get(j).getShortestPath().add(tempProfile.getShortestPath().get(l));
				}
				tempFriends.get(j).getShortestPath().add(tempProfile);
				
				//Sets the final distance to the source
				tempFriends.get(j).calculateFinalDistance();
		}
		
		profileQueue.poll();
		tempProfile = profileQueue.peek();
		tempDistance++;
			}
			return to.getShortestPath().size();
		}
	}
	







//		for (int i=0; i<ProfileList.size(); i++) {
//			DistancesChecked.put(ProfileList.get(i), -1);
//		}
//
//		//do the same for the DistancesUnchecked HashMap
//		for (int i=0; i<ProfileList.size(); i++) {
//			DistancesUnchecked.put(ProfileList.get(i), -1);
//		}
//
//		//set from profile's distance to itself to 0
//		DistancesChecked.put(from, 0);
//
//		//remove the from profile from the DistancesUnchecked HashMap
//		DistancesUnchecked.remove(from);
//
//		//start off by setting tempProfile to the from profile
//		//Profiles tempProfile = from;
//
//		//while not all the documented profiles are checked, Dijkstra's algorithm continues...
//		while (DistancesUnchecked.size() > 0)
//		{
//			//temporary current profile's friends
//			//ArrayList<Profiles> tempFriends;
//
//			//get the friends of the currently checked profile
//			tempFriends = tempProfile.getFriends();
//
//			//smallest distance to the from profile (*start off High)
//			int minToSource = Integer.MAX_VALUE;
//
//			//closest profile to the from
//			Profiles minProfile = tempFriends.get(0);
//
////			//get the smallest distance/profile to the from
////			for (int k=0; k<tempFriends.size(); k++) {
////
////				//if a profile is found with a smaller distance to the from, it becomes the minimum
////				if (tempFriends.get(k).getDistance()<minToSource) {
////					minToSource = tempFriends.get(k).getDistance();
////					minProfile = tempFriends.get(k);
////				}
////
////			/*
////			 * to work on weights later
////			 */
////			//temporary weight of the current profile;
////			int profileWeight = tempProfile.getWeight();
//
//			//find the friends of the from profile and iterate through them...
//			for (int j=0; j<tempFriends.size(); j++)
//			{
//
//				//new distance to from
//				int alternateDistance;
//
//				//calculate new distanceToSource
//				alternateDistance = minProfile.getDistance() + (minToSource - tempFriends.get(j).getDistance());
//
////				//if a profile is found with a smaller distance to the from, it becomes the minimum
////				if (alternateDistance<tempFriends.get(j).getDistance()) {
////					alternateDistance = tempFriends.get(j).getDistance();
////					//this becomes the new closest profile
////					minProfile = tempFriends.get(j);
////				}
//
//				//now we will add the profile whose friends we are currently checking AND all of the Profiles from its ShortestPath Array
//				//to each of their ShortestPath arrays to document their shortest paths to the from profile
//
//				//add tempProfile's ShortestPath Profiles
//				for (int l=0; l<tempProfile.getShortestPath().size(); l++) {
//					tempFriends.get(j).getShortestPath().add(tempProfile.getShortestPath().get(l));
//				}
//
//				//now add the tempProfile itself
//				tempFriends.get(j).getShortestPath().add(tempProfile);
//
//				//add the now checked profile to the DistancesChecked HashMap and remove it from DistancesUnchecked
////				DistancesChecked.put(tempProfile.getFriends().get(j), tempProfile.getFriends().get(j).getDistance());
////				DistancesUnchecked.remove(tempProfile.getFriends().get(j));
//				DistancesUnchecked.remove(tempProfile);
//
//
				/*
				 * things to do:
				 */
				//add the shortest paths from a profile to the from to each profile's info
				//add profile weight in to the process
				//take in read values to their individual profiles from the crawling the site
				//add Dijkstra's into the main file
				//finish up things in Profile class
//			}
//
//			//tempProfile = tempProfile.getFriends().get(0);
//
//		}
	///	int toReturn  = to.getShortestPath().size();
		//ProfileList.forEach((profile) -> profile.getShortestPath().clear());
		//return toReturn;
	//}
//}
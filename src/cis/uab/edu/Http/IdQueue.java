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
	
	//This ArrayList will contain all Profile and ALL of their Info
	public ArrayList<Profile> ProfileList = new ArrayList<>();

	// 2 HashMaps for Dijkstra's data *line 128
	//ArrayList is faster**
	private HashMap<Profile, Integer> DistancesChecked = new HashMap<>();
	private HashMap<Profile, Integer> DistancesUnchecked = new HashMap<>();
	
	//temporary profile to add profiles and their IDs to the ProfileList
	//*done in "add" method on line 116
	private Profile tempProfile;
	//private edu.uab.cs334.Profile sourceProfile;
	
	
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
			if (values.get(key).equals(false)) return key;
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
			if (!values.get(value)) ++toReturn;
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
			if (values.get(key).equals(false)) return true;
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
	 * @param id Value ID to set
	 * @return If adding was successful
	 */
	public Profile add(Integer id)
	{
		if (id == null)
		{
			return null;
		}
		else if (!contains(id))
		{
			values.put(id, false);
			
			//creates new profile from new ID and adds it
			//to general Profile Info Array
			//add all of the ID values to a profile in the ProfileList
			tempProfile = new Profile(id);
			ProfileList.add(tempProfile);
			
			return tempProfile;
		}
		return getProfile(id);
	}
	
	public Profile getProfile(Integer id)
	{
		if (id == null) return null;
		for (Profile profile: ProfileList)
		{
			if (profile.getID() == id) return profile;
		}
		return null;
	}

	class Node
	{
		ArrayList<Node> parents = new ArrayList<>();
		ArrayList<Node> children = new ArrayList<>();
		String name = "Profile";
		Integer id;
		Integer distance = Integer.MIN_VALUE;
		Node(int id)
		{
			this.name = this.name + " " + id;
			this.id = id;
		}

		Node(Profile profile)
		{
			this.name = this.name + " " + profile.getID();
			this.id = profile.getID();
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public int Dijkstra(Profile from, Profile to) {


		//Scott attempt

		PriorityQueue<Profile> profileQueue = new PriorityQueue<>((Profile a, Profile b) -> Integer.compare(a.getDistance(), b.getDistance()));
		HashMap<Profile, Profile> dist = new HashMap<>();
		HashMap<Profile, Profile> path = new HashMap<>();

//		for (int i = 0; i < ProfileList.size(); i++) {
//			Profile p = ProfileList.get(i);
//			if (p == from) {
//				p.setDistance(0);
//			} else {
//				p.setDistance(Integer.MAX_VALUE);
//			}
//			profileQueue.add(p);
//		}
//		Profile current;
//		ArrayList<Profile> friendQueue = new ArrayList();
//		while((current = profileQueue.poll()) != null){
//			current.getFriends().forEach((friend) -> {
//				if (!friendQueue.contains(friend)) friendQueue.add(friend);
//			});
//			for(Profile profile: friendQueue){
//
//			}
//			friendQueue.forEach(profileQueue::remove);
//			profileQueue.addAll(friendQueue);
//			friendQueue.clear();
//		}
		HashMap<Integer, Node> nodeMap = new HashMap<>();
		HashMap<Node, Boolean> checkedMap = new HashMap<>();
		Profile current;
		int index = 0;
		while (index < ProfileList.size()) {
			current = ProfileList.get(index);
			Node currentNode = nodeMap.get(current.getID());

			if (currentNode == null)
			{
				nodeMap.put(current.getID(), new Node(current));
				currentNode = nodeMap.get(current.getID());
			}
			if (current == from) currentNode.distance = 0;

			for (Profile friend : current.getFriends()) {
				Node childNode = nodeMap.get(friend.getID());
				if (childNode == null) {
					childNode = new Node(friend.getID());
					nodeMap.put(friend.getID(), childNode);
				}
				childNode.parents.add(nodeMap.get(current.getID()));
				nodeMap.get(current.getID()).children.add(childNode);
			}
			index += 1;
		}
		Node root = null;
		for (Node node: nodeMap.values())
		{
			if (node.distance == 0) root = node;
		}
		nodeMap.forEach((key, value) -> checkedMap.put(value, false));

		try
		{
			Node using = root;
			while (using != null)
			{
				for (Node child: using.children)
				{
					if (!checkedMap.get(child))
					{
						child.distance = using.distance + 1;
						checkedMap.put(child, true);
					}
				}
			}

		} catch (NullPointerException error)
		{
			error.printStackTrace();
		}





		System.out.println("Butts");


		return 0;
		//Scott Attempt
	}
}
package cis.uab.edu.Http;

import java.util.ArrayList;

public class Profiles {
	private int Weight = 1;
	private int ID;
	private ArrayList<Profiles> Friends = new ArrayList<>();
	private boolean Checked = false;
	private String Name = "";
	//distance between Profiles
	private int Distance = 1;
	//Final distance to source profile
	private int FinalDistance = 0;
	//path to profile tree root
	private ArrayList<Profiles> ShortestPath = new ArrayList<>();
	
	public Profiles(int id)
	{
		ID = id;
	}

	Profiles(String name, int id, int weight, boolean b, int dist, ArrayList<Profiles> path, ArrayList<Profiles> array){
		setName(name);
		Weight = weight;
		ID = id;
		setFriends(array);
		setChecked(false);
		}
		
		//finish
		public String toString() {
			return "Profile " + ID;
		}
	
	public void addFriend(Profiles idOfFriend)
		{
			if (idOfFriend != null && idOfFriend.getID() != this.getID()) Friends.add(idOfFriend);
		}
	
	public int getWeight() {
			return Weight;
		}
	
	public void setWeight(int w) {
			Weight = w;
		}
	
	public int getID() {
			return ID;
		}
	
	public void setID(int num) {
			ID = num;
		}
		
	public ArrayList<Profiles> getFriends() {
			return Friends;
		}

		public void setFriends(ArrayList<Profiles> friends) {
			Friends = friends;
		}

		public boolean isChecked() {
			return Checked;
		}

		public void setChecked(boolean checked) {
			Checked = checked;
		}

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public int getDistance() {
			return Distance;
		}

		public void setDistance(int distance) {
			Distance = distance;
		}

		public ArrayList<Profiles> getShortestPath() {
			return ShortestPath;
		}

		public void setShortestPath(ArrayList<Profiles> path) {
			ShortestPath = path;
		}

		public int getFinalDistance() {
			return FinalDistance;
		}

		public void setFinalDistance(int finalDistance) {
			FinalDistance = finalDistance;
		}
}

package cis.uab.edu.Http;

import java.util.ArrayList;

public class Profiles {
	private int Weight;
	private int ID;
	private ArrayList<Profiles> Friends;
	private boolean Checked;
	private String Name;
	//distance between Profiles
	private int Distance;
	//Final distance to source profile
	private int FinalDistance;
	//path to profile tree root
	private ArrayList<Profiles> Path;
	
	Profiles() {
		
	}
	
	Profiles(int id) {
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
			return "Profile " + ID + " is " + FinalDistance + " from the source profile.";
		}
		
		void addFriend(Profiles idOfFriend)
		{
			Friends.add(idOfFriend);
		}
		
		int getWeight() {
			return Weight;
		}
		
		void setWeight(int w) {
			Weight = w;
		}
		 
		int getID() {
			return ID;
		}
		
		void setID(int num) {
			ID = num;
		}
		ArrayList<Profiles> getFriends() {
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

		public ArrayList<Profiles> getPath() {
			return Path;
		}

		public void setPath(ArrayList<Profiles> path) {
			Path = path;
		}

		public int getFinalDistance() {
			return FinalDistance;
		}

		public void setFinalDistance(int finalDistance) {
			FinalDistance = finalDistance;
		}
}

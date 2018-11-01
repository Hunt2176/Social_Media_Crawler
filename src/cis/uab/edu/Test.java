package cis.uab.edu;

import cis.uab.edu.Http.IdQueue;
import cis.uab.edu.Http.Profile;

import java.util.ArrayList;
import java.util.Arrays;

public class Test
{
//	static void LoginJson() throws IOException
//	{
//		Socket socket = new Socket("odin.cs.uab.edu", 3001);
//		PrintWriter writer = new PrintWriter(socket.getOutputStream());
//		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//		StringBuilder builder = new StringBuilder();
//		String json = "{ \"email\": \"lforbus@uab.edu\", \"password\": \"w7BR2PgZ\", \"grant_type\": \"password\"}";
//		builder.append("POST /oauth/token HTTP/1.1\r\n");
//		builder.append("Host: odin.cs.uab.edu:3001\r\n");
//		builder.append("Content-Type: application/json\r\n");
//		builder.append("Content-Length: " + json.length());
//		builder.append("\r\n\r\n");
//		builder.append(json);
//
//
//		writer.print(builder.toString());
//		writer.flush();
//		String line;
//		while((line = reader.readLine()) != null)
//		{
//			System.out.println(line);
//		}
//	}
	
	public static void main(String[] args)
	{
		ArrayList<Profile> profiles = new ArrayList<>();
		Profile profile1 = new Profile(1);
		Profile profile2 = new Profile(2);
		Profile profile3 = new Profile(3);
		Profile profile4 = new Profile(4);
		Profile profile5 = new Profile(5);
		Profile profile6 = new Profile(6);
		
		profile1.addFriend(profile2);
		profile1.addFriend(profile3);
		profile2.addFriend(profile3);
		profile3.addFriend(profile4);
		profile4.addFriend(profile5);
		profile5.addFriend(profile6);
		profile3.addFriend(profile6);
		
		profiles.addAll(Arrays.asList(
				profile1,
				profile2,
				profile3,
				profile4,
				profile5,
				profile6
		));
		
		IdQueue testing = new IdQueue();
		testing.profileList = profiles;
		testing.Dijkstra(testing.getProfile(1), testing.getProfile(6));
	}
	
	
}

class PathFinder
{
	private ArrayList<ArrayList<Integer>> pathsTo = new ArrayList<>();
	private ArrayList<Integer> checked = new ArrayList<>();
	
	boolean isFriendOfRecursive(Profile profile, Integer idToFind)
	{
		for (Profile friend : profile.getFriends())
		{
			if (friend.getID() == idToFind) return true;
			if (!checked.contains(friend.getID()))
			{
				checked.add(friend.getID());
				if (isFriendOfRecursive(friend, idToFind)) return true;
			}
			
		}
		return false;
	}
	
	boolean isFriendOf(Profile profile, Integer idToFind)
	{
		checked = new ArrayList<>();
		return isFriendOfRecursive(profile, idToFind);
	}
	
	void buildPaths(Profile profile, Integer idToFind)
	{
		if (!isFriendOf(profile, idToFind)) return;
		
		for (Profile friend : profile.getFriends())
		{
			ArrayList<Integer> path = new ArrayList<>();
			
			Profile next = friend;
			if (!isFriendOf(next, idToFind)) continue;
			path.add(profile.getID());
			path.add(next.getID());
			while ((next = nextInPath(next, idToFind)) != null)
			{
				if (next.getID() == idToFind || path.contains(next.getID())) break;
				path.add(next.getID());
				
			}
			pathsTo.add(path);
		}
		
	}
	
	Profile nextInPath(Profile profile, Integer idToFind)
	{
		if (profile.getID() == idToFind) return null;
		for (Profile friend : profile.getFriends())
		{
			if (isFriendOf(friend, idToFind)) return friend;
		}
		return null;
	}
	
	int count(int to, int from, IdQueue fromQueue)
	{
		Profile toNode = fromQueue.getProfile(to);
		Profile fromNode = fromQueue.getProfile(from);
		
		if (toNode == null || fromNode == null) return -1;
		if (!isFriendOf(fromNode, toNode.getID())) return -1;
		buildPaths(fromNode, toNode.getID());
		
		int toReturn = Integer.MAX_VALUE;
		for (ArrayList paths : pathsTo)
		{
			if (paths.size() < toReturn) toReturn = paths.size();
		}
		return (toReturn == Integer.MAX_VALUE) ? -1 : toReturn;
	}
}

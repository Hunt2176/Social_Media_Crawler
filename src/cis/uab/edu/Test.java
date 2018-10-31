package cis.uab.edu;

import cis.uab.edu.Http.IdQueue;
import cis.uab.edu.Http.Profiles;

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
		ArrayList<Profiles> profiles = new ArrayList<>();
		Profiles profiles1 = new Profiles(1);
		Profiles profiles2 = new Profiles(2);
		Profiles profiles3 = new Profiles(3);
		Profiles profiles4 = new Profiles(4);
		Profiles profiles5 = new Profiles(5);
		Profiles profiles6 = new Profiles(6);
		
		profiles1.addFriend(profiles2);
		profiles1.addFriend(profiles3);
		profiles2.addFriend(profiles3);
		profiles3.addFriend(profiles4);
		profiles4.addFriend(profiles5);
		profiles5.addFriend(profiles6);
		
		IdQueue testing = new IdQueue();
		testing.ProfileList.addAll(Arrays.asList(
				profiles1,
				profiles2,
				profiles3,
				profiles4,
				profiles5,
				profiles6
		));
		int x = testing.Dijkstra(profiles1, profiles6);
		System.out.println(x);
	}
	
	
}

class PathFinder
{
	private ArrayList<ArrayList<Integer>> pathsTo = new ArrayList<>();
	private ArrayList<Integer> checked = new ArrayList<>();
	
	boolean isFriendOfRecursive(Profiles profile, Integer idToFind)
	{
		for (Profiles friend: profile.getFriends())
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
	
	boolean isFriendOf(Profiles profiles, Integer idToFind)
	{
		checked = new ArrayList<>();
		return isFriendOfRecursive(profiles, idToFind);
	}
	
	void buildPaths(Profiles profiles, Integer idToFind)
	{
		if (!isFriendOf(profiles, idToFind)) return;
		
		for (Profiles friend: profiles.getFriends())
		{
			ArrayList<Integer> path = new ArrayList<>();
			
			Profiles next = friend;
			if (!isFriendOf(next, idToFind)) continue;
			path.add(profiles.getID());
			path.add(next.getID());
			while ((next = nextInPath(next, idToFind)) != null)
			{
				if (next.getID() == idToFind || path.contains(next.getID())) break;
				path.add(next.getID());
				
			}
			pathsTo.add(path);
		}
		
	}
	
	Profiles nextInPath(Profiles profile, Integer idToFind)
	{
		if (profile.getID() == idToFind) return null;
		for (Profiles friend: profile.getFriends())
		{
			if (isFriendOf(friend, idToFind)) return friend;
		}
		return null;
	}
	
	int count(int to, int from, IdQueue fromQueue)
	{
		Profiles toNode = fromQueue.getProfile(to);
		Profiles fromNode = fromQueue.getProfile(from);
		
		if (toNode == null || fromNode == null) return -1;
		if (!isFriendOf(fromNode, toNode.getID())) return -1;
		buildPaths(fromNode, toNode.getID());
		
		int toReturn = Integer.MAX_VALUE;
		for (ArrayList paths: pathsTo)
		{
			if (paths.size() < toReturn) toReturn = paths.size();
		}
		return (toReturn == Integer.MAX_VALUE)? -1 : toReturn;
	}
}

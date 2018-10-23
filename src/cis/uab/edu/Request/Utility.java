package cis.uab.edu.Request;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Utility
{
	static void writeFriends(IdQueue queue)
	{
		HashMap<Integer, Integer> counts = new HashMap<>();
		queue.getKeySet().forEach(index -> {
			User toUse = queue.profileForId(index);
			if (!counts.containsKey(toUse.friends.size()))
			{
				counts.put(toUse.friends.size(), 1);
			}
			else
			{
				counts.put(toUse.friends.size(), counts.get(toUse.friends.size()) + 1);
			}
		});
		ArrayList<FriendPair> pairsToPrint = new ArrayList<>();
		counts.forEach((key, value) -> pairsToPrint.add(new FriendPair(key, value)));
		pairsToPrint.sort(Comparator.comparingInt(o -> o.id));
		try {
			File file = new File("degree-dist.txt");
			if (!file.exists()) file.createNewFile();
			if (file.canWrite())
			{
				PrintWriter writer = new PrintWriter(file);
				pairsToPrint.forEach(pair -> {
					writer.println(pair.id + "\t" + pair.friend);
				});
				writer.close();
				
			}
			else
			{
				System.err.println("Could not write to file");
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}
	
	static void writeLinesWithIdInfo(String usrname, ArrayList<String> tokens)
	{
		try
		{
			File file = new File("secret_flags");
			
			
			StringBuilder toWrite = new StringBuilder();
			if (file.exists())
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				Files.readAllLines(file.toPath()).forEach(line -> toWrite.append(line).append("\n"));
				Files.readAllLines(file.toPath()).forEach(System.err::println);
				reader.close();
			}
			else
			{
				file.createNewFile();
			}
			
			if (file.canWrite())
			{
				PrintWriter writer = new PrintWriter(new FileOutputStream(file));
				toWrite.append(usrname + "\n");
				(tokens).forEach(token -> toWrite.append(token + "\n"));
				writer.println(toWrite.toString());
				writer.close();
			}
			else
			{
				System.err.println("Error writing secret_flags.txt file.");
			}
			
			
			
		}
		catch (IOException error)
		{
			error.printStackTrace();
		}
	}
}

package cis.uab.edu;

import cis.uab.edu.Http.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
	private static String address = "";
	private static int port = -1;
	private static String email = "";
	private static String password = "";
	private static Integer graphID = null;
	
	private static HttpSession session = null;
	private static ArrayList<String> flags = new ArrayList<>();
	private static IdQueue queue = new IdQueue();
	private static JsonMapper loginMap = new JsonMapper();
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 5)
		{
			for (int index = 0; index < args.length; index++)
			{
				try
				{
					switch (index)
					{
						case 0: address = args[0];
							break;
						case 1: port = Integer.valueOf(args[1]);
							break;
						case 2: email = args[2]; if (!email.contains("@uab.edu")) throw new Exception();
							break;
						case 3: password = args[3];
							break;
						case 4: graphID = Integer.valueOf(args[4]);
							break;
						default: throw new Exception("Invalid Parameter: " + args[index]);
					}
				} catch (Exception error)
				{
					String item = "";
					switch (index)
					{
						case 0: item = "address";
							break;
						case 1: item = "port number";
							break;
						case 2: item = "email";
							break;
						case 3: item = "password";
							break;
						case 4: item = "Graph ID";
							break;
					}
					System.err.println(args[index] + " is not a valid " + item + ".");
					System.exit(1);
				}
			}
		}
		else
		{
			System.err.println("Too few arguments.");
			System.exit(1);
		}
		
		session = new HttpSession(address, port, new HttpHeader(HttpMethod.POST, "/oauth/token"));
		
		session.addHeaderValue("accept", "application/json");
		session.addHeaderValue("connection", "close");
		session.addMethodHeader(HttpMethod.POST, "Content-Type", "application/json");
		
		session.addMatcher(HttpLineMatcher.containMatcher("token", (line) ->
		{
			String token = line.split("_token\":\"")[1].split("\",")[0];
			session.addHeaderValue("authorization", "bearer " + token);
		}));
		session.addMatcher(HttpLineMatcher.containMatcher("SECRET FLAG", (line) ->
		{
			String flag = line.split("\"SECRET FLAG: ")[1].split("\"},")[0];
			Pattern pattern = Pattern.compile("(\\w|\\d)+");
			Matcher matcher = pattern.matcher(flag);
			if (matcher.find())
			{
				flags.add(matcher.group(0));
				System.out.println(matcher.group(0));
			}
		}));
		session.addMatcher(HttpLineMatcher.regexMatcher("HTTP/1.1 \\d+ OK", (line) ->
		{
			if (line.contains("500"))
			{
				System.err.println("Unrecoverable server response: 500");
				System.exit(1);
			}
			
		}));
		session.addMatcher(HttpLineMatcher.containMatcher("Unauthorized", (line) -> {
			try
			{
				if (!session.isUsingOnSendComplete()) throw new Exception("Failed Authorization");
				login(true);
			} catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}));
		
		loginMap.putString("email", email);
		loginMap.putString("password", password);
		loginMap.putString("grant_type", "password");
		
		login(false);
		visit(null);
		Integer next;
		while ((next = queue.nextUnchecked()) != null && flags.size() < 5)
		{
			visit(next.toString());
			queue.setToChecked(next);
		}
	}
	
	private static void visit(String id) throws Exception
	{
		if (id != null)
		{
			session.setHeaderPath("/api/v1/beets/" + id);
			session.send();
			if (hasChallenge(id)) return;
			session.setHeaderPath("/api/v1/friends/" + id);
			session.send();
			if (hasChallenge(id)) return;
		} else
		{
			session.setHeaderPath("/api/v1/crawl_sessions/" + graphID);
			session.send("");
			session.setHeaderPath("/api/v1/graphs/random_people");
			session.send();
		}
		
		Profile using;
		try
		{
			if (id == null) throw new NumberFormatException();
			using = queue.getProfile(Integer.valueOf(id));
			
		} catch (NumberFormatException error)
		{
			using = null;
		}
		
		
		String json = session.getLastResponse().getJson();
		json = json
				.replaceAll("\"people\":", "")
				.replaceAll("(\\{|\\}|\\[|\\])", "")
				.replaceAll("\"friends\":", "")
				.replaceAll("\"length\":\\d+", "")
				.replaceAll(",", " ")
				.replaceAll("\"uid\":", "")
				.trim();
		
		for (String line : (json.split(" ")))
		{
			try
			{
				queue.add(Integer.valueOf(line));
				if (using != null) using.addFriend(queue.getProfile(Integer.valueOf(line)));
				
			} catch (NumberFormatException ignored)
			{
			}
		}
	}
	
	private static boolean hasChallenge(String id) throws Exception
	{
		if (session.getLastResponse().getResponseCode() == 302)
		{
			challenge();
			queue.setToUnchecked(Integer.valueOf(id));
			return true;
		}
		return false;
	}
	
	private static void challenge() throws Exception
	{
		session.setHeaderPath("/api/v1/challenges/");
		session.send();
		if (session.getLastResponse().getJson() != null)
		{
			String json = session.getLastResponse().getJson();
			json = json.split("\"to\":")[1];
			try
			{
				Profile to = queue.getProfile(Integer.valueOf(json.split(",")[0]));
				Profile from = queue.getProfile(Integer.valueOf(json.split("\"from\":")[1].split(",")[0]));
				JsonMapper mapper = new JsonMapper();
				
				int result;
				if (to == null || from == null)
				{
					result = -1;
				}
				else
				{
					result = queue.Dijkstra(from, to);
				}
				mapper.putInt("distance", result);
				
				session.send(mapper.toString());
				if (session.getLastResponse().getJson().contains("failure"))
				{
					//Call for getting bruteforce on error
					challengeBruteForce();
//					System.err.println("Incorrect response was given for challenge. Cannot Recover. Exiting.");
//					System.exit(1);
				}
				
			} catch (NumberFormatException error)
			{
				throw error;
			}
			session.printResponse(false);
			
		}
		
	}
	
	private static boolean login(boolean relog) throws Exception
	{
		if (relog)
		{
			session.setOnSendComplete((it) ->
			{
				HttpRequest lastRequest = it.getLastRequest();
				try
				{
					session.setHeaderPath("/oauth/token");
					session.send(loginMap.toString());
					it.send(it.getLastRequest().getHttpHeader());
				} catch (Exception err)
				{
					System.err.println("Unable to reauthorize");
					err.printStackTrace();
					System.exit(1);
				}
				
			});
			return true;
		}
		else
		{
			session.setHeaderPath("/oauth/token");
			session.send(loginMap.toString());
			return (session.getLastResponse().getResponseCode() == 200);
		}
	}
	
	private static int challengeBruteForce() throws Exception
	{
		int count = -1;
		while (session.getLastResponse().getJson() != null && session.getLastResponse().getJson().contains("failure"))
		{
			JsonMapper mapper = new JsonMapper();
			mapper.putInt("Distance", count);
			session.send("{\"distance\": " + count + "}");
			if (session.getLastResponse().getJson().contains("failure"))
			{
				count = (count == -1) ? 1 : count + 1;
			}
		}
		return count;
	}
	
}

package cis.uab.edu;
import cis.uab.edu.Http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//"email": "lforbus@uab.edu"
//"password": "w7BR2PgZ"

public class Main
{
	final static String email = "lforbus@uab.edu";
	final static String password = "w7BR2PgZ";
	
	static HttpSession session = new HttpSession("odin.cs.uab.edu", 3001, new HttpHeader(HttpMethod.POST, "/oauth/token"));
	static ArrayList<String> flags = new ArrayList<>();
	static IdQueue queue = new IdQueue();
	
    public static void main(String[] args) throws Exception
    {
    	
    	session.addHeaderValue("accept", "application/json");
    	session.addHeaderValue("connection", "close");
    	session.addMethodHeader(HttpMethod.POST, "Content-Type", "application/json");
    	
    	session.addMatcher(HttpLineMatcher.containMatcher("token\":",  (line) -> {
    		String token = line.split("_token\":\"")[1].split("\",")[0];
    		session.addHeaderValue("authorization", "bearer " + token);
	    }));
    	session.addMatcher(HttpLineMatcher.containMatcher("SECRET FLAG:", (line) ->{
    		String flag = line.split("\"SECRET FLAG: ")[1].split("\"},")[0];
		    Pattern pattern = Pattern.compile("(\\w|\\d)+");
		    Matcher matcher = pattern.matcher(flag);
		    if (matcher.find())
		    {
			    flags.add(matcher.group(0));
		    }
    		
	    }));
    	
    	JsonMapper mapper = new JsonMapper();
    	mapper.putString("email", email);
    	mapper.putString("password", password);
    	mapper.putString("grant_type", "password");
    	
    	session.send(mapper.toString());
    	
    	visit(null);
    	Integer next = null;
    	while ((next = queue.nextUnchecked()) != null)
	    {
	    	visit(next.toString());
	    	queue.setToChecked(next);
	    }
    	
    	
    }
    
    static void visit(String id) throws Exception
    {
    	if (id != null)
	    {
		    session.setHeaderPath("/api/v1/beets/" + id);
		    session.send();
		    if (hasChallenge(id)) return;
		    session.setHeaderPath("/api/v1/friends/" + id);
		    session.send();
		    if (hasChallenge(id)) return;
	    }
	    else
        {
	        session.setHeaderPath("/api/v1/crawl_sessions/1");
	        session.send("");
	        session.setHeaderPath("/api/v1/graphs/random_people");
	        session.send();
	    }
	    
	    Profiles using;
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
    	
    	for (String line: (json.split(" ")))
	    {
		    try
		    {
			    queue.add(Integer.valueOf(line));
			    if (using != null) using.addFriend(queue.getProfile(Integer.valueOf(line)));
			
		    } catch (NumberFormatException ignored){}
	    }
    }
    
    static boolean hasChallenge(String id) throws Exception
    {
	    if (session.getLastResponse().getResponseCode() == 302)
	    {
		    challenge();
		    queue.setToUnchecked(Integer.valueOf(id));
		    return true;
	    }
	    return false;
    }
    
    static void challenge() throws Exception
    {
    	session.printResponse(true);
    	session.setHeaderPath("/api/v1/challenges/");
    	session.send();
    	if (session.getLastResponse().getJson() != null)
	    {
	    	String json = session.getLastResponse().getJson();
	    	json = json.split("\"to\":")[1];
	    	try
		    {
			    Integer to = Integer.valueOf(json.split(",")[0]);
			    Integer from = Integer.valueOf(json.split("\"from\":")[1].split(",")[0]);
			    JsonMapper mapper = new JsonMapper();
			    PathFinder finder = new PathFinder();
			    int count = finder.count(to, from, queue);
			    mapper.putInt("distance", count);
			    session.send(mapper.toString());
			    if (session.getLastResponse().getJson().contains("failure"))
			    {
			    	int actual = challengeBruteForce((Integer) mapper.map.get("distance"));
				    System.out.println("Result Found: Tested: " + count + " - Actual: " + actual + "\n");
			    }
			    
		    } catch (NumberFormatException error)
		    {
		    	throw error;
		    }
		    session.printResponse(false);
	    	
	    }

    }
    
    static int challengeBruteForce(int original) throws Exception
    {
        session.printResponse(false);
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

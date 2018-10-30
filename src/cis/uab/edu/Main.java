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
    	String next = null;
    	while ((next = queue.nextUnchecked().toString()) != null)
	    {
	    	visit(next);
	    	queue.setToChecked(Integer.valueOf(next));
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
			    Profiles temp = queue.add(Integer.valueOf(line));
			    if (using != null && temp != null) using.addFriend(temp);
			
		    } catch (NumberFormatException ignored){}
	    }
    }
    
    static boolean hasChallenge(String id) throws Exception
    {
	    if (session.getLastResponse().getResponseCode() == 302)
	    {
		    challenge();
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
	    	String to = json.split(",")[0];
	    	String from = json.split("\"from\":")[1].split(",")[0];
	    }

    }
    
}

package cis.uab.edu;
import cis.uab.edu.Http.*;

//"email": "lforbus@uab.edu"
//"password": "w7BR2PgZ"

public class Main
{
	final static String email = "lforbus@uab.edu";
	final static String password = "w7BR2PgZ";
	
    public static void main(String[] args) throws Exception
    {
    	StringBuilder stringBuilder = new StringBuilder();

    	HttpSession session = new HttpSession("odin.cs.uab.edu", 3001, new HttpHeader(HttpMethod.POST, "/oauth/token"));
    	session.addHeaderValue("accept", "application/json");
    	session.addHeaderValue("connection", "close");
    	session.addMethodHeader(HttpMethod.POST, "Content-Type", "application/json");
    	
    	JsonMapper mapper = new JsonMapper();
    	mapper.putString("email", email);
    	mapper.putString("password", password);
    	mapper.putString("grant_type", "password");
    	
    	session.send(mapper.toString());
    	
	    System.out.println(session.getLastResponse().getJson());
	    
    	session.addHeaderValue("authorization", "bearer 3f02f47ec0fab748f2ad16ddcf400fa1efe95481db336a6e5ba40268a30a3ce4");
    	session.setHeaderPath("/api/v1/crawl_sessions/1");
    	session.send("");
	    
    	session.setHeaderPath("/api/v1/beets/1");
    	session.send();
    	
    	
	    
    }
    
}

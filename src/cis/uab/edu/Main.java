package cis.uab.edu;
import cis.uab.edu.Request.*;

import java.net.Socket;

//"email": "lforbus@uab.edu"
//"password": "w7BR2PgZ"

public class Main
{
    public static void main(String[] args) throws Exception
    {
        HttpRequest request = new HttpRequest(new Socket("odin.cs.uab.edu", 3001), new HttpHeader(HttpMethod.POST, "/oauth/token"));
        request.addHeader("Content-Type: application/json");
        request.addHeader("Connection: Close");
        request.addMatcher(HttpLineMatcher.regexMatcher("", (line) -> {}));
        request.printResponseOnceRead();
        request.sendWithBody("{ \"email\": \"lforbus@uab.edu\", \"password\": \"w7BR2PgZ\", \"grant_type\": \"password\"}");
        
    }
    
}

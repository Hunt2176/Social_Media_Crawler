package cis.uab.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test
{
	static void LoginJson() throws IOException
	{
		Socket socket = new Socket("odin.cs.uab.edu", 3001);
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		StringBuilder builder = new StringBuilder();
		String json = "{ \"email\": \"lforbus@uab.edu\", \"password\": \"w7BR2PgZ\", \"grant_type\": \"password\"}";
		builder.append("POST /oauth/token HTTP/1.1\r\n");
		builder.append("Host: odin.cs.uab.edu:3001\r\n");
		builder.append("Content-Type: application/json\r\n");
		builder.append("Content-Length: " + json.length());
		builder.append("\r\n\r\n");
		builder.append(json);
		
		
		writer.print(builder.toString());
		writer.flush();
		String line;
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
		}
	}
}

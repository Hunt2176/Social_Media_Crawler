package cis.uab.edu.Http;

import java.util.ArrayList;
import java.util.Arrays;

public class HttpResponse
{
	int responseCode = -1;
	String response = "";
	String[] headers = new String[]{};
	String json = "";
	String[] body = new String[]{};
	
	HttpResponse(String response)
	{
		String[] lines = response.split("\n");
		String headerline = lines[0].split("HTTP.....")[1];
		String[] headerSplit = headerline.split(" ");
		responseCode = Integer.valueOf(headerSplit[0]);
		this.response = headerSplit[1];
		
		ArrayList<String> headers = new ArrayList<>();
		int lastprocessed = 1;
		for (int index = lastprocessed; index < lines.length; index++)
		{
			String line = lines[index];
			lastprocessed = index;
			if (line.isEmpty()) break;
			headers.add(line);
		}
		if (responseCode > 399) return;
		this.headers = headers.toArray(this.headers);
		body = Arrays.copyOfRange(lines, lastprocessed + 1, lines.length);
		for (String i: body)
		{
			if (i.matches("\\{.+}"))
			{
				json = i;
				break;
			}
		}
	}
	
	public int getResponseCode()
	{
		return responseCode;
	}
	
	public String getResponse()
	{
		return response;
	}
	
	public String[] getHeaders()
	{
		return headers;
	}
	
	public String getJson()
	{
		return json;
	}
	
	public String[] getBody()
	{
		return body;
	}
}

package cis.uab.edu.Http;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that takes in headers and cookies to be sent with HTTPRequest class
 */
public class HttpHeader
{
	String[] headers;
	HttpMethod method;
	String connectPath;
	Cookie[] cookies = new Cookie[0];
	protected HttpRequest owningRequest = null;
	
	/**
	 * @param method HttpMethod to send with (GET, POST)
	 * @param connectPath Path on server to connect with
	 * @param headers vararg of Additional headers to send when sending the HTTPRequest
	 */
	public HttpHeader(HttpMethod method, String connectPath, String... headers)
	{
		this.method = method;
		this.connectPath = connectPath;
		this.headers = headers;
	}
	
	public HttpHeader(HttpMethod method, String connectPath)
	{
		this.method = method;
		this.connectPath = connectPath;
		this.headers = new String[0];
	}
	
	/**
	 * Adds an additional header to the array
	 * @param header String of a header to add
	 */
	public void addHeader(String header)
	{
		ArrayList<String> temp = new ArrayList(Arrays.asList(headers));
		temp.add(header);
		headers = temp.toArray(headers);
	}
	
	/**
	 * Adds an additional cookie to the cookie array
	 * @param cookie Cookie to add
	 */
	public void addCookie(Cookie cookie)
	{
		ArrayList<Cookie> temp = new ArrayList<>(Arrays.asList(cookies));
		temp.add(cookie);
		cookies = temp.toArray(cookies);
	}
	
	/**
	 * Clears stored cookies from the header
	 */
	public void clearCookies()
	{
		cookies = new Cookie[0];
	}
	
	/**
	 * Returns the HTTP compatible format of the header
	 * @return Headers in HTTP form
	 */
	public String getHttpHeader()
	{
		return toString();
	}
	
	/**
	 * Returns the HTTP compatible format of the header
	 * @return Headers in HTTP form
	 */
	@Override
	public String toString()
	{
		String toReturn = "";
		
		switch (method)
		{
			case GET: toReturn = "GET " + connectPath + " HTTP/1.1\r\n";
				break;
			case POST: toReturn = "POST " + connectPath + " HTTP/1.1\r\n";
				break;
		}
		if (owningRequest != null) toReturn += "Host: " + owningRequest.socketHttpHost() + "\r\n";
		for (String header: headers)
		{
			toReturn += (header + "\r\n");
		}
		
		if (cookies.length > 0)
		{
			toReturn += "Cookie: ";
			for (Cookie cookie: cookies)
			{
				toReturn += cookie + "; ";
			}
		}
		
		toReturn += "\r\n";
		return toReturn;
	}
	
	
}



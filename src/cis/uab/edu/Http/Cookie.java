package cis.uab.edu.Http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for storing the key and value of a cookie
 */
public class Cookie
{
	/**
	 * Creates a cookie from an HTTPLine that contains the "Set-Cookie:" text
	 * @param line Line received to extract cookie from
	 * @return Cookie or null if not processable
	 */
	static Cookie fromHttpLine(String line)
	{
		Pattern cookieCutter = Pattern.compile("[^Set\\-Cookie: ]([^;])+");
		Matcher matcher = cookieCutter.matcher(line);
		if (matcher.find())
		{
			String[] toSplit = matcher.group(0).split("=");
			return new Cookie(toSplit[0], toSplit[1]);
		}
		return null;
	}
	
	String id;
	String value;
	
	/**
	 * Creates cookie from ID and value strings
	 * @param id key value for the cookie
	 * @param value value associated with key
	 */
	Cookie(String id, String value)
	{
		// Removes escape characters and whitespace
		this.id = id.replaceAll("[ \\r\\n\\f\\t]", "");
		this.value = value.replaceAll("[ \\r\\n\\f\\t]", "");
	}
	
	/**
	 * Resets stored values to that of the new cookie
	 * @param cookie to set with
	 */
	void set(Cookie cookie)
	{
		this.id = cookie.id;
		this.value = cookie.value;
	}
	
	/**
	 * Creates String to correctly represent the cookie as it would be in a Http Header
	 */
	@Override
	
	public String toString()
	{
		return id + "=" + value;
	}
}
package cis.uab.edu.Http;

/**
 * Used to match lines received back from HTTP Request for processing
 */
public abstract class HttpLineMatcher
{
	public static HttpLineMatcher cookieUpdater(Cookie cookieToUpdate)
	{
		return HttpLineMatcher.regexMatcher("Set-Cookie.*", (line -> {
			Cookie testCookie = Cookie.fromHttpLine(line);
			if (testCookie != null) cookieToUpdate.set(testCookie);
		}));
	}
	
	public static HttpLineMatcher regexMatcher(String regex, LineMatch onMatch)
	{
		return new HttpLineMatcher(MatchMethod.regex, regex, onMatch)
		{
			@Override
			void onMatch(String line)
			{
				onMatchLambda.onMatch(line);
			}
		};
	}
	
	public static HttpLineMatcher containMatcher(String regex, LineMatch onMatch)
	{
		return new HttpLineMatcher(MatchMethod.contains, regex, onMatch)
		{
			@Override
			void onMatch(String line)
			{
				onMatchLambda.onMatch(line);
			}
		};
	}
	
	public static HttpLineMatcher jsonMatcher(LineMatch onMatch)
	{
		return HttpLineMatcher.regexMatcher("\\{.+\\}", onMatch);
	}
	
	/**
	 * Matcher that matches if any response other than an HTTP 200 is given
	 * @param onMatch The line to use with the lambda
	 * @return Matcher that has been assembled with the match and the onMatch set to the lambda
	 */
	public static HttpLineMatcher failedHttpRequest(LineMatch onMatch)
	{
		return HttpLineMatcher.regexMatcher("HTTP/(\\d|\\.)+ [^(200)].+", onMatch);
	}
	
	public static HttpLineMatcher failedHttpRequest()
	{
		return HttpLineMatcher.regexMatcher("HTTP/(\\d|\\.)+ [^(200)].+", (line -> System.err.println(line)));
	}
	
	String regex;
	MatchMethod method;
	LineMatch onMatchLambda;
	
	/**
	 * @param method MatchMethod of either Regex or containing
	 * @param regex The string that will be matched against with the method provided
	 */
	public HttpLineMatcher(MatchMethod method, String regex)
	{
		this.method = method;
		this.regex = regex;
	}
	
	// Used by the static methods to implement lambdas into the current scheme
	private HttpLineMatcher(MatchMethod method, String regex, LineMatch onMatch)
	{
		this.method = method;
		this.regex = regex;
		this.onMatchLambda = onMatch;
	}
	
	/**
	 * Checks the supplied line with the method specified to determine whether the line
	 * can be processed by the onMatch function
	 *
	 * @param toCheck HttpLine passed for checking
	 */
	void checkMatch(String toCheck)
	{
		switch (method)
		{
			case regex: if (toCheck.matches(regex)) onMatch(toCheck);
				break;
			case contains: if (toCheck.contains(regex)) onMatch(toCheck);
				break;
		}
		
	}
	
	/**
	 * Abstract method called by the HttpRequest when the line has a match from the
	 * supplied regex on object creation
	 *
	 * @param line Line from HTTP request that matched
	 */
	abstract void onMatch(String line);
	
	/**
	 * Available matching methods.
	 * Regex: Will use the input as a Regular expression. The line must match 100% to be processed
	 * contains: Verifies that the line contains the substring passed into the method before processing.
	 */
	enum MatchMethod
	{
		regex, contains
	}
}


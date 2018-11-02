package cis.uab.edu.Http;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Handles creation and use of HttpRequest, HttpHeaders, and HttpResponses.
 * Keep previous items in memory and provides easy solution for sending multiple requests to the same address.
 */
public class HttpSession
{
	String address;
	int port;
	String headerPath;
	
	HttpHeader header = null;
	
	HttpLineMatcher[] storedMatchers = new HttpLineMatcher[]{};
	String[] bothHeaders = new String[]{};
	String[] postHeaders = new String[]{};
	String[] getHeaders = new String[]{};
	Boolean printWhenDone = false;
	
	HttpRequest lastRequest = null;
	HttpResponse lastResponse = null;
	
	SessionSendComplete onComplete = null;
	
	/**
	 * Creates a Session with a header to start with
	 * @param address Address to connect sockets to
	 * @param port Port to connect sockets to
	 * @param startHeader Starting HTTPHeader
	 */
	public HttpSession(String address, int port, HttpHeader startHeader)
	{
		this.address = address;
		this.port = port;
		this.header = startHeader;
		this.headerPath = startHeader.connectPath;
	}
	
	/**
	 *  Creates a session with a path to add to headers
	 * @param address Address to connect sockets to
	 * @param port Port to connect sockets to
	 * @param connectionPath Path that is added to header to connect at
	 */
	public HttpSession(String address, int port, String connectionPath)
	{
		this.address = address;
		this.port = port;
		this.headerPath = connectionPath;
	}
	
	/**
	 * Creates an HTTPRequest to use.
	 * @param using HttpHeader to use in creation
	 * @return Created HttpRequest
	 * @throws IOException
	 */
	private HttpRequest createRequest(HttpHeader using) throws IOException
	{
		HttpRequest toReturn = new HttpRequest(new Socket(address, port), using, storedMatchers);
		if (printWhenDone) toReturn.printResponseOnceRead();
		
		this.header = null;
		this.lastRequest = toReturn;
		return toReturn;
	}
	
	/**
	 * Creates an array of headers depending on Http Method
	 * @param method Method of which to get headers for
	 * @return Combined Http Headers from the method specified
	 */
	private String[] createHeaderValues(HttpMethod method)
	{
		ArrayList<String> toReturn = new ArrayList<>(Arrays.asList(bothHeaders));
		switch (method)
		{
			case GET:
				toReturn.addAll(Arrays.asList(getHeaders));
				break;
			case POST:
				toReturn.addAll(Arrays.asList(postHeaders));
				break;
		}
		return toReturn.toArray(new String[]{});
	}
	
	private void resendLastRequest() throws Exception
	{
		if (this.getLastRequest() != null)
		{
			send(this.getLastRequest().getHttpHeader());
		}
	}
	
	/**
	 * Adds an HttpMatcher that is applied to all HttpRequests created
	 * @param matcher HttpMatcher to add to Request
	 */
	public void addMatcher(HttpLineMatcher matcher)
	{
		ArrayList<HttpLineMatcher> temp = new ArrayList<>(Arrays.asList(storedMatchers));
		temp.add(matcher);
		storedMatchers = temp.toArray(storedMatchers);
	}
	
	/**
	 * Adds an Http Header to GET/POST requests
	 * @param key header name
	 * @param value header value
	 */
	public void addHeaderValue(String key, String value)
	{
		ArrayList<String> temp = new ArrayList<>(Arrays.asList(bothHeaders));
		//Removes header if key already exits
		for (String header: temp)
		{
			if (header.toLowerCase().contains((key + ": ").toLowerCase())) temp.remove(header);
		}
		temp.add(key + ": " + value);
		bothHeaders = temp.toArray(bothHeaders);
		if (header != null) header.addHeader(key + ": " + value);
	}
	
	/**
	 * Adds and Http Header to either GET or POST requests
	 * @param method Method to add to
	 * @param key Header name
	 * @param value Header value
	 */
	public void addMethodHeader(HttpMethod method, String key, String value)
	{
		ArrayList<String> headers;
		switch (method)
		{
			case GET:
				headers = new ArrayList<>(Arrays.asList(getHeaders));
				for (String header: headers)
				{
					if (header.toLowerCase().contains((key + ": ").toLowerCase())) headers.remove(header);
				}
				headers.add(key + ": " + value);
				getHeaders = headers.toArray(getHeaders);
				break;
			case POST:
				headers = new ArrayList<>(Arrays.asList(postHeaders));
				for (String header: headers)
				{
					if (header.toLowerCase().contains((key + ": ").toLowerCase())) headers.remove(header);
				}
				headers.add(key + ": " + value);
				postHeaders = headers.toArray(postHeaders);
				break;
			default:
				return;
		}
		if (this.header != null && this.header.method == method) this.header.addHeader(key + ": " + value);
	}
	
	/**
	 * Sets whether the request will print out its full HttpResponse to line once completed
	 * @param bool
	 */
	public void printResponse(Boolean bool)
	{
		this.printWhenDone = bool;
	}
	
	/**
	 * Sets a specific Header to use on next send operation
	 * @param newHeader HttpHeader to Set
	 */
	public void setHeader(HttpHeader newHeader)
	{
		this.header = newHeader;
	}
	
	/**
	 * Sets the path that the generated HttpHeader will use. Does not apply to manually passed header.
	 * @param newPath
	 */
	public void setHeaderPath(String newPath)
	{
		this.headerPath = newPath;
	}
	
	/**
	 * Sends a GET HttpRequest with no Body
	 * @throws IOException
	 */
	public void send() throws IOException
	{
		HttpHeader using = (header == null) ? new HttpHeader(HttpMethod.GET, headerPath, createHeaderValues(HttpMethod.GET)) : header;
		createRequest(using).send();
		this.lastResponse = new HttpResponse(this.lastRequest.getResponse());
		onSendComplete();
	}
	
	/**
	 * Sends a POST HttpRequest with specified Body
	 * @param body Body to send with
	 * @throws IOException
	 */
	public void send(String body) throws IOException
	{
		HttpHeader using = (header == null) ? new HttpHeader(HttpMethod.POST, headerPath, createHeaderValues(HttpMethod.POST)) : header;
		createRequest(using).sendWithBody(body);
		this.lastResponse = new HttpResponse(this.lastRequest.getResponse());
		onSendComplete();
	}
	
	/**
	 * Sends HttpRequest with specified Header and body. Overwrites any stored header within the session.
	 * @param header HttpHeader to send with
	 * @param body Body to send with
	 * @throws IOException
	 */
	public void send(HttpHeader header, String body) throws IOException
	{
		this.header = header;
		send(body);
		onSendComplete();
	}
	
	/**
	 *  Sends HttpRequest with specified HttpRequest. Overwrites any stored header within the session.
	 * @param header HttpHeader to send with
	 * @throws IOException
	 */
	public void send(HttpHeader header) throws IOException
	{
		this.header = header;
		send();
		onSendComplete();
	}
	
	/**
	 * Returns the Request that would normally be sent with a body. If body is empty the request is automatically GET
	 * and POST if a body is provided. Uses stored HttpHeader if applied.
	 * @param body Body to send with
	 * @return String of HttpRequest that would be sent
	 * @throws IOException
	 */
	public String getFullRequest(String body) throws IOException
	{
		HttpHeader using = (!(body != null && body.length() > 0))
				? new HttpHeader(HttpMethod.GET, headerPath, createHeaderValues(HttpMethod.GET))
				: new HttpHeader(HttpMethod.POST, headerPath, createHeaderValues(HttpMethod.POST));
		body = (body == null) ? "" : body;
		return ((header == null)
				? createRequest(using)
				: createRequest(header)).requestToString(body);
	}
	
	private boolean usingOnSendComplete = false;
	private void onSendComplete()
	{
		if (onComplete != null && !usingOnSendComplete)
		{
			usingOnSendComplete = true;
			onComplete.run(this);
		}
		onComplete = null;
		usingOnSendComplete = false;
	}
	
	public boolean isUsingOnSendComplete()
	{
		return this.usingOnSendComplete;
	}
	
	public void setOnSendComplete(SessionSendComplete onComplete)
	{
		this.onComplete = onComplete;
	}
	
	/**
	 * The last HttpResponse received.
	 * @return Stored HttpResponse
	 */
	public HttpResponse getLastResponse()
	{
		return this.lastResponse;
	}
	
	public HttpRequest getLastRequest()
	{
		return this.lastRequest;
	}
	
	public String getHeaderPath()
	{
		return this.headerPath;
	}
	
}

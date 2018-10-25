package cis.uab.edu.Http;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	
	public HttpSession(String address, int port, HttpHeader startHeader)
	{
		this.address = address;
		this.port = port;
		this.header = startHeader;
		this.headerPath = startHeader.connectPath;
	}
	
	public HttpSession(String address, int port, String connectionPath)
	{
		this.address = address;
		this.port = port;
		this.headerPath = connectionPath;
	}
	
	private HttpRequest createRequest(HttpHeader using) throws IOException
	{
		HttpRequest toReturn = new HttpRequest(new Socket(address, port), using, storedMatchers);
		if (printWhenDone) toReturn.printResponseOnceRead();
		
		this.header = null;
		this.lastRequest = toReturn;
		return toReturn;
	}
	
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
	
	public void addMatcher(HttpLineMatcher matcher)
	{
		ArrayList<HttpLineMatcher> temp = new ArrayList<>(Arrays.asList(storedMatchers));
		temp.add(matcher);
		storedMatchers = temp.toArray(storedMatchers);
	}
	
	public void addHeaderValue(String key, String value)
	{
		ArrayList<String> temp = new ArrayList<>(Arrays.asList(bothHeaders));
		temp.add(key + ": " + value);
		bothHeaders = temp.toArray(bothHeaders);
		if (header != null) header.addHeader(key + ": " + value);
	}
	
	public void addMethodHeader(HttpMethod method, String key, String value)
	{
		ArrayList<String> headers;
		switch (method)
		{
			case GET:
				headers = new ArrayList<>(Arrays.asList(getHeaders));
				headers.add(key + ": " + value);
				getHeaders = headers.toArray(getHeaders);
				break;
			case POST:
				headers = new ArrayList<>(Arrays.asList(postHeaders));
				headers.add(key + ": " + value);
				postHeaders = headers.toArray(postHeaders);
				break;
			default:
				return;
		}
		if (this.header != null && this.header.method == method) this.header.addHeader(key + ": " + value);
	}
	public void printResponse(Boolean bool)
	{
		this.printWhenDone = bool;
	}
	
	public void setHeader(HttpHeader newHeader)
	{
		this.header = newHeader;
	}
	
	public void setHeaderPath(String newPath)
	{
		this.headerPath = newPath;
	}
	
	public void send() throws IOException
	{
		HttpHeader using = (header == null) ? new HttpHeader(HttpMethod.GET, headerPath, createHeaderValues(HttpMethod.GET)) : header;
		createRequest(using).send();
		this.lastResponse = new HttpResponse(this.lastRequest.getResponse());
	}
	
	public void send(String body) throws IOException
	{
		HttpHeader using = (header == null) ? new HttpHeader(HttpMethod.POST, headerPath, createHeaderValues(HttpMethod.POST)) : header;
		createRequest(using).sendWithBody(body);
		this.lastResponse = new HttpResponse(this.lastRequest.getResponse());
	}
	
	public void send(HttpHeader header, String body) throws IOException
	{
		this.header = header;
		send(body);
	}
	
	public void send(HttpHeader header) throws IOException
	{
		this.header = header;
		send();
	}
	
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
	
	public HttpResponse getLastResponse()
	{
		return this.lastResponse;
	}
	
}

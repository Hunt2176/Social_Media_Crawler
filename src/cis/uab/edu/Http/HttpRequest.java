package cis.uab.edu.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Sends the Headers and body and processes responses
 */
public class HttpRequest
{
	
	public static HttpRequest createWithReadingThread(Socket socket, HttpHeader header, HttpLineMatcher... matchers)
	{
		HttpRequest request = new HttpRequest(socket, header, matchers);
		request.readerThread = new Thread(() -> request.readReader(true));
		request.readerThread.start();
		return request;
	}
	
	private HttpHeader header;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private Thread readerThread = null;
	private ArrayList<HttpLineMatcher> matchers = new ArrayList<>();
	
	private String response = "";
	private Boolean printResponse = false;
	
	/*
	Used to change whether the BufferedReader reading with be printed to the console
	 */
	public Boolean printOutput = false;
	
	/**
	 * Creates the request and the objects needed to send and receive the request
	 *
	 * @param socket Socket of address and port
	 * @param header Header object of what to send with request
	 * @param matchers Watchers that can be used to monitor the output received from the request
	 */
	public HttpRequest(Socket socket, HttpHeader header, HttpLineMatcher... matchers)
	{
		this.socket = socket;
		this.header = header;
		this.header.owningRequest = this;
		this.matchers.addAll((Arrays.asList(matchers)));
		try
		{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			
		} catch (IOException err)
		{
			reader = null;
		}
	}
	
	/**
	 * Adds an additional output matcher
	 * @param toAdd Watcher to add
	 */
	public void addMatcher(HttpLineMatcher toAdd)
	{
		matchers.add(toAdd);
	}
	
	/**
	 * Reads the response from the request until the stream ends.
	 * If there is an issue with getting stuck in the method, try using
	 * the createPrintThread method
	 *
	 * @param printOutput Boolean of whether to print the response to the console
	 */
	private void readReader(boolean printOutput)
	{
		String response;
		try
		{
			while ((response = reader.readLine()) != null)
			{
				if (printOutput) System.out.println(response);
				this.response += (response + "\n");
				for (HttpLineMatcher matcher: matchers)
				{
					matcher.checkMatch(response);
				}
			}
			if (this.printResponse) System.out.println(this.response);
		
		} catch (IOException err)
		{
			System.err.println("Watcher Quit Unexpectedly");
			err.printStackTrace();
		}
	}
	
	/**
	 * Loads the sockets PrintWriter with the headers.
	 * Used in both GET and POST requests
	 */
	private void readyWriter()
	{
		writer.print(this.header.getHttpHeader());
	}
	
	/**
	 * Sends only the headers provided
	 */
	public void send()
	{
		readyWriter();
		writer.flush();
		if (readerThread == null) readReader(printOutput);
		close();
	}
	
	/**
	 * Sends the stored headers and additional body, meant for POST requests
	 *
	 * @param requestBody Additional body message sent with request
	 */
	public void sendWithBody(String requestBody)
	{
		try
		{
			//Adds the length of the post content to the header
			header.addHeader("Content-Length: " + requestBody.length());
			
			readyWriter();
			writer.print(requestBody);
			writer.flush();
			if (readerThread == null)
			{
				readReader(printOutput);
			}
		} catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
	
	public void replaceHttpHeader(HttpHeader header)
	{
		this.header = header;
	}
	
	/**
	 * Debug method. Same as the sendWithBody method but just prints the PrintWriter to the console
	 * instead of sending. Can be used to verify the Request is formatted correctly.
	 * @param body
	 */
	public void flushWriterWithBodyToLine(String body)
	{
		writer = new PrintWriter(System.out);
		sendWithBody(body);
	}
	
	public String getResponse()
	{
		return this.response;
	}
	
	public String requestToString(String body)
	{
		if (body != null && body.length() > 0) header.addHeader("Content-Length: " + body.length());
		return header.getHttpHeader() + body;
	}
	
	
	/**
	 * Sets the request to print out the response received once the socket is closed
	 */
	public void printResponseOnceRead()
	{
		printResponse = true;
	}
	
	/**
	 * Adds a header value to the contained HttpHeader
	 * @param key Header name
	 * @param value Header value
	 */
	public void addHeaderValue(String key, String value)
	{
		header.addHeader(key + ": " + value);
	}
	
	/**
	 * Gets the Socket address and port for HTTP 1.1 use
	 * @return Socket connect info formatted for HTTP 1.1
	 */
	public String socketHttpHost()
	{
		return socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort();
	}
	
	/**
	 * Closes the socket stored within the Request
	 */
	public void close()
	{
		try
		{
			socket.close();
			
		} catch (IOException err)
		{
			err.printStackTrace();
		}
	}
}

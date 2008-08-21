package daap;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;

public class DAAPUtilities {

	HttpClient  client;	
	
	protected DAAPUtilities(final String hostname, final Log log) throws IOException {
		
		this.names = new HashMap<Integer,String>();
		this.types = new HashMap<Integer,Short>();
		
		this.requests = new HashMap<InputStream, HttpMethod>();
		
		DAAPUtilities.initContentCodes(this.names, this.types);
		
		this.retrieveContentCodes(hostname, log);
	}
	
	protected InputStream request(String hostname, String request, Log log) throws IOException {
		
		log.debug("daap client: request for http://" + hostname + ":3689/" + request);
		
		client = new HttpClient();
		HttpMethod method = new GetMethod("http://" + hostname + ":3689/" + request);
		
		InputStream responseBody = null;
		
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    try {
	        // Execute the method.
	        int statusCode = client.executeMethod(method);

	        if (statusCode/100 != HttpStatus.SC_OK/100) {
	          log.error("Method failed: " + method.getStatusLine());
	        }

	        // Read the response body.
	        responseBody = method.getResponseBodyAsStream();

	        this.requests.put(responseBody, method);
	        
	        return responseBody;

	    } catch (HttpException e) {
	        System.err.println("Fatal protocol violation: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return null;
	}
	
	protected void release(InputStream request) {
		if ((request != null) && (this.requests.get(request) != null)) {
			try {
				request.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.requests.get(request).releaseConnection();
			this.requests.remove(request);
		}
	}
	
	private void retrieveContentCodes(final String hostname, final Log log) throws IOException {
		
		InputStream response = this.request(hostname, "content-codes", log);
		
		if (response == null) {
			throw new NullPointerException();
		}
		
		DAAPEntry entry = DAAPEntry.parseStream(response, this.types);

		if (entry.getName() == stringToInt("mccr")) {
			for (DAAPEntry e: entry) {

				if (e.getName() != stringToInt("mdcl")) {
					continue;
				}

				Map<Integer,Object> values = e.getValueMap();

				String name = (String)values.get(stringToInt("mcna"));
				short type = (Short)values.get(stringToInt("mcty"));
				int number = (Integer)values.get(stringToInt("mcnm"));

				this.names.put(number, name);
				this.types.put(number, type);
			}
		}

		this.release(response);
	}
	
	protected Map<Integer, String> names;
	protected Map<Integer, Short> types;
	
	private Map<InputStream, HttpMethod> requests;
	
	private static void initContentCodes(Map<Integer, String> names, Map<Integer, Short> types) {
		names.put(stringToInt("mdcl"), "dmap.dictionary");
		types.put(stringToInt("mdcl"), DAAPEntry.LIST);
		names.put(stringToInt("mstt"), "dmap.status");
		types.put(stringToInt("mstt"), DAAPEntry.INTEGER);
		names.put(stringToInt("mcnm"), "dmap.contentcodesnumber");
		types.put(stringToInt("mcnm"), DAAPEntry.INTEGER);
		names.put(stringToInt("mcty"), "dmap.contentcodestype");
		types.put(stringToInt("mcty"), DAAPEntry.SHORT);
		names.put(stringToInt("mcna"), "dmap.contentcodesname");
		types.put(stringToInt("mcna"), DAAPEntry.STRING);
		names.put(stringToInt("mccr"), "dmap.contentcodes");
		types.put(stringToInt("mccr"), DAAPEntry.LIST);
		names.put(stringToInt("msrv"), "dmap.serverinforesponse");
		types.put(stringToInt("msrv"), DAAPEntry.LIST);
	}
	
	public static int stringToInt(String name) {
		return new BigInteger(name.getBytes()).intValue();
	}
	
	public static String intToString(int name) {
		byte[] bytes = new byte[4];
		int n = name;
		for (int i = 0; i < 4; i++) {
			bytes[3-i] = (byte)(n % 256);
			n = n / 256;
		}
		return new String(bytes);
	}
}

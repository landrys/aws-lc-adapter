package com.amazonaws.lambda.lcadapter.lcclient;
 
import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.lambda.lcadapter.oauthclient.Secure;



public class LcApi {
	private static final Log logger = LogFactory.getLog(LcApi.class);
	// holds http errors gotten
	private static ConcurrentHashMap<Integer, Integer> lCErrors = new ConcurrentHashMap<Integer, Integer>();

	//LCLeakyBucket lCLeakyBucket;

	OAuthTokenRefresher oAuthTokenRefresher = new OAuthTokenRefresher();
	/*
	 *
	 *  I got this totally manually see procedure on googleDrive
	 * 
	 */
	private String oAuthToken = Secure.oAuthToken;
	private AtomicBoolean refreshingToken = new AtomicBoolean();

	private String commonURL = LcUtils.URL + "/" + LcUtils.PRODACCOUNT;

	private int numOfCalls;

	public LcApi() {
		super();
		refreshingToken.set(false);
		logger.info("*** Instantiating LcApi ***");
	}

	public InputStreamWrapper getInputStreamSynchronized( String query, String string ) throws RefreshingTokenInProcessException, Exception
	{
		return getInputStreamSynchronized(query, string, null);
	}

	// Not Synchronized yet
	public InputStreamWrapper getInputStreamSynchronized(
			String apiCommand, String requestMethod, Object content ) throws RefreshingTokenInProcessException, Exception
	{
		if ( refreshingToken.get() )
			throw new RefreshingTokenInProcessException("Refreshing Token please try again....");

		try
		{
			return getInputStreamUsingLeakyBucketInfo(apiCommand, requestMethod, content);
		}
		catch (RefreshedTokenException e)
		{
			return getInputStreamUsingLeakyBucketInfo(apiCommand, requestMethod, content);

		}

	}

	private InputStreamWrapper getInputStreamUsingLeakyBucketInfo(
			String apiCommand, String requestMethod, Object obj) throws RefreshedTokenException, Exception {

		numOfCalls++;
		apiCommand = prepare(apiCommand);

		logger.info("The obj:requestMethod:apiCommand is: " + obj
				+ ":"  + requestMethod + ":" + apiCommand );

		HttpsURLConnection uc = setUpApiConnection(apiCommand, requestMethod);
		
		if ( obj != null )
			setUpForPUTOrPOST(obj, uc);

        InputStreamWrapper isw = new InputStreamWrapper();

        try {

        	// removing this see task manager's leaky bucket limiter
        	//waitIfNeeded(requestMethod, uc);
        	// Needed to add below.
	       	logger.info("***CALL_TO_LC***");
			isw.setIs((InputStream)uc.getInputStream());

        } catch (IOException e ) {

        	//if (uc.getResponseCode() == 429)
            // TODO add more restrictions here like get that token is expired.
			if (uc.getResponseCode() == 401 || uc.getResponseCode() == 403)
			{
				refreshingToken.set(true);
				refreshToken();
				refreshingToken.set(false);
				throw new RefreshedTokenException("Token refreshed. Please try again.");
			}

			getErrorStream(isw, uc, e);
            trackErrors(uc.getResponseCode());

        } catch (Exception e ) {
            // I am not sure this ever gets thrown but let's put it here in case...
			// if (uc.getResponseCode() == 429)
			if (uc.getResponseCode() == 401 || uc.getResponseCode() == 403)
			{
				refreshingToken.set(true);
				refreshToken();
				refreshingToken.set(false);
				throw new RefreshedTokenException("Token refreshed. Please try again.");
			}
			getErrorStream(isw, uc, e);
            trackErrors(uc.getResponseCode());
        }

        isw.setResponseCode(uc.getResponseCode());
        isw.setUrlConnection(uc);
        return isw;
	}

	private void refreshToken() throws Exception
	{
		oAuthTokenRefresher.refreshToken();
		setOAuthToken(oAuthTokenRefresher.getToken());
	}

	private void trackErrors(int rc) {
	    // Put into a hashMap to track Errors
        if ( lCErrors.get(rc) == null ) {
            lCErrors.put(rc,1);
            //Running sum
            if ( lCErrors.get(rc+1000) != null )  {
                int i = lCErrors.get(rc+1000);
                lCErrors.put(rc+1000, ++i);
            } else {
                lCErrors.put(rc+1000, 1);
            }

        } else {
            int i = lCErrors.get(rc);
            lCErrors.put(rc, ++i);
            //Running sum
            int j = lCErrors.get(rc+1000);
            lCErrors.put(rc+1000, ++j);
        }	
	}

	private void setUpForPUTOrPOST(Object obj, HttpsURLConnection uc)
			throws Exception {
		if (obj != null) {
			OutputStream os = uc.getOutputStream();
			OutputStreamWriter wout = new OutputStreamWriter(os);
			// Need to decode URL encodings...
			obj = java.net.URLDecoder.decode((String) obj, "UTF-8");

			StringReader reader = new StringReader((String) obj);
			StringReader reader1 = new StringReader((String) obj);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			logger.info("PUT or POST: The content being sent is:");
			StreamResult result = new StreamResult(System.out);
			transformer.transform(new javax.xml.transform.stream.StreamSource(
					reader), result);
			StreamResult result1 = new StreamResult(wout);
			transformer.transform(new javax.xml.transform.stream.StreamSource(
					reader1), result1);
			wout.flush();
			os.close();
		}
	}

	private HttpsURLConnection setUpApiConnection(String apiCommand, String requestMethod) throws Exception {

		URL lc = null;
		HttpsURLConnection uc = null;

		logger.info("Using OAuth request method");
		lc = new URL(apiCommand);
		uc = (HttpsURLConnection) lc.openConnection();
		uc.setRequestProperty("Authorization", "OAuth " + oAuthToken);
		uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod( requestMethod );
        uc.setDoOutput(true);
        uc.setDoInput(true); 
        uc.setRequestProperty ( "Accept", "application/json");
        return uc;
	}

	private String prepare(String apiCommand) {
        apiCommand = commonURL + "/" + apiCommand;
        return cleanAndPrepare(apiCommand);
	}

	private void getErrorStream( InputStreamWrapper isw, HttpsURLConnection uc, 
			Exception e) throws Exception {
		logger.info("Caught Exception Getting InputStream. Will get the error stream and return that.\n" 
			+ e. getMessage() );
        isw.setIs((InputStream)uc.getErrorStream());
        isw.setErrorStream(true);
    }

    public String getAccount() { return LcUtils.PRODACCOUNT; }

    /**
     * Sets the oAuthToken for this instance.
     *
     * @param oAuthToken The oAuthToken.
     */
    public void setOAuthToken(String oAuthToken)
    {
        this.oAuthToken = oAuthToken;
    }


    private String cleanAndPrepare( String apiCommand ) {
        apiCommand = apiCommand.trim();
        apiCommand = apiCommand.replace(" ", "%20"); 
        apiCommand = apiCommand.replace("#", "%23"); 
        apiCommand = apiCommand.replace("?or=timeStamp=>", "?or=timeStamp%3d%3e"); 
        apiCommand = apiCommand.replace("ItemShops.timeStamp=>", "ItemShops.timeStamp%3d%3e"); 
        apiCommand = apiCommand.replace("!=", "!%3d"); 
        /*
        apiCommand = apiCommand.replace("=>", "%3d%3e"); 
        apiCommand = apiCommand.replace("=<", "%3d%3c"); 
        apiCommand = apiCommand.replace("=", "%3d"); 
        apiCommand = apiCommand.replace("?", "%3f"); 
        apiCommand = apiCommand.replace("&", "%26"); 
        apiCommand = apiCommand.replace(">", "%3e"); 
        apiCommand = apiCommand.replace("<", "%3c"); 
        apiCommand = apiCommand.replace(",", "%2c"); 
        */
        return apiCommand;
    }

	private void runErrorCheck() {
		logger.info("NumberOfCalls: " + numOfCalls + "\nlCErrors: "
				+ lCErrors);
	}

	public void errorReport() {
		runErrorCheck();
	}

	// TESTING...
	private void testUC(HttpsURLConnection uc, Object obj) throws InterruptedException {
		String name = "X-LS-API-Bucket-Level";
		int i = 0;
		while (i < 1) {
			try {
				setUpForPUTOrPOST(obj, uc);
				System.out.println(uc.getHeaderField(name));
				uc.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("******************************"
						+ e.getMessage());

			}

			Thread.sleep(1000);
			i++;
		}

		// uc.getInputStream();
		// boolean header = uc.getUseCaches();
		// TODO Auto-generated method stub

	}


}
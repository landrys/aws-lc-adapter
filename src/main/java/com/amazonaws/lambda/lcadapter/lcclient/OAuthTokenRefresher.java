package com.amazonaws.lambda.lcadapter.lcclient;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.amazonaws.lambda.lcadapter.oauthclient.Secure;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OAuthTokenRefresher
{

	private static final Log logger = LogFactory.getLog(OAuthTokenRefresher.class);

	private ObjectMapper objectMapper = new ObjectMapper();
	
	private RefreshTokenBean refreshTokenBean= null;

	private String refreshUrl="https://cloud.merchantos.com/oauth/access_token.php";

	private String clientSecret = Secure.clientSecret;

	private String clientId="lcproxy";


	private String refreshToken=Secure.refreshToken;

	private String token = Secure.oAuthToken;

	private HttpsURLConnection uc;

	private InputStream inputStream;

	public OAuthTokenRefresher()
	{
		super();
	}

	// Just throwing an LCDown Exception if can't refresh.
	public synchronized void refreshToken() throws LCDownException
	{
		try
		{
     		setUpRefreshConnection();
			inputStream = uc.getInputStream();
     		processInputStream();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.info(e.getMessage());
			throw new LCDownException(e.getMessage());
		}

	}

	public String getToken() 
	{
		return refreshTokenBean.getAccessToken();
	}

	private  void processInputStream() throws LCDownException, Exception
	{

		if (inputStream == null)
			throw new LCDownException("Input stream is null. LC is probably unavailable.");

		refreshTokenBean = objectMapper.readValue(inputStream, RefreshTokenBean.class);
		logger.info(refreshTokenBean.toString());

	}

	private HttpsURLConnection setUpRefreshConnection() throws Exception
	{
		String payload = "client_id=" + clientId  + "&"
				+ "grant_type=refresh_token" + "&"
				+ "client_secret=" + clientSecret + "&"
				+ "refresh_token=" + refreshToken;

		logger.info(payload);

		URL lc = null;

		logger.info("Refreshing token...");
		lc = new URL(refreshUrl);
		uc = (HttpsURLConnection) lc.openConnection();
		uc.setReadTimeout(LcUtils.READ_TIMEOUT);
		uc.setRequestMethod("POST");
		uc.setDoOutput(true);
		uc.setDoInput(true);
		OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
		return uc;
	}

	public RefreshTokenBean getRefreshTokenBean()
	{
		return refreshTokenBean;
	}


}
		/*
		if ( refreshTokenBean == null )
			payload = payload + refreshToken;
		else
			payload = payload + refreshTokenBean.getRefreshToken();
//+ refreshTokenBean==null? refreshTokenBean.getRefreshToken():refreshToken;
 */


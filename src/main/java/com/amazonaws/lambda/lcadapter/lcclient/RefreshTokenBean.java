package com.amazonaws.lambda.lcadapter.lcclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenBean
{
	
    @JsonProperty("access_token")
	private  String accessToken;

    @JsonProperty("expires_in")
	private  String expiresIn;

    @JsonProperty("token_type")
	private  String tokenType;

    @JsonProperty("scope")
	private  String scope;

    @JsonProperty("systemuserid")
	private  String systemUserId;

    //@JsonProperty("refresh_token")
    @JsonIgnore
	private  String refreshToken;

	public String getAccessToken()
	{
		return accessToken;
	}
	public void setAccessToken( String accessToken )
	{
		this.accessToken = accessToken;
	}
	public String getExpiresIn()
	{
		return expiresIn;
	}
	public void setExpiresIn( String expiresIn )
	{
		this.expiresIn = expiresIn;
	}
	public String getTokenType()
	{
		return tokenType;
	}
	public void setTokenType( String tokenType )
	{
		this.tokenType = tokenType;
	}
	public String getScope()
	{
		return scope;
	}
	public void setScope( String scope )
	{
		this.scope = scope;
	}
	public String getSystemUserId()
	{
		return systemUserId;
	}
	public void setSystemUserId( String systemUserId )
	{
		this.systemUserId = systemUserId;
	}
	public String getRefreshToken()
	{
		return refreshToken;
	}
	public void setRefreshToken( String refreshToken )
	{
		this.refreshToken = refreshToken;
	}
	@Override
	public String toString()
	{
		return "RefreshTokenBean [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", tokenType=" + tokenType
				+ ", scope=" + scope + ", systemUserId=" + systemUserId + ", refreshToken=" + refreshToken + "]";
	}

}

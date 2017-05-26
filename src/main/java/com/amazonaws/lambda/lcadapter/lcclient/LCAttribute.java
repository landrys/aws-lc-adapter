package com.amazonaws.lambda.lcadapter.lcclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LCAttribute
{
	
    @JsonProperty("count")
	private  Integer count;

    @JsonProperty("offset")
	private  Integer offset;

    @JsonProperty("limit")
	private  Integer limit;

	public Integer getCount()
	{
		return count;
	}

	public void setCount( Integer count )
	{
		this.count = count;
	}

	public Integer getOffset()
	{
		return offset;
	}

	public void setOffset( Integer offset )
	{
		this.offset = offset;
	}

	public Integer getLimit()
	{
		return limit;
	}

	public void setLimit( Integer limit )
	{
		this.limit = limit;
	}

	@Override
	public String toString()
	{
		return "LCAttribute [count=" + count + ", offset=" + offset + ", limit=" + limit + "]";
	}

}

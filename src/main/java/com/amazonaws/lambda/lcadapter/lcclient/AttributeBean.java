package com.amazonaws.lambda.lcadapter.lcclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeBean
{
	
    @JsonProperty("@attributes")
    private LCAttribute attribute;

	@Override
	public String toString()
	{
		return "[" + attribute.toString() + "]";

	}

	public LCAttribute getAttribute()
	{
		return attribute;
	}

	public void setAttribute( LCAttribute attribute )
	{
		this.attribute = attribute;
	}
}

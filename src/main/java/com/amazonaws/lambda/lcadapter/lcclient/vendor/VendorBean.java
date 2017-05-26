package com.amazonaws.lambda.lcadapter.lcclient.vendor;

import com.amazonaws.lambda.lcadapter.lcclient.LCAttribute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorBean
{
	
    @JsonProperty("@attributes")
    private LCAttribute attribute;

    @JsonProperty("Vendor")
    private Vendor vendor;

	public Vendor getVendor()
	{
		return vendor;
	}


	@Override
	public String toString()
	{
		return "[" + vendor.toString() + " ," + attribute.toString() + "]";

	}


	public LCAttribute getAttribute()
	{
		return attribute;
	}
}

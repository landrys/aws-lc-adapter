package com.landry.aws.lambda.lcadapter.lcclient.vendor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.landry.aws.lambda.lcadapter.lcclient.LCAttribute;

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

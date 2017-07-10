package com.landry.aws.lambda.lcadapter.lcclient.vendor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.landry.aws.lambda.lcadapter.lcclient.LCAttribute;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorsBean
{
	

 //   @JsonProperty("Vendor")
// private Vendor vendor;

    @JsonProperty("@attributes")
    private LCAttribute attribute;

    @JsonProperty("Vendor")
    private List<Vendor> vendors;

	public List<Vendor> getVendors()
	{
		return vendors;
	}

	@Override
	public String toString()
	{
		return "[" + vendors.toString() + " ," + attribute.toString() + "]";
	}

	public LCAttribute getAttribute()
	{
		return attribute;
	}

}

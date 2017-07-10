package com.landry.aws.lambda.lcadapter.lcclient.vendor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vendor
{
	
    @JsonProperty("vendorID")
	private  Integer id;

    @JsonProperty("name")
	private  String name;


    @JsonProperty("archived")
	private  Boolean archived;

	public Integer getId()
	{
		return id;
	}

	public void setId( Integer id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Boolean getArchived()
	{
		return archived;
	}

	public void setArchived( Boolean archived )
	{
		this.archived = archived;
	}

	@Override
	public String toString()
	{
		return "Vendor [id=" + id + ", name=" + name + ", archived=" + archived + "]";
	}

}

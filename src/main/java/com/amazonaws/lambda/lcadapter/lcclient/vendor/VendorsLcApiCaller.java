package com.amazonaws.lambda.lcadapter.lcclient.vendor;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.lcadapter.lcclient.InputStreamWrapper;
import com.amazonaws.lambda.lcadapter.lcclient.LcApiCaller;
import com.fasterxml.jackson.databind.JsonMappingException;

public class VendorsLcApiCaller extends LcApiCaller<Vendor>
{
	private String query;
	private List<Vendor> vendors = new ArrayList<Vendor>();

	@Override
	public List<Vendor> get( Integer offset )
	{
		try
		{
			String queryWithOffset = query;

			if (offset != null)
				if (query.contains("?"))
					queryWithOffset = query + "&offset=" + offset;
				else
					queryWithOffset = query + "?offset=" + offset;

			InputStreamWrapper isw = lcApi.getInputStreamSynchronized(queryWithOffset, "GET");

			VendorsBean vsb;
			VendorBean vb;

			try
			{
				vsb = objectMapper.readValue(isw.getIs(), VendorsBean.class);
				if (vsb.getAttribute().getCount() == 0)
					return vendors;
			}
			catch (JsonMappingException e ) // Hack I need since LC calls a single value the same as an array!!!
			{
				isw = lcApi.getInputStreamSynchronized(queryWithOffset, "GET");
			    vb = objectMapper.readValue(isw.getIs(), VendorBean.class);
				if (vb.getAttribute().getCount() != 0)
					vendors.add(vb.getVendor());
			    return vendors;
			}

			if (vendors.size() != 0)
				vendors.addAll(vsb.getVendors());
			else
				vendors = vsb.getVendors();

			if ( vsb.getVendors().size() == 100 )
				get(offset == null ? 100:offset+100 );

			return vendors;

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return vendors;
		}
	}

	public static class Builder
	{
		private String query;

		public Builder query( String query )
		{
			this.query = query;
			return this;
		}

		public VendorsLcApiCaller build()
		{
			return new VendorsLcApiCaller(this);
		}
	}

	private VendorsLcApiCaller(Builder builder)
	{
		this.query = builder.query;
	}
}

package com.landry.aws.lambda.lcadapter.lcclient.vendor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.landry.aws.lambda.lcadapter.lcclient.LcApiCaller;
import com.landry.aws.lambda.lcadapter.lcclient.LcProxyCaller;

public class VendorsCaller extends LcProxyCaller<Vendor>
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

			LcApiCaller lcApiCaller = new LcApiCaller(queryWithOffset);
			String result = lcApiCaller.get();

			VendorsBean vsb;
			VendorBean vb;

			try
			{
				vsb = objectMapper.readValue(result, VendorsBean.class);
				if (vsb.getAttribute().getCount() == 0)
					return vendors;
			}
			catch (JsonMappingException e ) // Hack I need since LC thinks a single value the same as an array!!!
			{
			    //result = service.lcProxy(input);
	     		//LcApiCaller lcApiCaller = new LcApiCaller(queryWithOffset);
		    	result = lcApiCaller.get();
			    vb = objectMapper.readValue(result, VendorBean.class);
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

		public VendorsCaller build()
		{
			return new VendorsCaller(this);
		}
	}

	private VendorsCaller(Builder builder)
	{
		this.query = builder.query;
	}
}

package com.amazonaws.lambda.lcadapter.functions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import com.amazonaws.lambda.lcadapter.lcclient.LcApiCaller;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.Vendor;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.VendorShipTimeUpdater;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.VendorsLcApiCaller;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.landry.aws.lambda.common.invoker.DueDateInvoker;
import com.landry.aws.lambda.common.model.DueDateInput;
import com.landry.aws.lambda.common.model.LCVendorAdapterInput;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeDAO;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeSupportDAO;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;

public class LCVendorAdapter implements RequestHandler<LCVendorAdapterInput, String>
{

	public static final DynamoVendorShipTimeDAO vstDao = DynamoVendorShipTimeDAO.instance();
	private static final DynamoVendorShipTimeSupportDAO vstsDao = DynamoVendorShipTimeSupportDAO.instance();

	@Override
	public String handleRequest( LCVendorAdapterInput input, Context context )
	{

		context.getLogger().log("In with given input: " + input);
		if ( input != null && input.getPing() != null )
			return null;

	    VendorShipTimeSupport maxData = getSupportInfo(VendorShipTimeSupport.MAX_DATA_SUPPORT);
	    VendorShipTimeSupport lastGet = getSupportInfo(VendorShipTimeSupport.LAST_GET_SUPPORT);
	    if ( input.getLastGet() != null && isValid(input.getLastGet()) )
	    	lastGet.setTimestamp(input.getLastGet());


		// First the the current time
		String currentTimeStampString = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));

		context.getLogger().log("This is the last time we called LC: " + currentTimeStampString);


		if (lastGet != null && maxData != null)
		{
			// Get all Vendors Changed since last time we called
			LcApiCaller<Vendor> lcApiCaller = new VendorsLcApiCaller.Builder()
					.query("Vendor?timeStamp=>," + lastGet.getTimestamp() + "&archived=1")
					// .query("Vendor?archived=1")
					 //.query("Vendor?vendorID=1382")
					.build();
			List<Vendor> vendors = lcApiCaller.get(null); // offset of null to get all available

			context.getLogger().log("Got " + vendors.size() + " vendors that have changed.");

			// First lets persist the timestamp to be used in the next call to LC

			context.getLogger().log("Persisting new timestamp: " + currentTimeStampString);
			writeTimestampToDynamo(currentTimeStampString);

			// Next lets see if we have anything new to update
			if (vendors != null && vendors.size() > 0)
			{
				Long nextVSTId = maxData.getId().longValue();
				nextVSTId++;
			    context.getLogger().log("The next vendor ship time id is: " + nextVSTId);
				VendorShipTimeUpdater vstu = new VendorShipTimeUpdater.Builder()
						.vendors(vendors)
						.nextVendorShipTimeId(nextVSTId)
						.build();
				vstu.doWork();
				if ( vstu.persistedChanges() )
					reloadVSTs();
			}
		}

		return "done";

	}

	private void reloadVSTs()
	{
		DueDateInvoker dueDateInvoker = LambdaInvokerFactory.builder()
				.lambdaClient(AWSLambdaClientBuilder.defaultClient()).build(DueDateInvoker.class);
		DueDateInput ddi = new DueDateInput.Builder().reload(true).build();
		dueDateInvoker.dueDate(ddi);
	}

	private boolean isValid( String lastGetGiven )
	{
		try
		{
			ZonedDateTime.parse(lastGetGiven);
			return true;
		}
		catch (Exception e)
		{

			return false;
		}
	}

	private void writeTimestampToDynamo( String currentTimeStampString )
	{
		VendorShipTimeSupport vsts = new VendorShipTimeSupport();
		vsts.setSupport(VendorShipTimeSupport.LAST_GET_SUPPORT);
		vsts.setTimestamp(currentTimeStampString);
		vstsDao.write(vsts);
	}

	private VendorShipTimeSupport getSupportInfo(String support)
	{
		List<VendorShipTimeSupport> vendorShipTimeSupports = getVSTS();

		Iterator<VendorShipTimeSupport> it = vendorShipTimeSupports.iterator();
		while (it.hasNext())
		{
			VendorShipTimeSupport vsts = it.next();
			if (vsts.getSupport().equalsIgnoreCase(support))
				return vsts;
		}
		return null;
	}

	private List<VendorShipTimeSupport> getVSTS()
	{
		return vstsDao.findAll();
		/*
		GentVendorShipTimeSupportsInvoker service = LambdaInvokerFactory.builder()
				.lambdaClient(AWSLambdaClientBuilder.defaultClient()).build(GentVendorShipTimeSupportsInvoker.class);
		Set<VendorShipTimeSupport> vendorShipTimeSupports = service.getVendorShipTimeSupports("");
		return vendorShipTimeSupports;
		*/
	}

}
		/*
		 * DateTimeFormatter formatter =
		 * DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		 * DateTimeFormatter formatter =
		 * DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		 * System.out.println(ZonedDateTime.now().format(formatter ));
		 * 2017-05-23T14:07:50.763-04:00[America/New_York]
		 */
	/*
	private void getSupportInfo()
	{
		Set<VendorShipTimeSupport> vendorShipTimeSupports = getVSTS();

		Iterator<VendorShipTimeSupport> it = vendorShipTimeSupports.iterator();
		while (it.hasNext())
		{
			VendorShipTimeSupport vst = it.next();
			System.out.println(vst.getTimestamp() + ":" + vst.getSupport() + ":" + vst.getCutOffTime());
			if (vst.getSupport().equalsIgnoreCase(VendorShipTimeSupport.LAST_GET_SUPPORT))
			{
				lastGet = vst;
			}
			if (vst.getSupport().equalsIgnoreCase(VendorShipTimeSupport.MAX_DATA_SUPPORT))
			{
				maxData = vst;
			}

		}
	}
	*/

		// Grab the last time we called LC to get Vendors and the max v
		// Vendor Ship Time Id.
		//getSupportInfo();
package com.landry.aws.lambda.lcadapter.functions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.landry.aws.lambda.common.model.LCVendorAdapterInput;
import com.landry.aws.lambda.common.util.LambdaFunctions;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeDAO;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeSupportDAO;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;
import com.landry.aws.lambda.lcadapter.lcclient.LcProxyCaller;
import com.landry.aws.lambda.lcadapter.lcclient.vendor.Vendor;
import com.landry.aws.lambda.lcadapter.lcclient.vendor.VendorShipTimeUpdater;
import com.landry.aws.lambda.lcadapter.lcclient.vendor.VendorsCaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LCVendorAdapter implements RequestHandler<LCVendorAdapterInput, String>
{


    private static final Log logger = LogFactory.getLog(LCVendorAdapter.class);
	public static final DynamoVendorShipTimeDAO vstDao = DynamoVendorShipTimeDAO.instance();
	private static final DynamoVendorShipTimeSupportDAO vstsDao = DynamoVendorShipTimeSupportDAO.instance();

	@Override
	public String handleRequest( LCVendorAdapterInput input, Context context )
	{

        logger.info("Function: " + LambdaFunctions.LC_VENDOR_ADAPTER);
		logger.info("In with given input: " + input);
		if ( input != null && input.getPing() != null )
			return null;

	    VendorShipTimeSupport maxData = getSupportInfo(VendorShipTimeSupport.MAX_DATA_SUPPORT);
	    VendorShipTimeSupport lastGet = getSupportInfo(VendorShipTimeSupport.LAST_GET_SUPPORT);
	    if ( input.getLastGet() != null && isValid(input.getLastGet()) )
	    	lastGet.setTimestamp(input.getLastGet());


		// First the the current time
		String currentTimeStampString = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));

		logger.info("This is the current timestamp: " + currentTimeStampString);
		logger.info("This is the last time we called LC: " + lastGet.getTimestamp());

		if (lastGet != null && maxData != null)
		{
			// Get all Vendors Changed since last time we called
			LcProxyCaller<Vendor> lcApiCaller = new VendorsCaller.Builder()
					.query("Vendor.json?timeStamp=>," + lastGet.getTimestamp() + "&archived=1")
					// .query("Vendor?archived=1")
					 //.query("Vendor?vendorID=1382")
					.build();
			List<Vendor> vendors = lcApiCaller.get(null); // offset of null to get all available

			logger.info("Got " + vendors.size() + " vendors that have changed since"  + lastGet.getTimestamp());

			// First lets persist the timestamp to be used in the next call to LC
			logger.info("Persisting new timestamp: " + currentTimeStampString);
			writeTimestampToDynamo(currentTimeStampString);

			// Next lets see if we have anything new to update
			if (vendors != null && vendors.size() > 0)
			{
				Long nextVSTId = maxData.getId().longValue();
				nextVSTId++;
			    logger.info("The next vendor ship time id is: " + nextVSTId);
				VendorShipTimeUpdater vstu = new VendorShipTimeUpdater.Builder()
						.vendors(vendors)
						.nextVendorShipTimeId(nextVSTId)
						.build();
				vstu.doWork();
			}
		}

		return "Done syncing changes to the VendorShipTime table from LC Vendor table.";

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
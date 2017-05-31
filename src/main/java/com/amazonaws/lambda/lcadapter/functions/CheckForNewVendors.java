package com.amazonaws.lambda.lcadapter.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


import com.amazonaws.lambda.lcadapter.lcclient.LcApiCaller;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.Vendor;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.VendorsLcApiCaller;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeDAO;
import com.landry.aws.lambda.dynamo.domain.VendorShipTime;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;
import com.landry.aws.lambda.dynamo.invoker.GentVendorShipTimeSupportsInvoker;
import com.landry.aws.lambda.dynamo.invoker.WriteVendorShipTimeInvoker;

// OBSOLETE USING ADAPTER
public class CheckForNewVendors implements RequestHandler<String, String> {

	private static final DynamoVendorShipTimeDAO vstDao = DynamoVendorShipTimeDAO.instance();

    @Override
    public String handleRequest(String input, Context context) {

        context.getLogger().log("Input: " + input);
       
        Integer maxVendorId =91000000; 
        Integer maxId =91000000;

        Set<VendorShipTimeSupport> vendorShipTimeSupports = getVSTS();
        Iterator<VendorShipTimeSupport> it = vendorShipTimeSupports.iterator();
        while ( it.hasNext() ) {
        	VendorShipTimeSupport vst = it.next();
        	if ( vst.getSupport().equalsIgnoreCase(VendorShipTimeSupport.MAX_DATA_SUPPORT) ) {
        		maxVendorId = vst.getVendorId();
        		maxId = vst.getId();
        		System.out.println("The gotten max Vendor id is: " + maxVendorId);
        		System.out.println("The gotten max id is: " + maxId);
        		break;
        	}
        }

		LcApiCaller<Vendor> lcApiCaller = new VendorsLcApiCaller.Builder()
				.query("Vendor?vendorID=>," + maxVendorId)
				.build();
        List<Vendor> vendors = lcApiCaller.get(null); // offset of null 
        System.out.println(vendors);
        if ( vendors != null && vendors.size() > 0)
        	writeToDynamo(vendors, maxId.longValue());

        return "done";


	}

	private void writeToDynamo( List<Vendor> vendors, Long maxId )
	{
		// Add one the the max id
		maxId++;
		//WriteVendorShipTimeInvoker service = LambdaInvokerFactory.builder()
		//		.lambdaClient(AWSLambdaClientBuilder.defaultClient()).build(WriteVendorShipTimeInvoker.class);

		for (Vendor vendor : vendors)
		{
			VendorShipTime vst = new VendorShipTime();
			vst.setId(maxId);
			vst.setVendorId(vendor.getId());
			vst.setName(vendor.getName());
			vst.setIsBike(false);
			vst.setWeeklyOrder(false);
			vst.setDropShipToStore(false);
			vstDao.write(vst);
			maxId++;
		}
		
	}

	private Set<VendorShipTimeSupport> getVSTS()
	{
		GentVendorShipTimeSupportsInvoker service = LambdaInvokerFactory.builder()
				.lambdaClient(AWSLambdaClientBuilder.defaultClient()).build(GentVendorShipTimeSupportsInvoker.class);
		Set<VendorShipTimeSupport> vendorShipTimeSupports = service.getVendorShipTimeSupports("");
        return vendorShipTimeSupports;
    }

}

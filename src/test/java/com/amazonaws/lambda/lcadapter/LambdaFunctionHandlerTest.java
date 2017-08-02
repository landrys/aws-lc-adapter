package com.amazonaws.lambda.lcadapter;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landry.aws.lambda.common.model.LCVendorAdapterInput;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeDAO;
import com.landry.aws.lambda.dynamo.dao.DynamoVendorShipTimeSupportDAO;
import com.landry.aws.lambda.dynamo.domain.VendorShipTime;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;
import com.landry.aws.lambda.lcadapter.functions.LCVendorAdapter;
import com.landry.aws.lambda.lcadapter.lcclient.LcProxyCaller;
import com.landry.aws.lambda.lcadapter.lcclient.vendor.Vendor;
import com.landry.aws.lambda.lcadapter.lcclient.vendor.VendorsCaller;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static LCVendorAdapterInput input;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final DynamoVendorShipTimeSupportDAO vstsDao = DynamoVendorShipTimeSupportDAO.instance();
    private static final DynamoVendorShipTimeDAO vstDao = DynamoVendorShipTimeDAO.instance();

    @BeforeClass
    public static void createString() throws IOException {
        // TODO: set up your sample input object here.
        input = new LCVendorAdapterInput();
    }

    private Context createContext() {
        TestContext ctx = new TestContext();
        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");
        return ctx;
    }

	@Test
	public void testLocalDb()
	{
		List<VendorShipTimeSupport> vendorShipTimeSupport = vstsDao.findAll();
		for ( VendorShipTimeSupport vsts : vendorShipTimeSupport )
			System.out.println(vsts);
		
		VendorShipTime vendorShipTime = vstDao.findById(1);
		System.out.println(vendorShipTime);
		

		/*
		List<VendorShipTime> vendorShipTime = vstDao.findAll();
		for ( VendorShipTime vst : vendorShipTime )
			System.out.println(vst);
			*/
	}

    @Test
    public void testLambdaFunctionHandler2() {

        LCVendorAdapter handler = new LCVendorAdapter();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);

        if (output != null) {
            System.out.println(output.toString());
        }

    }

@Test
	public void testLcApiVendorCaller() throws Exception, Exception
	{

		LcProxyCaller<Vendor> lcApiCaller = new VendorsCaller.Builder()
				.query("Vendor?archived=1")
				// .query("Vendor?archived=1")
				// .query("Vendor?vendorID=1382")
				.build();
		List<Vendor> vendors = lcApiCaller.get(null); // offset of null to get
														// all available
		System.out.println(vendors.size());
	}
	
	@Test
	public void outputJson() throws Exception {
		VendorShipTimeSupport vsts = new VendorShipTimeSupport();
		System.out.println(objectMapper.writeValueAsString(vsts));
	}

}

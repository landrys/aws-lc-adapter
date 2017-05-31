package com.amazonaws.lambda.lcadapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.lcadapter.functions.LCVendorAdapter;
import com.amazonaws.lambda.lcadapter.lcclient.AttributeBean;
import com.amazonaws.lambda.lcadapter.lcclient.InputStreamWrapper;
import com.amazonaws.lambda.lcadapter.lcclient.LcApi;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.VendorBean;
import com.amazonaws.lambda.lcadapter.lcclient.vendor.VendorsBean;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.landry.aws.lambda.common.model.LCVendorAdapterInput;
import com.landry.aws.lambda.dynamo.domain.VendorShipTimeSupport;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static LCVendorAdapterInput input;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void createString() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
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
	public void testLcApiMap() throws Exception, Exception
	{
		LcApi lcAPI = new LcApi();
		InputStreamWrapper result = lcAPI.getInputStreamSynchronized("Vendor.json?limit=1", "GET", null);
		//outputRaw(result);

		VendorBean vb;
		VendorsBean vsb;
		try
		{
			vsb = objectMapper.readValue(result.getIs(), VendorsBean.class);
    		System.out.println(vsb.toString());
		}
		catch (JsonMappingException e)
		{
		    result = lcAPI.getInputStreamSynchronized("Vendor.json?vendorID=>,1400", "GET", null);
			vb = objectMapper.readValue(result.getIs(), VendorBean.class);
    		System.out.println(vb.getAttribute().toString());
    		//System.out.println(vb.toString());
		}
		
	}

    @Test
    public void testLcApi() throws Exception, Exception {
		LcApi lcAPI = new LcApi();
		InputStreamWrapper result = lcAPI.getInputStreamSynchronized("/Vendor.json?limit=2", "GET", null);
		outputRaw(result);
	}
    private void outputRaw( InputStreamWrapper result ) throws IOException
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(result.getIs(), "UTF-8"));
	    String line = null;
	    while ((line = r.readLine()) != null) {
	        System.out.println(line);
	    }
	}

	@Test
    public void testLcApiAttributes() throws Exception {
		LcApi lcAPI = new LcApi();
		InputStreamWrapper result = lcAPI.getInputStreamSynchronized("/Vendor.json?limit=2", "GET", null);
		AttributeBean vb = objectMapper.readValue(result.getIs(), AttributeBean.class);
		System.out.println(vb.toString());
	}
	
	@Test
	public void outputJson() throws Exception {
		VendorShipTimeSupport vsts = new VendorShipTimeSupport();
		System.out.println(objectMapper.writeValueAsString(vsts));
		
		
		
	}




}

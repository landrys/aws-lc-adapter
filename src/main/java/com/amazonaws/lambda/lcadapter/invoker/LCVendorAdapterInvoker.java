package com.amazonaws.lambda.lcadapter.invoker;

import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.landry.aws.lambda.common.model.LCVendorAdapterInput;

public interface LCVendorAdapterInvoker
{
	@LambdaFunction(functionName = "lcVendorAdapter")
	String lcVendorAdapter( LCVendorAdapterInput input );
}
package com.amazonaws.lambda.lcadapter.invoker;

import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface LCVendorAdapterInvoker
{
	@LambdaFunction(functionName = "lcVendorAdapter")
	String lcVendorAdapter( String lastGet );
}
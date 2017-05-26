package com.amazonaws.lambda.lcadapter.invoker;

import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface CheckForLCVendorUpdatesInvoker
{
	@LambdaFunction(functionName = "checkForLCVendorUpdates")
	String checkForLCVendorUpdates( String lastGet );
}
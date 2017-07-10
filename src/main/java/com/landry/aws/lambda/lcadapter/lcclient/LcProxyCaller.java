package com.landry.aws.lambda.lcadapter.lcclient;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class LcProxyCaller<T>
{

	protected ObjectMapper objectMapper  = new ObjectMapper();

	public abstract List<T> get( Integer offset);
	
}

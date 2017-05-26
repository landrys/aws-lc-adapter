package com.amazonaws.lambda.lcadapter.lcclient;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class LcApiCaller<T>
{

	//protected String query;
	//private List<T> vendors = new ArrayList<T>();
	protected LcApi lcApi  = new LcApi();
	protected ObjectMapper objectMapper  = new ObjectMapper();

	public abstract List<T> get( Integer offset);
	
}

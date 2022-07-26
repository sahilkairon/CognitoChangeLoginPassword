package com.amazonaws.lambda;

import java.util.Map;

public class APIGatewayProxyResponse {

	private int statusCode;
	private String body;

	public APIGatewayProxyResponse() {
		super();
	}

	public APIGatewayProxyResponse(int statusCode, String body) {
		super();
		this.statusCode = statusCode;
		this.body = body;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}

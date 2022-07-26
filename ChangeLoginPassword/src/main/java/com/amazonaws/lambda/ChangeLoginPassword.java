package com.amazonaws.lambda;

import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ChangeLoginPassword implements RequestHandler<APIGatewayProxyRequest, APIGatewayProxyResponse> {

	private String AccessKey = "AKIAYUCMZEDM5Q3UW7ZH";
	private String SecretKey = "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";
	private String AppClientId = "6t097jurf8m5rhj6gh2cklmik8";
	private String userPoolId = "ap-south-1_xZ7yl7rZn";

	APIGatewayProxyResponse response = new APIGatewayProxyResponse();

	@Override
	public APIGatewayProxyResponse handleRequest(APIGatewayProxyRequest input, Context context) {
		context.getLogger().log("Input: " + input);

		try {
			String body = input.getBody();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(body, Map.class);
			String userName = map.get("emailId");
			String confirmationCode = map.get("confirmationCode");
			String password = map.get("password");

//			Cognito client cerated 

			AWSCredentials cred = new BasicAWSCredentials(AccessKey, SecretKey);
			AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(cred);
			AWSCognitoIdentityProvider client = AWSCognitoIdentityProviderClientBuilder.standard()
					.withCredentials(credProvider).withRegion(Regions.AP_SOUTH_1).build();

			context.getLogger().log("Cognito Client created !");

			if (userName != null && userName.isEmpty() == false) {
				context.getLogger().log("emailId received from the user ");

//				checking if user is registered in userPool 
				AdminGetUserRequest getUser = new AdminGetUserRequest().withUsername(userName)
						.withUserPoolId(userPoolId);

				AdminGetUserResult userResult = client.adminGetUser(getUser);
				context.getLogger().log("get user Result :- " + userResult.toString());


//					proceeding with user change password !

				if (confirmationCode != null && confirmationCode.isEmpty() == false) {
					context.getLogger().log("confirmation code received from the user !");

					ConfirmForgotPasswordRequest confirmpass = new ConfirmForgotPasswordRequest()
							.withClientId(AppClientId).withUsername(userName).withPassword(password)
							.withConfirmationCode(confirmationCode);
					ConfirmForgotPasswordResult confirmed = client.confirmForgotPassword(confirmpass);

					context.getLogger().log(confirmed.toString());

					response.setBody("user password changed successfully !");
					response.setStatusCode(200);

					context.getLogger().log("user password changed successfully !");
					return response;

				} else {
					context.getLogger().log("sending confirmation code to the user !");

					ForgotPasswordRequest pass = new ForgotPasswordRequest().withClientId(AppClientId)
							.withUsername(userName);
					ForgotPasswordResult passResult = client.forgotPassword(pass);

					context.getLogger().log(passResult.toString());
					context.getLogger().log("confirmation code sent to the user !");

					response.setBody("confirmation code sent to user emailId");
					response.setStatusCode(200);
					return response;

				}

			} else {
				context.getLogger().log("Please insert UserName !");

				response.setBody("Please insert UserName !");
				response.setStatusCode(200);
				return response;
			}

		} catch (UserNotFoundException n) {
			context.getLogger().log("user not registered in userPool !");

			response.setBody("This user is not Resgistered, please ask your Admin to register !");
			response.setStatusCode(400);
			return response;

		} catch (ExpiredCodeException expired) {
			context.getLogger().log("Exception occured :- " + expired.getErrorMessage());

			response.setBody("Please provide a valid confirmation code");
			response.setStatusCode(400);
			return response;

		} catch (Exception e) {
			context.getLogger().log("Exception occured :- " + e.getMessage());

			response.setBody("Exception occured :- " + e.getMessage());
			response.setStatusCode(400);
			return response;

		}

	}

}

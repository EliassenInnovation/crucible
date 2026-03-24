package com.eliassen.crucible.demo.unitTests;

//import com.amazonaws.DefaultRequest;
//import com.amazonaws.Request;
//import com.amazonaws.auth.AWS4Signer;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.http.HttpMethodName;


public class AWSHelperTests {
//    public static void main(String[] args)
//    {
//        AWSCredentials credentials = new AWSCredentials() {
//            @Override
//            public String getAWSAccessKeyId() {
//                return AWSHelper.getAWSConfigValue("accessKey");
//            }
//
//            @Override
//            public String getAWSSecretKey() {
//                return AWSHelper.getAWSConfigValue("secretAccessKey");
//            }
//        };
//
//        //create request
//        Request<Void> request = new DefaultRequest<Void>("ec2");
//        request.setHttpMethod(HttpMethodName.GET);
//        request.setEndpoint(URI.create("https://ec2.us-east-2.amazonaws.com"));
//        request.addParameter("Action","DescribeAvailabilityZones");
//        request.addParameter("Version","2016-11-15");
//
//        //sign request
//        AWS4Signer signer = new AWS4Signer();
//        signer.setServiceName(request.getServiceName());
//        signer.setRegionName(AWSHelper.getAWSConfigValue("region"));
//        signer.sign(request, credentials);
//
//
//        CurrentPage.setPageObject(new Common());
//
//        ApiRequest apiRequest = AWSHelper.GetApiRequestFromAWSRequest(request);
//        apiRequest.setHasFormParameters(false);
//
//        HttpsTrustManager.allowAllSSL();
//
//        //apiRequest.parameters = null;
//        //apiRequest.url = "https://ec2.us-east-2.amazonaws.com/?Action=DescribeAvailabilityZones&Version=2016-11-15";
//
//        ApiResponse apiResponse = new ApiHelper().callApi(apiRequest);
//        Logger.log("Framework code response: " + apiResponse.code);
//
//        HttpGet httpGet = new HttpGet("https://ec2.us-east-2.amazonaws.com/?Action=DescribeAvailabilityZones&Version=2016-11-15");
//        for(String key : request.getHeaders().keySet())
//        {
//            httpGet.addHeader(key, request.getHeaders().get(key));
//        }
//
//        HttpResponse response = null;
//
//        try {
//            response = HttpClientBuilder.create().build().execute(httpGet);
//        }
//        catch(Exception e)
//        {}
//
//        Logger.log("Basic httpget response: " + response.getStatusLine().getStatusCode());
//        Logger.log(response.getStatusLine().getReasonPhrase());
//    }
//
//
//

}

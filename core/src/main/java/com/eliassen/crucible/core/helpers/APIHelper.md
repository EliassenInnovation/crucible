# ApiHelper

ApiHelper is a Java utility class designed to simplify API interactions. It provides a set of methods for sending HTTP requests, handling responses, and logging API interactions.

## Features

* Supports HTTP GET, POST, PUT, and DELETE requests
* Handles request and response payloads in JSON format
* Provides logging capabilities for requests and responses
* Allows for customization of API URLs and headers
* Supports data table-driven testing using Cucumber

## Usage

### Sending API Requests

To send an API request, create an instance of `ApiRequest` using the `ApiRequestBuilder`. You can then use the `CallApi` method to send the request and retrieve the response.

```java
ApiInfo apiInfo = new ApiInfo();
apiInfo.apiUrl = "/users";
apiInfo.method = "GET";

Headers headers = new Headers();
headers.put("Authorization", "Bearer token");

ApiRequest request = new ApiRequestBuilder()
        .setMethodType(apiInfo.method)
        .setUrl(createApiUrl(apiInfo))
        .setHeaders(headers)
        .build();

ApiResponse response = ApiHelper.CallApi(request);
```


### Logging API Interactions

ApiHelper provides logging capabilities for requests and responses. You can enable logging by adding the `@logRequest` or `@logResponse` tags to your Cucumber scenarios.

    @logRequest
    Scenario: Get user information
      Given I send a GET request to "/users"
      Then the response status code should be 200


### Data Table-Driven Testing

ApiHelper supports data table-driven testing using Cucumber. You can pass a data table to the `addUrlParametersFromDataTable` method to add URL parameters to your API request.

    Scenario: Get user information with query parameters
      Given I send a GET request to "/users" with query parameters
        | name | value |
        | page | 1     |
        | size | 10    |
      Then the response status code should be 200


Methods
-------

### `CallApi(ApiRequest request)`

Sends an API request and returns the response.

*   `request`: The `ApiRequest` object containing the request details.

### `CallApi(ApiInfo apiInfo, Headers headers, Object[] urlParameters)`

Sends an API request using the provided `ApiInfo` and `Headers` objects.

*   `apiInfo`: The `ApiInfo` object containing the API URL and method.
*   `headers`: The `Headers` object containing the request headers.
*   `urlParameters`: An array of URL parameters to be used in the API URL.

### `logRequest(ApiRequest request)`

Logs the API request details.

*   `request`: The `ApiRequest` object containing the request details.

### `logResponse()`

Logs the API response details.

Dependencies
------------

*   `org.json`: For JSON parsing and manipulation.
*   `io.cucumber`: For Cucumber integration.

Notes
-----

*   ApiHelper is designed to be used with Cucumber and may require modifications to work with other testing frameworks.
*   The logging capabilities are designed to work with Cucumber's logging features. You may need to modify the logging code to work with other logging frameworks.
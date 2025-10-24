README
------

### Overview

This document explains the relationship between `ApiName` (enum), `DemoApiHelper`, and `apiMetaData.json`.

### ApiName Enum

The `ApiName` enum is defined in the `com.eliassen.crucible.demo.helpers.api` package. It contains a list of API names used in the application.

    package com.eliassen.crucible.demo.helpers.api;
    
    public enum ApiName {
        AvatarCharacters,
        CallingCode,
        DescribeAvailabilityZones,
        DemoJar16,
        DemoWebApi,
        ESPNAssociatedLinksApi,
        GetWorstOffender,
        SpecificBuildApi,
        GoodJokes,
        GetTotals,
        GetFails
    }


### apiMetaData.json

The `apiMetaData.json` file contains metadata for the APIs listed in the `ApiName` enum. The exact structure of this file is not shown here, but it is expected to contain information such as API endpoints, request methods, and other relevant details.

### DemoApiHelper

The `DemoApiHelper` class is responsible for interacting with the APIs listed in the `ApiName` enum. It is expected to use the metadata from `apiMetaData.json` to make API calls.

The relationship between these three components is as follows:

*   The `ApiName` enum provides a list of API names that can be used in the application.
*   The `apiMetaData.json` file contains metadata for these APIs, which is used by the `DemoApiHelper` class to make API calls.
*   The `DemoApiHelper` class uses the `ApiName` enum to determine which API to call and retrieves the necessary metadata from `apiMetaData.json` to make the call.

By using this structure, the application can easily add or remove APIs by modifying the `ApiName` enum and updating the `apiMetaData.json` file accordingly. The `DemoApiHelper` class can then be used to interact with these APIs in a standardized way.
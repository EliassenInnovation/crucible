using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Tools;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Xml;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class ApiWorker : WorkerBase
    {
        readonly HttpClient client = DataGrabber.Client;

        public ApiWorker(IConfiguration config) : base(config)
        { }

        public const string JSON_API = "/api/json";
        public const string XML_API = "/api/xml";
        public const string WORKSPACE = "/ws/";
        public const string BUILD_QUEUE = "computer/api/xml?tree=computer[executors[currentExecutable[url]],oneOffExecutors[currentExecutable[url]]]&xpath=//url&wrapper=builds";
        public const string DESCRIPTION_API = "/description";

        public static bool JenkinsIdle
        {
            get
            {
                string apiUrl = BUILD_QUEUE;
                XmlDocument buildStatus = GetXmlFromApi(apiUrl);
                int count = buildStatus.SelectNodes("//url").Count;
                return  count < 1;
            }
        }

        public static XmlDocument GetXmlFromApi(string apiUrl)
        {
            ApiResponse jobFeed = CallApi(ApiWorker.Jenkins + apiUrl);
            XmlDocument xmlDoc = new XmlDocument();
            if(jobFeed != null){
                xmlDoc.LoadXml(jobFeed?.Content);
            }
            return xmlDoc;
        }

        private static string SecureToken
        {
            get
            {
                string token = Config.GetSection("jenkins").GetSection("security").GetSection("token").Value;
                string combinedToken = Username + ":" + token;
                string combinedTokenBase64 = Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(combinedToken));
                return combinedTokenBase64;
            }
        }

        private static string SuperSecureToken
        {
            get
            {
                string token = Config.GetSection("jenkins").GetSection("security").GetSection("supertoken").Value;
                string combinedToken = SuperUserName + ":" + token;
                string combinedTokenBase64 = Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(combinedToken));
                return combinedTokenBase64;
            }
        }

        private static string Username
        {
            get
            {
                return Config.GetSection("jenkins").GetSection("security").GetSection("username").Value;
            }
        }

        private static string SuperUserName
        {
            get
            {
                return Config.GetSection("jenkins").GetSection("security").GetSection("superusername").Value;
            }
        }

        private static bool UseSecurity
        {
            get
            {
                return Config.GetSection("jenkins").GetSection("security").GetValue<bool>("useSecurity");
            }
        }

        public static string Jenkins
        {
            get
            {
#if DEBUG
                return Config.GetSection("jenkins").GetSection("debug_jenkins").Value;
#else
                return Config.GetSection("jenkins").GetSection("jenkins").Value;
#endif
            }
        }

        public static JObject GetJsonFromApi(string apiUrl, Boolean useSecurity = false)
        {
            ApiResponse response = CallApi(apiUrl, useSecurity);
            JObject responseObject = null;
            int tryCount = 0;

            while (responseObject == null && tryCount++ < 10)
            {
                try
                {
                    // Check if response or response.Content is null
                    if (response?.Content == null)
                    {
                        LogWorker.Log("Response or Content is null.");
                        LogWorker.Log("API Url: " + response?.RequestUrl);
                        if (response?.Response != null)
                        {
                            LogWorker.Log("Response Code: " + response.Response.StatusCode);
                        }
                        return responseObject;
                    }

                    // Attempt to parse the content
                    responseObject = JObject.Parse(response.Content);
                }
                catch (ArgumentNullException a)
                {
                    LogWorker.LogError(a);
                    LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                    LogWorker.Log("API Url: " + response?.RequestUrl);
                }
                catch (NullReferenceException n)
                {
                    LogWorker.LogError(n);
                    LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                    LogWorker.Log("API Url: " + response?.RequestUrl);
                }
                catch (JsonReaderException j)
                {
                    LogWorker.LogError(j);
                    LogWorker.Log("Invalid JSON format in response.");
                    LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                    LogWorker.Log("API Url: " + response?.RequestUrl);
                }
            }

            return responseObject;
        }

        public static async Task<JObject> GetJsonFromApiAsync(string apiUrl, bool useSecurity = false)
        {
            try
            {
                using HttpClient client = new HttpClient();
                using HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, apiUrl);

                // Add security headers if needed
                if (useSecurity || UseSecurity)
                {
                    request.Headers.Add("Authorization", $"Basic {SecureToken}");
                }

                // Initiate request but don't wait for the full body to download
                using HttpResponseMessage response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);

                if (!response.IsSuccessStatusCode)
                {
                    LogWorker.Log($"API call failed: {response.StatusCode} - {apiUrl}");
                    return null;
                }

                // Read and stream JSON asynchronously
                using Stream responseStream = await response.Content.ReadAsStreamAsync();
                using StreamReader reader = new StreamReader(responseStream);
                using JsonTextReader jsonReader = new JsonTextReader(reader);

                // Deserialize as a streaming JSON object
                return await Task.Run(() => JObject.Load(jsonReader));
            }
            catch (Exception ex)
            {
                LogWorker.LogError(ex);
                return null;
            }
        }


        public static XmlDocument GetXMLFromApi(string apiUrl, bool useSecurity = false)
        {
            ApiResponse response = CallApi(apiUrl, useSecurity);
            XmlDocument responseDocument = null;

            try
            {
                // Check if response or response.Content is null
                if (response?.Content == null)
                {
                    LogWorker.Log("Response or Content is null.");
                    LogWorker.Log("API Url: " + response?.RequestUrl);
                    if (response?.Response != null)
                    {
                        LogWorker.Log("Response Code: " + response.Response.StatusCode);
                    }
                    return null;
                }

                // Attempt to load the XML content
                responseDocument = new XmlDocument();
                responseDocument.LoadXml(response.Content);
            }
            catch (ArgumentNullException a)
            {
                responseDocument = null;
                LogWorker.LogError(a);
                LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                LogWorker.Log("API Url: " + response?.RequestUrl);
            }
            catch (NullReferenceException n)
            {
                responseDocument = null;
                LogWorker.LogError(n);
                LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                LogWorker.Log("API Url: " + response?.RequestUrl);
            }
            catch (XmlException x)
            {
                responseDocument = null;
                LogWorker.LogError(x);
                LogWorker.Log("Invalid XML format in response.");
                LogWorker.Log("Response Code: " + response?.Response?.StatusCode);
                LogWorker.Log("API Url: " + response?.RequestUrl);
            }

            return responseDocument;
        }

        public static ApiResponse CallApi(string apiUrl, bool useSecurity = false, bool useSuperUser = false, Dictionary<string, string> formParams = null)
        {
            string url = "";
            ApiResponse apiResponse = null;

            try
            {
                if (!apiUrl.Contains(Jenkins))
                {
                    url = ConstructApiUrl(new string[] { Jenkins, apiUrl });
                }
                else
                {
                    url = apiUrl;
                }

                url = Regex.Replace(url, @"(?<!https?:)/{2,}", "/");

                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                // Ignore SSL errors (assuming localhost usage)
                request.ServerCertificateValidationCallback = delegate { return true; };
                apiResponse = new ApiResponse();
                apiResponse.RequestUrl = url;

                if (useSecurity || UseSecurity)
                {
                    if (useSuperUser)
                    {
                        request.Headers.Add("username:" + SuperUserName);
                        request.Headers.Add("Authorization: Basic " + SuperSecureToken);
                    }
                    else
                    {
                        request.Headers.Add("username:" + Username);
                        request.Headers.Add("Authorization: Basic " + SecureToken);
                    }
                }

                // If there are form parameters, send them in the request body
                if (formParams != null && formParams.Count > 0)
                {
                    request.Method = "POST"; // Use POST method for sending form data
                    request.ContentType = "application/x-www-form-urlencoded";

                    // Convert form parameters to URL-encoded string, ensuring empty strings are included
                    string formData = string.Join("&", formParams.Select(kvp =>
                        $"{Uri.EscapeDataString(kvp.Key)}={Uri.EscapeDataString(kvp.Value ?? string.Empty)}"));

                    using (StreamWriter writer = new StreamWriter(request.GetRequestStream()))
                    {
                        writer.Write(formData); // Write the form data to the request body
                    }
                }

                apiResponse.Response = (HttpWebResponse)request.GetResponse();

                using (StreamReader strm = new StreamReader(apiResponse.Response.GetResponseStream()))
                {
                    apiResponse.Content = strm.ReadToEnd();
                }
            }
            catch (Exception e)
            {
                LogWorker.LogError(e);
                LogWorker.Log("URL: " + url);
            }

            return apiResponse;
        }

        internal static ApiResponse CallPostApi(string jobUrl, bool v1, bool v2)
        {
            throw new NotImplementedException();
        }


        public static bool IsBuilding(string buildColor)
        {
            if (!string.IsNullOrEmpty(buildColor) && buildColor.Contains(JenkinsBuild.BUILDING_INDICATOR))
            {
                return true;
            }

            return false;
        }

        public static bool TriggerBuild(string buildPath)
        {
            string buildTriggerUrl = "";

            try
            {
                string token = Config.GetSection("jenkins").GetSection("token").Value;

                string cleanedBuildPath = CleanedBuildNameForTriggering(buildPath);

                buildTriggerUrl = "/buildByToken/build?job=" + cleanedBuildPath + "&token=" + token; 

                ApiResponse response = CallApi(buildTriggerUrl);

                if (response.Response.StatusCode == HttpStatusCode.Created)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception e)
            {
                LogWorker.LogError(e);
                LogWorker.Log("buildTriggerUrl: " + buildTriggerUrl);

                return false;
            }
        }

        private static string CleanedBuildNameForTriggering(string uncleanName)
        {
            string[] nameParts = uncleanName.Split('/');
            StringBuilder cleanedName = new StringBuilder();
            foreach (string part in nameParts)
            {
                if (!part.ToLowerInvariant().Equals("job"))
                {
                    if (cleanedName.Length != 0)
                    {
                        cleanedName.Append("%2F");
                    }
                    cleanedName.Append(part);
                }
            }

            return cleanedName.ToString();
        }

        public static string ConstructApiUrl(string[] apiParts)
        {
            StringBuilder url = new StringBuilder();

            for (int x = 0; x < apiParts.Length; x++)
            {
                if (x > 0 && !apiParts[x - 1].EndsWith("/"))
                {
                    url.Append("/");
                }
                url.Append(apiParts[x]);
            }

            return url.ToString();
        }

        private static HttpWebRequest CreateRequest(string url, bool useSecurity = false, bool useSuperUser = false)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.ServerCertificateValidationCallback = delegate { return true; };

            if (useSecurity || UseSecurity)
            {
                if (useSuperUser)
                {
                    request.Headers.Add("username", SuperUserName);
                    request.Headers.Add("Authorization", $"Basic {SuperSecureToken}");
                }
                else
                {
                    request.Headers.Add("username", Username);
                    request.Headers.Add("Authorization", $"Basic {SecureToken}");
                }
            }

            return request;
        }

        private static ApiResponse SendRequest(HttpWebRequest request)
        {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.RequestUrl = request.RequestUri.ToString();

            try
            {
                apiResponse.Response = (HttpWebResponse)request.GetResponse();

                using (StreamReader strm = new StreamReader(apiResponse.Response.GetResponseStream()))
                {
                    apiResponse.Content = strm.ReadToEnd();
                }
            }
            catch (Exception e)
            {
                LogWorker.LogError(e);
                LogWorker.Log("URL: " + request.RequestUri);
            }

            return apiResponse;
        }

        public static ApiResponse GetApi(string apiUrl, bool useSecurity = false, bool useSuperUser = false)
        {
            string url = ConstructApiUrl(new string[] { Jenkins, apiUrl });
            url = Regex.Replace(url, @"(?<!https?:)/{2,}", "/");

            HttpWebRequest request = CreateRequest(url, useSecurity, useSuperUser);
            request.Method = "GET";

            return SendRequest(request);
        }

        public static ApiResponse PostApi(string apiUrl, bool useSecurity = false, bool useSuperUser = false, Dictionary<string, string> formParams = null)
        {
            string url = ConstructApiUrl(new string[] { Jenkins, apiUrl });
            url = Regex.Replace(url, @"(?<!https?:)/{2,}", "/");

            HttpWebRequest request = CreateRequest(url, useSecurity, useSuperUser);
            request.Method = "POST";

            if (formParams != null && formParams.Count > 0)
            {
                request.ContentType = "application/x-www-form-urlencoded";

                string formData = string.Join("&", formParams.Select(kvp =>
                    $"{Uri.EscapeDataString(kvp.Key)}={Uri.EscapeDataString(kvp.Value ?? string.Empty)}"));

                using (StreamWriter writer = new StreamWriter(request.GetRequestStream()))
                {
                    writer.Write(formData);
                }
            }

            return SendRequest(request);
        }
    }
}

package wang.kidd.http;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author apple
 */
public class RestClient {
    static Logger logger = Logger.getLogger(RestClient.class);

    private static RestClient instance = new RestClient(new HTTP(
            new HTTPConfig()));

    private HTTP httpClient;

    public RestClient(HTTP httpClient) {
        this.httpClient = httpClient;
    }

    public static RestClient getDefault() {
        return instance;
    }

    enum Method {
        POST, PUT, PATCH, GET, DELETE, HEAD, OPTIONS, TRACE
    }

    public Response postForm(String url,
                             Map<String, ? extends CharSequence> headers,
                             Map<String, String> data) throws ClientProtocolException,
            IOException {
        return reqWithForm(Method.POST, url, null, headers, data);
    }

    public Response post(String url,
                         Map<String, ? extends CharSequence> headers, List<File> files)
            throws ClientProtocolException, IOException {
        return uploadFile(Method.POST, url, null, headers, files);
    }

    public Response post(String url,
                         Map<String, ? extends CharSequence> headers, String json)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.POST, url, null, headers, json);
    }

    public Response post(String url,
                         Map<String, ? extends CharSequence> headers,
                         Map<String, Object> data) throws ClientProtocolException,
            IOException {
        return reqWithEntity(Method.POST, url, null, headers, data);
    }

    public Response post(String url, Map<String, Object> data)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.POST, url, null, null, data);
    }

    public Response post(String url, String json)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.POST, url, null, null, json);
    }

    public Response put(String url,
                        Map<String, ? extends CharSequence> headers, String json)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.PUT, url, null, headers, json);
    }

    public Response put(String url,
                        Map<String, ? extends CharSequence> headers,
                        Map<String, Object> data) throws ClientProtocolException,
            IOException {
        return reqWithEntity(Method.PUT, url, null, headers, data);
    }

    public Response put(String url, String json)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.PUT, url, null, null, json);
    }

    public Response put(String url, Map<String, Object> data)
            throws ClientProtocolException, IOException {
        return reqWithEntity(Method.PUT, url, null, null, data);
    }

    public Response get(String url, Map<String, ? extends CharSequence> headers)
            throws ClientProtocolException, IOException {
        return reqNoEntity(Method.GET, url, null, headers);
    }

    public Response get(String url) throws ClientProtocolException, IOException {
        return reqNoEntity(Method.GET, url, null, null);
    }

    public Response delete(String url,
                           Map<String, ? extends CharSequence> headers)
            throws ClientProtocolException, IOException {
        return reqNoEntity(Method.DELETE, url, null, headers);
    }

    public Response delete(String url) throws ClientProtocolException,
            IOException {
        return reqNoEntity(Method.DELETE, url, null, null);
    }

    public static String buildUri(String url, Map<String, ?> params) {
        try {
            URIBuilder ub = new URIBuilder(url);
            if (params != null) {
                for (Entry<String, ?> entry : params.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        ub.addParameter(entry.getKey(), entry.getValue()
                                .toString());
                    }
                }
            }
            return ub.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setHeaders(HttpUriRequest request,
                            Map<String, ? extends CharSequence> headers) {
        if (headers != null) {
            for (Entry<String, ? extends CharSequence> entry : headers
                    .entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    request.addHeader(entry.getKey(), entry.getValue()
                            .toString());
                }
            }
        }
    }

    private Response uploadFile(Method method, String url,
                                Map<String, ? extends CharSequence> params,
                                Map<String, ? extends CharSequence> headers, List<File> list)
            throws ClientProtocolException, IOException {
        HttpEntityEnclosingRequestBase request;
        if (method == Method.POST) {
            request = new HttpPost(buildUri(url, params));
        } else {
            request = new HttpPut(buildUri(url, params));
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int i = 0; i < list.size(); i++) {
            FileBody fileBody = new FileBody(list.get(i));
            builder.addPart("file" + i, fileBody);
        }

        request.setEntity(builder.build());
        Response result = httpClient.execute(request, ResponseHandlers.REST);
        return result;
    }

    private Response reqWithEntity(Method method, String url,
                                   Map<String, ? extends CharSequence> params,
                                   Map<String, ? extends CharSequence> headers, String json)
            throws ClientProtocolException, IOException {
        HttpEntityEnclosingRequestBase request;
        if (method == Method.POST) {
            request = new HttpPost(buildUri(url, params));
        } else {
            request = new HttpPut(buildUri(url, params));
        }

        setHeaders(request, headers);

        if (json != null && !json.isEmpty()) {
            request.setEntity(new StringEntity(json, "utf-8"));
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        }

        Response result = httpClient.execute(request, ResponseHandlers.REST);
        return result;
    }

    private Response reqWithEntity(Method method, String url,
                                   Map<String, ? extends CharSequence> params,
                                   Map<String, ? extends CharSequence> headers,
                                   Map<String, Object> data) throws ClientProtocolException,
            IOException {
        String json = null;
        if (data != null && !data.isEmpty()) {
            json = JsonHelper.bean2json(data);
        }
        return reqWithEntity(method, url, params, headers, json);
    }

    private Response reqNoEntity(Method method, String url,
                                 Map<String, ? extends CharSequence> params,
                                 Map<String, ? extends CharSequence> headers)
            throws ClientProtocolException, IOException {
        HttpUriRequest request;
        if (method == Method.GET) {
            request = new HttpGet(buildUri(url, params));
        } else if (method == Method.DELETE) {
            request = new HttpDelete(buildUri(url, params));
        } else if (method == Method.HEAD) {
            request = new HttpHead(buildUri(url, params));
        } else if (method == Method.OPTIONS) {
            request = new HttpOptions(buildUri(url, params));
        } else {
            request = new HttpTrace(buildUri(url, params));
        }

        setHeaders(request, headers);
        return httpClient.execute(request, ResponseHandlers.REST);
    }

    private Response reqWithForm(Method method, String url,
                                 Map<String, ? extends CharSequence> params,
                                 Map<String, ? extends CharSequence> headers,
                                 Map<String, String> data) throws ClientProtocolException,
            IOException {
        HttpEntityEnclosingRequestBase request;
        if (method == Method.POST) {
            request = new HttpPost(buildUri(url, params));
        } else if (method == Method.PUT) {
            request = new HttpPut(buildUri(url, params));
        } else {
            request = new HttpPatch(buildUri(url, params));
        }

        setHeaders(request, headers);

        if (data != null && !data.isEmpty()) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : data.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
            }
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(formparams,
                    "utf-8");
            request.setEntity(form);
        }

        Response result = httpClient.execute(request, ResponseHandlers.REST);
        if (!result.isSuccess()) {
            logger.warn("error response:");
            logger.warn(result);
            logger.warn("request data:");
            logger.warn(data);
        }
        return result;
    }

    public void close() throws IOException {
        httpClient.close();
    }
}

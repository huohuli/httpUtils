package wang.kidd.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @author apple
 */
public class HTTP {
	private CloseableHttpClient client;

	public HTTP(HTTPConfig config) {
		RequestConfig reqConfig = RequestConfig.custom()
				.setSocketTimeout(config.getSocketTimeout())
				.setConnectTimeout(config.getConnectTimeout()).build();
		client = HttpClients.custom().setDefaultRequestConfig(reqConfig)
				.setConnectionManager(config.getConnectionManager())
				.setRetryHandler(config.getRetryHandler()).build();
	}

	public HttpResponse execute(HttpHost host, HttpRequest request,
                                HttpContext context) throws IOException, ClientProtocolException {
		return client.execute(host, request, context);
	}

	public <T> T execute(HttpHost host, HttpRequest request,
                         ResponseHandler<? extends T> handler, HttpContext context)
			throws IOException, ClientProtocolException {
		return client.execute(host, request, handler, context);
	}

	public <T> T execute(HttpHost host, HttpRequest request,
                         ResponseHandler<? extends T> handler) throws IOException,
            ClientProtocolException {
		return client.execute(host, request, handler);
	}

	public HttpResponse execute(HttpHost host, HttpRequest request)
			throws IOException, ClientProtocolException {
		return client.execute(host, request);
	}

	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		return client.execute(request, context);
	}

	public <T> T execute(HttpUriRequest request,
                         ResponseHandler<? extends T> handler, HttpContext context)
			throws IOException, ClientProtocolException {
		return client.execute(request, handler, context);
	}

	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> handler) throws IOException,
            ClientProtocolException {
		return client.execute(request, handler);
	}

	public HttpResponse execute(HttpUriRequest request) throws IOException,
            ClientProtocolException {
		return client.execute(request);
	}

	public void close() throws IOException {
		client.close();
	}
}

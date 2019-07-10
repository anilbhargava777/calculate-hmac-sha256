package net.bzium.shopify;

import net.bzium.shopify.Hmac;
import net.bzium.shopify.pojo.Cart;
import net.bzium.shopify.pojo.CartItem;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dwuziu on 02/05/17.
 */
public class WebhookServlet extends HttpServlet {

	public static final String HMAC_HEADER = "X-Shopify-Hmac-Sha256";
	public static final String WEBHOOK_TOPIC_HEADER = "x-shopify-topic";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static String secret;

	@Override
	protected void doGet(HttpServletRequest reqest, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().println("Hello World!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out
				.println("\n\n<<--------------- New Webhook Request received --------------->>\n");

		String headerName = null;
		String headerValue = null;
		Map<String, String> headerMap = new HashMap<String, String>();
		Enumeration<String> headerNames = req.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				headerName = headerNames.nextElement();
				headerValue = req.getHeader(headerName);
				headerMap.put(headerName, headerValue);
				System.out.println("Header-- " + headerName + ": "
						+ headerValue);
			}
		}
		String body = req.getReader().lines().collect(Collectors.joining());
		System.out.println("\nRequest body: " + body);

		String webhookHmac = req.getHeader(HMAC_HEADER);
		System.out.println("\nReceived hmac: " + webhookHmac);
		String hmac;
		try {
			hmac = Hmac.calculateHmac(body, secret);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		System.out.println("Calculated hmac: " + hmac);

		doActionOnWebhookTopic(headerMap, body);

		resp.setStatus(200);
		resp.addHeader("Content-Type", "text/plain");
		resp.getWriter().println("Webhook Request Captured..!");
	}

	private void doActionOnWebhookTopic(final Map<String, String> headerMap,
			final String jsonBody) {
		switch (headerMap.get(WEBHOOK_TOPIC_HEADER).toLowerCase()) {
		case "carts/update":
			onCartUpdate(headerMap, jsonBody);
			break;

		default:
			break;
		}
	}

	private void onCartUpdate(final Map<String, String> headerMap,
			final String jsonBody) {
		Cart cart = null;
		try {
			cart = objectMapper.readValue(jsonBody, Cart.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cart != null) {
			for (int i = 0; i < cart.getLine_items().length; i++) {
				System.out.println("Item: " + (i + 1) + " Quantity: "
						+ cart.getLine_items()[i]);
			}
		}
	}

	@Override
	public void init() throws ServletException {
		System.out.println("Servlet " + this.getServletName()
				+ " has started with secret=" + secret);
	}

	@Override
	public void destroy() {
		System.out.println("Servlet " + this.getServletName() + " has stopped");
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new IllegalArgumentException("Missing secret");
		}

		secret = args[0];

		Server server = new Server();

		HttpConfiguration http = new HttpConfiguration();
		http.addCustomizer(new SecureRequestCustomizer());
		http.setSecurePort(8443);
		http.setSecureScheme("https");

		ServerConnector connector = new ServerConnector(server);
		connector.addConnectionFactory(new HttpConnectionFactory(http));
		connector.setPort(8080);

		HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath("/home/ec2-user/store.jks");
		sslContextFactory.setKeyStorePassword("changeit");
		sslContextFactory.setKeyManagerPassword("changeit");

		ServerConnector sslConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, "http/1.1"),
				new HttpConnectionFactory(https));
		sslConnector.setPort(8443);

		server.setConnectors(new Connector[] { connector, sslConnector });

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		handler.addServletWithMapping(WebhookServlet.class, "/*");
		ServletHolder servletHolder = new ServletHolder(new WebhookServlet());
		handler.addServlet(servletHolder);

		server.start();
		server.join();
	}

}

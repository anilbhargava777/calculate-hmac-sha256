package net.bzium.shopify;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;

public class WebhookTest {
	@Test
	public void whenPostJsonUsingHttpClient_thenCorrect() throws IOException {
		String SERVER_URL = "https://h4m9d5b189.execute-api.us-east-1.amazonaws.com/viome";
		final CloseableHttpClient client = HttpClients.createDefault();
		final HttpPost httpPost = new HttpPost(SERVER_URL);

		final String json = "{\"id\":\"6b4be910771741349a1758d31ec90dc9\",\"token\":\"6b4be910771741349a1758d31ec90dc9\","
				+ "\"line_items\":[{\"id\":16879990702126,\"properties\":null,\"quantity\":1,\"variant_id\":16879990702126,"
				+ "\"key\":\"16879990702126:8b6e07c6c0996b7008ab94dfeb303ebd\",\"discounted_price\":\"20.00\",\"discounts\":[],"
				+ "\"gift_card\":false,\"grams\":0,\"line_price\":\"20.00\",\"original_line_price\":\"20.00\","
				+ "\"original_price\":\"20.00\",\"price\":\"20.00\",\"product_id\":1872162062382,\"sku\":\"\",\"taxable\":true,"
				+ "\"title\":\"Viome Test Product 1\",\"total_discount\":\"0.00\",\"vendor\":\"ViomeDemo\","
				+ "\"discounted_price_set\":{\"shop_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"},"
				+ "\"presentment_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"}},\"line_price_set\":{\"shop_money\":{\"amount\":\"20.0\","
				+ "\"currency_code\":\"INR\"},\"presentment_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"}},"
				+ "\"original_line_price_set\":{\"shop_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"},"
				+ "\"presentment_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"}},\"price_set\":{\"shop_money\":{\"amount\":\"20.0\","
				+ "\"currency_code\":\"INR\"},\"presentment_money\":{\"amount\":\"20.0\",\"currency_code\":\"INR\"}},"
				+ "\"total_discount_set\":{\"shop_money\":{\"amount\":\"0.0\",\"currency_code\":\"INR\"},\"presentment_money\":{\"amount\":\"0.0\","
				+ "\"currency_code\":\"INR\"}}}],\"note\":null,\"updated_at\":\"2019-07-10T10:37:28.308Z\",\"created_at\":\"2019-07-08T11:08:16.680Z\"}";
		final StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("x-shopify-topic", "carts/update");
		httpPost.setHeader("x-shopify-hmac-sha256",
				"2e2LPkT7hTjjn+OFNw5a+wrpXT1Z90dBot1Bah3mYUQ=");

		final CloseableHttpResponse response = client.execute(httpPost);
		System.out.println("Status Code: "
				+ response.getStatusLine().getStatusCode());
		client.close();
	}
}

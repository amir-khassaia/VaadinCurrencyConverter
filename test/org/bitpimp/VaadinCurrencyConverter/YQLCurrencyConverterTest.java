package org.bitpimp.VaadinCurrencyConverter;


import org.bitpimp.VaadinCurrencyConverter.YQLCurrencyConverter.JsonReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RunWith(BlockJUnit4ClassRunner.class)
public class YQLCurrencyConverterTest {

	private final YQLCurrencyConverter converter = new YQLCurrencyConverter(
			new JsonReader() {
				@Override
				public JsonElement queryJson(String url) throws Exception {
					// Sample response
					String response = "{\"query\":{\"count\":1,\"created\":\"2013-06-03T04:41:24Z\"," +
							"\"lang\":\"en-US\",\"diagnostics\":{\"publiclyCallable\":\"true\"," +
							"\"url\":{\"execution-start-time\":\"1\",\"execution-stop-time\":\"317\"," +
							"\"execution-time\":\"316\",\"proxy\":\"DEFAULT\",\"content\":" +
							"\"http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?" +
							"FromCurrency=EUR&ToCurrency=GBP\"},\"user-time\":\"317\"," +
							"\"service-time\":\"316\",\"build-version\":\"37040\"}," +
							"\"results\":{\"double\":{\"xmlns\":\"http://www.webserviceX.NET/\"," +
							"\"content\":\"0.855\"}}}}";
					return new JsonParser().parse(response);
				}
			});
	
	@BeforeClass
	public static void init() {
	}
	
	@Test
	public void currencyConverterTestValid() {
		for (int i = 0; i < 10; i++) {
			final String result = converter.convert("EUR", "GBP");
			Assert.assertNotNull(result);
			Assert.assertEquals("0.855", result);
		}
	}
	
	@Test
	public void currencyConverterTestInvalid() {
		Assert.assertNull(converter.convert(null, null));
		Assert.assertNull(converter.convert("", ""));
		Assert.assertNull(converter.convert("AUD", ""));
		Assert.assertNull(converter.convert("a", "a"));
		Assert.assertNull(converter.convert("aaaaa", "aaa"));
	}
	
}

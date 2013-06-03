package org.bitpimp.VaadinCurrencyConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Sample converter service for Yahoo Finance using YQL
 * 
 */
public class YQLCurrencyConverter {

	// Google Guava time-expiring cache instance to minimize the online service abuse
	private final Cache<String, String> rates = CacheBuilder.newBuilder()
		       .maximumSize(1000)
		       .expireAfterWrite(1, TimeUnit.MINUTES)
		       .build();
	
	/** a customisable implementation of the JsonReader that is used by the conversion service */
	private final JsonReader reader;
	
	/**
	 * Simple JSON fetch and parse interface
	 */
	public interface JsonReader {
		public JsonElement queryJson(String url) throws Exception;	
	}
	
	public YQLCurrencyConverter() {
		this(null);
	}
	
	public YQLCurrencyConverter(final JsonReader service) {
		this.reader = service != null ? service : new JsonReader() {
			/**
			 * Default JSON reader just uses the helper to fetch and parse JSON
			 */
			@Override
			public JsonElement queryJson(String url) throws Exception {
				return readJsonFromUrl(url);
			}
		};
	}
	
	/**
	 * The simplified converter service - we only return the rate value where an object
	 * representing various related settings can be returned.
	 * 
	 * @param fromCurrency
	 * @param toCurrency
	 * @return
	 */
	public String convert(final String fromCurrency, final String toCurrency)  {
		if (Strings.isNullOrEmpty(fromCurrency) || Strings.isNullOrEmpty(toCurrency))
			return null;

		// Make a cache key and try from cache first
		final String key = String.format("%s.%s", fromCurrency, toCurrency);
		if (rates.getIfPresent(key) != null)
			return rates.getIfPresent(key);
		try {
			// Invoke the service using YQL
			JsonElement response = reader.queryJson("http://query.yahooapis.com/v1/public/yql?" +
					"q=select%20*%20from%20xml%20where%20url%3D%27" +
					"http%3A%2F%2Fwww.webservicex.net%2FCurrencyConvertor.asmx%2FConversionRate%3F" +
					"FromCurrency%3D" + fromCurrency + "%26ToCurrency%3D" + toCurrency + 
					"%27&format=json&diagnostics=true");
			if (!(response instanceof JsonObject))
				return null;
			JsonObject json = response.getAsJsonObject();
			
			// Parse the results 
			// (very simplistic as just going after the single exchange rate value)
			JsonObject query = json.getAsJsonObject("query");
			if (query == null || query.get("results") instanceof JsonNull)
				return null;
			JsonObject results = query.getAsJsonObject("results");
			JsonElement rate = results != null ? results.getAsJsonObject("double").get("content") : null;
			String value = rate != null ? rate.getAsString() : null;
			
			// Store in the cache
			if (value != null)
				rates.put(key, value);
			return value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	private static JsonObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, 
            		Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JsonElement jelement = new JsonParser().parse(jsonText);
            JsonObject jobject = jelement.getAsJsonObject();
            return jobject;
        } finally {
            is.close();
        }
    }
	
	private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}

/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ebayopensource.fidouafclient.curl;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Curl {

	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	// 創建一個共享的 OkHttpClient 實例（更高效）
	// 默認配置：支持 HTTPS，30秒超時
	private static final OkHttpClient httpClient = createHttpClient();

	/**
	 * 創建 HTTP 客戶端
	 * 支持 HTTPS 和自簽名證書（類似原來的實現）
	 */
	private static OkHttpClient createHttpClient() {
		try {
			// 創建信任所有證書的 TrustManager（注意：生產環境不推薦）
			final TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
						}

						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return new java.security.cert.X509Certificate[]{};
						}
					}
			};

			// 安裝信任所有證書的 TrustManager
			final SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

			// 創建允許所有主機名的 HostnameVerifier
			final HostnameVerifier allowAllHostnames = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			return new OkHttpClient.Builder()
					.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
					.hostnameVerifier(allowAllHostnames)
					.connectTimeout(30, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.writeTimeout(30, TimeUnit.SECONDS)
					.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 將 Response 轉換為字符串
	 */
	private static String toStr(Response response) {
		try {
			if (response.body() != null) {
				return response.body().string();
			}
			return "";
		} catch (IOException ex) {
			ex.printStackTrace();
			return "Error";
		}
	}

	/**
	 * 在單獨的線程中執行 GET 請求
	 */
    //TODO:DEPRECATE方法要改成用OKHTTP方法
	public static String getInSeparateThread(String url) {
		GetAsyncTask async = new GetAsyncTask();
		async.execute(url);
		while (!async.isDone()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return async.getResult();
	}

	/**
	 * 在單獨的線程中執行 POST 請求
	 */
	public static String postInSeparateThread(String url, String header, String data) {
		PostAsyncTask async = new PostAsyncTask();
		async.execute(url, header, data);
		while (!async.isDone()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return async.getResult();
	}

	/**
	 * 執行 GET 請求（無自定義 headers）
	 */
	public static String get(String url) {
		return get(url, null);
	}

	/**
	 * 執行 GET 請求（帶自定義 headers）
	 * @param url 請求的 URL
	 * @param headers 自定義 headers 陣列，格式："Header-Name:Header-Value"
	 */
	public static String get(String url, String[] headers) {
		String ret = "";
		try {
			Request.Builder requestBuilder = new Request.Builder()
					.url(url);

			// 添加自定義 headers
			if (headers != null) {
				for (String h : headers) {
					String[] split = h.split(":", 2);
					if (split.length == 2) {
						requestBuilder.addHeader(split[0].trim(), split[1].trim());
					}
				}
			}

			Request request = requestBuilder.build();

			try (Response response = httpClient.newCall(request).execute()) {
				ret = toStr(response);
			} catch (IOException ex) {
				ex.printStackTrace();
				ret = "{'error_code':'connect_fail','url':'" + url + "'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = "{'error_code':'connect_fail','e':'" + e + "'}";
		}

		return ret;
	}

	/**
	 * 執行 POST 請求（header 以空格分隔的字符串）
	 */
	public static String post(String url, String header, String data) {
		if (header != null && !header.isEmpty()) {
			return post(url, header.split(" "), data);
		}
		return post(url, (String[]) null, data);
	}

	/**
	 * 執行 POST 請求（帶自定義 headers）
	 * @param url 請求的 URL
	 * @param headers 自定義 headers 陣列，格式："Header-Name:Header-Value"
	 * @param data 請求體數據
	 */
	public static String post(String url, String[] headers, String data) {
		String ret = "";
		try {
			// 創建請求體
			RequestBody body = RequestBody.create(data, MEDIA_TYPE_JSON);

			Request.Builder requestBuilder = new Request.Builder()
					.url(url)
					.post(body);

			// 添加自定義 headers
			if (headers != null) {
				for (String h : headers) {
					String[] split = h.split(":", 2);
					if (split.length == 2) {
						requestBuilder.addHeader(split[0].trim(), split[1].trim());
					}
				}
			}

			Request request = requestBuilder.build();

			try (Response response = httpClient.newCall(request).execute()) {
				ret = toStr(response);
			} catch (IOException ex) {
				ex.printStackTrace();
				ret = "{'error_code':'connect_fail','url':'" + url + "'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = "{'error_code':'connect_fail','e':'" + e + "'}";
		}

		return ret;
	}
}

/**
 * AsyncTask for GET requests
 */
class GetAsyncTask extends AsyncTask<String, Integer, String> {

	private String result = null;
	private boolean done = false;

	public boolean isDone() {
		return done;
	}

	public String getResult() {
		return result;
	}

	@Override
	protected String doInBackground(String... args) {
		result = Curl.get(args[0]);
		done = true;
		return result;
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(String result) {
		this.result = result;
		done = true;
	}
}

/**
 * AsyncTask for POST requests
 */
class PostAsyncTask extends AsyncTask<String, Integer, String> {

	private String result = null;
	private boolean done = false;

	public boolean isDone() {
		return done;
	}

	public String getResult() {
		return result;
	}

	@Override
	protected String doInBackground(String... args) {
		result = Curl.post(args[0], args[1], args[2]); // (url, header, data)
		done = true;
		return result;
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	@Override
	protected void onPostExecute(String result) {
		this.result = result;
		done = true;
	}
}

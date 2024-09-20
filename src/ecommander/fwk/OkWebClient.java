package ecommander.fwk;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.brotli.dec.BrotliInputStream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class OkWebClient {
	private static final String UTF_8 = "UTF-8";
	private static final Pattern URL_ENCODED_PATTERN = Pattern.compile("%[0-9a-d]{2}");

	private static OkWebClient instance;

	private OkHttpClient client = null;
	//private HttpClientContext httpContext = null;
	private BasicCookieStore cookieStore = null;


	private void startSession() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		/*
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
				NoopHostnameVerifier.INSTANCE);

		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		cookieStore = new BasicCookieStore();

		// automatically follow redirects
		client = HttpClients
				.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setDefaultRequestConfig(requestConfig)
				.setSSLSocketFactory(scsf)
				.setDefaultCookieStore(cookieStore)
				.build();

		 */
	}

	private OkWebClient() {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(new UnzippingInterceptor());
		client = clientBuilder.readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();
	}

	public static OkWebClient getInstance() {
		if (instance == null)
			instance = new OkWebClient();
		return instance;
	}


	private void prepareHeadersAndProxies(HttpRequestBase request, String...proxy) {

//		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:100.0) Gecko/20100101 Firefox/100.0");
//		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
//		request.setHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//		request.setHeader("Accept-Encoding", "gzip, deflate, br");
//		request.setHeader("DNT", "1");
//		request.setHeader("Connection", "keep-alive");
//		request.setHeader("Upgrade-Insecure-Requests", "1");
//		request.setHeader("Sec-Fetch-Dest", "document");
//		request.setHeader("Sec-Fetch-Mode", "navigate");
//		request.setHeader("Sec-Fetch-Site", "none");
//		request.setHeader("Sec-Fetch-User", "?1");
//		request.setHeader("Pragma", "no-cache");
//		request.setHeader("Cache-Control", "no-cache");

		request.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
		request.setHeader("accept-encoding", "gzip, deflate, br");
		request.setHeader("accept-language", "en-US,en;q=0.9,ru;q=0.8");
		request.setHeader("cache-control", "no-cache");
		request.setHeader("device-memory", "8");
		request.setHeader("downlink", "5.55");
		request.setHeader("dpr", "1");
		request.setHeader("ect", "4g");
		request.setHeader("pragma", "no-cache");
		request.setHeader("rtt", "250");
		request.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
		request.setHeader("sec-ch-ua-mobile", "?0");
		request.setHeader("sec-ch-ua-platform", "\"Windows\"");
		request.setHeader("sec-fetch-dest", "document");
		request.setHeader("sec-fetch-mode", "navigate");
		request.setHeader("sec-fetch-site", "same-origin");
		request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		request.setHeader("upgrade-insecure-requests", "1");
		request.setHeader("viewport-width", "1280");
		request.setHeader("Connection", "keep-alive");
//		request.setHeader("referer", "https://www.digikey.com/en/products/filter/accessories/800");

		//request.setHeader("x-requested-with", "XMLHttpRequest");



		if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
			HttpHost proxyHost = new HttpHost(proxy[0]);
			RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
			request.setConfig(config);
		}
		if (!cookieStore.getCookies().isEmpty()) {
			StringBuilder cookies = new StringBuilder();
			for (Cookie cookie : cookieStore.getCookies()) {
				if (cookies.length() > 0)
					cookies.append("; ");
				cookies.append(cookie.getName()).append('=').append(cookie.getValue());
			}
			request.setHeader("Cookie", cookies.toString());
		}
	}


	public byte[] getBytes(String url) throws IOException {
		Request request = new Request.Builder().url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
				.addHeader("Accept-Language", "en-US,en;q=0.8,en-US;q=0.5,en;q=0.3")
				.addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cookie", "_pxhd=0733810ce31f40c79265ce9f576636fcda590d9600b5285eae6201552b9de078:402cb68e-7763-11ef-8615-068ead46b13c; pf-accept-language=en-US; ping-accept-language=en-US; TS01c02def=01dea9a590919857de4dc27bcb91e9eab4ce8f0e712e92914397771877267738dcdf52eda9cb3606a42a9d1f2ebe65896639a75887; TS0178f15d=01dea9a590919857de4dc27bcb91e9eab4ce8f0e712e92914397771877267738dcdf52eda9cb3606a42a9d1f2ebe65896639a75887; TS809e22c5027=081244aa86ab20001394d803e3dab23d4c63d86aa2f6615aef54c263a6bd37244bc835bd7db4d1450801ceffdc113000fa3cb23d8f0cb8613fd7756acb145168c4b349eeec9675ff311db59bd848f28a5befb99d537c194c0d73b5d00c1b35c4; TS01cba742=01c72bed21f265565b08bca0ff2e31f08e5e5191f183a6edbdb2a7ef6cb7c637ccee34bb6f30ac7a4d3493beb99cb88486451e6f42; TS01737bf0=01c72bed21f265565b08bca0ff2e31f08e5e5191f183a6edbdb2a7ef6cb7c637ccee34bb6f30ac7a4d3493beb99cb88486451e6f42; TS605a4192027=08a1509f8aab20002e3e949e70103a04df75934c379dab8bb38d270b3cc2cbc17f580e8a3478a3ab080fff903c113000879099b3536e2253ad0a027eca4e9c7af66e3ebb2411ec10a26e2d59895eb3cb7c4bf0baaecbfe2d3112418d27b96f0c; TS016ca11c=01c72bed21f5b8d2e7be7a627f5ee71d0f9522cb33d9b2d0c5faa3adb3d1a68569d6fa76b425540522df6b99721ae424dddae0908a; TS01d47dae=01c72bed21f5b8d2e7be7a627f5ee71d0f9522cb33d9b2d0c5faa3adb3d1a68569d6fa76b425540522df6b99721ae424dddae0908a; TSc580adf4027=08a1509f8aab2000114b874d0e68d0ae2ec9405f74659091caf9b8d6cc17eab6bf6d1b234df03ff9085182b51911300050a8bb7518298f429c6aa34db50ed06357908cf9c7cc9b00ffde1bbd384f4efece7123d4701d51bae4f09b6ab4512848; utag_main=v_id:01921001232500349ea8fcfdde9c05050005c00d00c48$_sn:1$_se:4$_ss:0$_st:1726847185109$ses_id:1726845362981%3Bexp-session$_pn:1%3Bexp-session; ai_user=fV2gjn3V3GiNJyF64sOjJI|2024-09-20T15:16:03.023Z; dkc_tracker=3658657579570; digikey_theme=dklt; _dd_s=rum=0&expire=1726846304603; TS019f5e6c=01c72bed21fc146d320500333713fd01370fbd28bb6ff7a4eee3fb96aafdd8926292c4453c29777f80388e96262193e8fc40eb6445; TSbafe380b027=08a1509f8aab2000b0cf7932292adc714b5671997f0e4e1b613f96d58a341543e255c9bb91cef1ea088465384f113000e5cf1161c59786189c6aa34db50ed0633e3f32ffec4c414737250ac07143c7a69a12671a08b6e1956a626745756a7d59; website#lang=en-US; TS0198bc0d=01c72bed212e0ea5939cbd437b283443788a80f045f34f1af89f05706daaf89a8d543f3219bdc6177b168f893cf9c69805cad9893d; TSe14c7dc7027=08a1509f8aab2000135bf01b9119007fb759de5ead9df885c271f3af8ad3836e1b84609a75a18cc908cff0f056113000f3edda92b44527ff9c6aa34db50ed063dc6acf4f65df7a855d4269160c0187dbb6f3307270df70c849dff8245fe12752; dk_sft_ses=c615a95d-7696-4814-9443-1ad1bf84632d; dk_tagxi=undefined.0.undefined; dk_ref_data_x=ref_page_type=PS&ref_page_sub_type=PD&ref_page_id=PD&ref_page_url=https://www.digikey.com/en/products/detail/w%C3%BCrth-elektronik/875105242010/5147574&ref_content_search_keywords=undefined; ai_session=3ety/g3b87aAupk8gc+475|1726845363650|1726845363650; pxcts=409ac3e9-7763-11ef-81c1-dce6b2e211e8; _pxvid=402cb68e-7763-11ef-8615-068ead46b13c; _pxde=c286512c30670a2e645d93c2bd1c338d6236b32820229eebb91d3d51d5604371:eyJ0aW1lc3RhbXAiOjE3MjY4NDUzODY1NzEsImZfa2IiOjAsImlwY19pZCI6W119; EG-U-ID=E9a9e5374b-7541-47b2-b4dc-5676b21ee904; EG-S-ID=A0a31a1a8e-dd64-4633-95ba-4bc17cebfd19; OptanonConsent=isGpcEnabled=1&datestamp=Fri+Sep+20+2024+18%3A16%3A12+GMT%2B0300+(%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D0%BE%D0%B5+%D0%B2%D1%80%D0%B5%D0%BC%D1%8F)&version=202407.2.0&browserGpcFlag=1&isIABGlobal=false&hosts=&consentId=78d04d25-f121-45f6-a597-4fd691f8dd0b&interactionCount=1&isAnonUser=1&landingPath=NotLandingPage&groups=C0001%3A1%2CC0003%3A1%2CC0002%3A1%2CC0004%3A1&intType=1; _px2=eyJ1IjoiM2ZjMDljYjAtNzc2My0xMWVmLWJlNzEtZTkzNWYwNzJiMTU4IiwidiI6IjQwMmNiNjhlLTc3NjMtMTFlZi04NjE1LTA2OGVhZDQ2YjEzYyIsInQiOjE3MjY4NDU2NjU1NDgsImgiOiI3NDBjY2JmMDIyMTU2NGUyODI3MzAxM2UwY2FiOTRhNzBmODU1NmRiNWM5MWQ4NDg5YzNjZTYyMDlkMWM3ZTQ1In0=; OptanonAlertBoxClosed=2024-09-20T15:16:11.960Z; _ga=GA1.1.1654993483.1726845364; _ga_0434Z4NCVG=GS1.1.1726845363.1.1.1726845385.46.0.0; _gcl_au=1.1.325992500.1726845372; _cs_mk=0.05236125171174111_1726845373646; TS01173021=01f9ef228df5a3dd6f7ac524d01f8342c866ba17b06558defa2ab6477fb71bf8d4486552abd58f8635a9e6bdaa4ed547726ba64913; TScaafd3c3027=08205709cbab20008e0eddfa6726f99f1175552d7a26053670f69b3b03ccc39f8240ecdb9e528027083a4abdf2113000511bd17453113b7779efebdfd26de5c1d4b55606eaa27de299156518f1321315e571cd70e48db1a3db72d13831babfad; dk_item_data=vil=ps-fam_69; TSd6475659027=08e0005159ab20001507b9526697e7e463392e445edbfdfbefeb292ca0fc73ae6bb504e6141ca8f508f3f713601130006f1140a718ee09aa7d33a2404d4700cfe2ac500847fb0cd89a7dd07296f17e8bee1f61066dc400363e58a20d29ec7ee1; utm_data_x=html_element1%3Dbreadcrumb-link-Aluminum%20-%20Polymer%20Capacitors%2Chtml_element2%3DMuiBreadcrumbs-li%2Chtml_element3%3Dtss-css-1fxim6f-container%2Chtml_element4%3DMuiGrid-root%20MuiGrid-item%20MuiGrid-grid-xxs-12%20MuiGrid-grid-sm-8%20MuiGrid-grid-md-8%20mui-css-v65x6o%2Cundefined%3DMuiGrid-root%20MuiGrid-container%20mui-css-vli43w%2CExtRun%3D450.1%7C409.2%2Cref_page_type%3DPS%2Cref_page_sub_type%3DPD%2Cref_page_id%3DPD%2Cccookie%3D2024-09-20T15%3A16%3A02.980Z%2Cref_part_id%3D5147574%2Cref_pn_sku%3D732-6410-2-ND%2Cref_supplier_id%3D732%2Cref_page_state%3DShow%20Packaging%20Options%40%40%20Parts%20In%20Stock%40%40Substitutes%40%40Substitute%20-%20Kit-Parent%40%40Substitute%20-%20AlsoEvaluated-AlsoEvaluated%2Cref_pers_state%3D%7B%7D%2Cref_part_search_term%3D%2Cref_part_search_term_ext%3D%2CExtRun%3D450.1%7C409.2%7C429.2%7C428.1%7C428.5")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("Sec-Fetch-Dest", "document")
				.addHeader("Sec-Fetch-Mode", "navigate")
				.addHeader("Sec-Fetch-Site", "none")
				.addHeader("Sec-Fetch-User", "?1")
				.build();
		try (Response response = client.newCall(request).execute()) {
//			byte[] bytes = response.body().bytes();
//			String result1 = new String(bytes, StandardCharsets.UTF_8);
//			String result2 = new String(bytes, StandardCharsets.UTF_16);
//			String result3 = new String(bytes, StandardCharsets.ISO_8859_1);
//			String result4 = new String(bytes, StandardCharsets.US_ASCII);
//			String result5 = new String(bytes, StandardCharsets.UTF_16BE);
//			String result6 = new String(bytes, StandardCharsets.UTF_16LE);
//			System.out.println(result1 + result2 + result3 + result4 + result5 + result6);
			return response.body().bytes();
		}
	}

	public String getString(String url) throws IOException {
		Request request = new Request.Builder().url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
				.addHeader("Accept-Language", "en-US,en;q=0.8,en-US;q=0.5,en;q=0.3")
				.addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cookie", "_pxhd=0733810ce31f40c79265ce9f576636fcda590d9600b5285eae6201552b9de078:402cb68e-7763-11ef-8615-068ead46b13c; pf-accept-language=en-US; ping-accept-language=en-US; TS01c02def=01dea9a590919857de4dc27bcb91e9eab4ce8f0e712e92914397771877267738dcdf52eda9cb3606a42a9d1f2ebe65896639a75887; TS0178f15d=01dea9a590919857de4dc27bcb91e9eab4ce8f0e712e92914397771877267738dcdf52eda9cb3606a42a9d1f2ebe65896639a75887; TS809e22c5027=081244aa86ab20001394d803e3dab23d4c63d86aa2f6615aef54c263a6bd37244bc835bd7db4d1450801ceffdc113000fa3cb23d8f0cb8613fd7756acb145168c4b349eeec9675ff311db59bd848f28a5befb99d537c194c0d73b5d00c1b35c4; TS01cba742=01c72bed21f265565b08bca0ff2e31f08e5e5191f183a6edbdb2a7ef6cb7c637ccee34bb6f30ac7a4d3493beb99cb88486451e6f42; TS01737bf0=01c72bed21f265565b08bca0ff2e31f08e5e5191f183a6edbdb2a7ef6cb7c637ccee34bb6f30ac7a4d3493beb99cb88486451e6f42; TS605a4192027=08a1509f8aab20002e3e949e70103a04df75934c379dab8bb38d270b3cc2cbc17f580e8a3478a3ab080fff903c113000879099b3536e2253ad0a027eca4e9c7af66e3ebb2411ec10a26e2d59895eb3cb7c4bf0baaecbfe2d3112418d27b96f0c; TS016ca11c=01c72bed21f5b8d2e7be7a627f5ee71d0f9522cb33d9b2d0c5faa3adb3d1a68569d6fa76b425540522df6b99721ae424dddae0908a; TS01d47dae=01c72bed21f5b8d2e7be7a627f5ee71d0f9522cb33d9b2d0c5faa3adb3d1a68569d6fa76b425540522df6b99721ae424dddae0908a; TSc580adf4027=08a1509f8aab2000114b874d0e68d0ae2ec9405f74659091caf9b8d6cc17eab6bf6d1b234df03ff9085182b51911300050a8bb7518298f429c6aa34db50ed06357908cf9c7cc9b00ffde1bbd384f4efece7123d4701d51bae4f09b6ab4512848; utag_main=v_id:01921001232500349ea8fcfdde9c05050005c00d00c48$_sn:1$_se:4$_ss:0$_st:1726847185109$ses_id:1726845362981%3Bexp-session$_pn:1%3Bexp-session; ai_user=fV2gjn3V3GiNJyF64sOjJI|2024-09-20T15:16:03.023Z; dkc_tracker=3658657579570; digikey_theme=dklt; _dd_s=rum=0&expire=1726846304603; TS019f5e6c=01c72bed21fc146d320500333713fd01370fbd28bb6ff7a4eee3fb96aafdd8926292c4453c29777f80388e96262193e8fc40eb6445; TSbafe380b027=08a1509f8aab2000b0cf7932292adc714b5671997f0e4e1b613f96d58a341543e255c9bb91cef1ea088465384f113000e5cf1161c59786189c6aa34db50ed0633e3f32ffec4c414737250ac07143c7a69a12671a08b6e1956a626745756a7d59; website#lang=en-US; TS0198bc0d=01c72bed212e0ea5939cbd437b283443788a80f045f34f1af89f05706daaf89a8d543f3219bdc6177b168f893cf9c69805cad9893d; TSe14c7dc7027=08a1509f8aab2000135bf01b9119007fb759de5ead9df885c271f3af8ad3836e1b84609a75a18cc908cff0f056113000f3edda92b44527ff9c6aa34db50ed063dc6acf4f65df7a855d4269160c0187dbb6f3307270df70c849dff8245fe12752; dk_sft_ses=c615a95d-7696-4814-9443-1ad1bf84632d; dk_tagxi=undefined.0.undefined; dk_ref_data_x=ref_page_type=PS&ref_page_sub_type=PD&ref_page_id=PD&ref_page_url=https://www.digikey.com/en/products/detail/w%C3%BCrth-elektronik/875105242010/5147574&ref_content_search_keywords=undefined; ai_session=3ety/g3b87aAupk8gc+475|1726845363650|1726845363650; pxcts=409ac3e9-7763-11ef-81c1-dce6b2e211e8; _pxvid=402cb68e-7763-11ef-8615-068ead46b13c; _pxde=c286512c30670a2e645d93c2bd1c338d6236b32820229eebb91d3d51d5604371:eyJ0aW1lc3RhbXAiOjE3MjY4NDUzODY1NzEsImZfa2IiOjAsImlwY19pZCI6W119; EG-U-ID=E9a9e5374b-7541-47b2-b4dc-5676b21ee904; EG-S-ID=A0a31a1a8e-dd64-4633-95ba-4bc17cebfd19; OptanonConsent=isGpcEnabled=1&datestamp=Fri+Sep+20+2024+18%3A16%3A12+GMT%2B0300+(%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D0%BE%D0%B5+%D0%B2%D1%80%D0%B5%D0%BC%D1%8F)&version=202407.2.0&browserGpcFlag=1&isIABGlobal=false&hosts=&consentId=78d04d25-f121-45f6-a597-4fd691f8dd0b&interactionCount=1&isAnonUser=1&landingPath=NotLandingPage&groups=C0001%3A1%2CC0003%3A1%2CC0002%3A1%2CC0004%3A1&intType=1; _px2=eyJ1IjoiM2ZjMDljYjAtNzc2My0xMWVmLWJlNzEtZTkzNWYwNzJiMTU4IiwidiI6IjQwMmNiNjhlLTc3NjMtMTFlZi04NjE1LTA2OGVhZDQ2YjEzYyIsInQiOjE3MjY4NDU2NjU1NDgsImgiOiI3NDBjY2JmMDIyMTU2NGUyODI3MzAxM2UwY2FiOTRhNzBmODU1NmRiNWM5MWQ4NDg5YzNjZTYyMDlkMWM3ZTQ1In0=; OptanonAlertBoxClosed=2024-09-20T15:16:11.960Z; _ga=GA1.1.1654993483.1726845364; _ga_0434Z4NCVG=GS1.1.1726845363.1.1.1726845385.46.0.0; _gcl_au=1.1.325992500.1726845372; _cs_mk=0.05236125171174111_1726845373646; TS01173021=01f9ef228df5a3dd6f7ac524d01f8342c866ba17b06558defa2ab6477fb71bf8d4486552abd58f8635a9e6bdaa4ed547726ba64913; TScaafd3c3027=08205709cbab20008e0eddfa6726f99f1175552d7a26053670f69b3b03ccc39f8240ecdb9e528027083a4abdf2113000511bd17453113b7779efebdfd26de5c1d4b55606eaa27de299156518f1321315e571cd70e48db1a3db72d13831babfad; dk_item_data=vil=ps-fam_69; TSd6475659027=08e0005159ab20001507b9526697e7e463392e445edbfdfbefeb292ca0fc73ae6bb504e6141ca8f508f3f713601130006f1140a718ee09aa7d33a2404d4700cfe2ac500847fb0cd89a7dd07296f17e8bee1f61066dc400363e58a20d29ec7ee1; utm_data_x=html_element1%3Dbreadcrumb-link-Aluminum%20-%20Polymer%20Capacitors%2Chtml_element2%3DMuiBreadcrumbs-li%2Chtml_element3%3Dtss-css-1fxim6f-container%2Chtml_element4%3DMuiGrid-root%20MuiGrid-item%20MuiGrid-grid-xxs-12%20MuiGrid-grid-sm-8%20MuiGrid-grid-md-8%20mui-css-v65x6o%2Cundefined%3DMuiGrid-root%20MuiGrid-container%20mui-css-vli43w%2CExtRun%3D450.1%7C409.2%2Cref_page_type%3DPS%2Cref_page_sub_type%3DPD%2Cref_page_id%3DPD%2Cccookie%3D2024-09-20T15%3A16%3A02.980Z%2Cref_part_id%3D5147574%2Cref_pn_sku%3D732-6410-2-ND%2Cref_supplier_id%3D732%2Cref_page_state%3DShow%20Packaging%20Options%40%40%20Parts%20In%20Stock%40%40Substitutes%40%40Substitute%20-%20Kit-Parent%40%40Substitute%20-%20AlsoEvaluated-AlsoEvaluated%2Cref_pers_state%3D%7B%7D%2Cref_part_search_term%3D%2Cref_part_search_term_ext%3D%2CExtRun%3D450.1%7C409.2%7C429.2%7C428.1%7C428.5")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("Sec-Fetch-Dest", "document")
				.addHeader("Sec-Fetch-Mode", "navigate")
				.addHeader("Sec-Fetch-Site", "none")
				.addHeader("Sec-Fetch-User", "?1")
				.build();
		try (Response response = client.newCall(request).execute()) {
//			byte[] bytes = response.body().bytes();
//			String result1 = new String(bytes, StandardCharsets.UTF_8);
//			String result2 = new String(bytes, StandardCharsets.UTF_16);
//			String result3 = new String(bytes, StandardCharsets.ISO_8859_1);
//			String result4 = new String(bytes, StandardCharsets.US_ASCII);
//			String result5 = new String(bytes, StandardCharsets.UTF_16BE);
//			String result6 = new String(bytes, StandardCharsets.UTF_16LE);
//			System.out.println(result1 + result2 + result3 + result4 + result5 + result6);
			return response.body().string();
		}
	}

	/**
	 * Получить строку ответа с использованием заголовков запроса
	 * @param url
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public String getStringHeaders(String url, String... headers) throws IOException {
		Request.Builder builder = new Request.Builder().url(url);
		for (int i = 1; i < headers.length; i += 2) {
			builder.header(headers[i - 1], headers[i]);
		}
		Request request = builder.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.body() != null) {
				return response.body().string();
			}
			return null;
		}
	}

	/**
	 * Получить строку ответа POST запроса с заголовками и с параметрами
	 * @param url
	 * @param body
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public String postStringHeaders(String url, String body, String bodyType, String... headers) throws IOException {
		RequestBody reqBody = RequestBody.create(MediaType.parse(bodyType), body);
		Request.Builder builder = new Request.Builder().url(url);
		for (int i = 1; i < headers.length; i += 2) {
			builder.header(headers[i - 1], headers[i]);
		}
		Request request = builder.post(reqBody).build();
		try (Response response = client.newCall(request).execute()) {
			if (response.body() != null)
				return response.body().string();
			return null;
		}
	}


	private static class UnzippingInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Response response = chain.proceed(chain.request());
			return unzip(response);
		}


		// copied from okhttp3.internal.http.HttpEngine (because is private)
		private Response unzip(final Response response) throws IOException {
			if (response.body() == null)
			{
				return response;
			}

			//check if we have gzip response
			String contentEncoding = response.headers().get("Content-Encoding");
			if (StringUtils.isBlank(contentEncoding))
				contentEncoding = response.headers().get("Content-Type");

			//this is used to decompress gzipped responses
			if (contentEncoding != null && StringUtils.containsIgnoreCase(contentEncoding,"gzip")) {
				Long contentLength = response.body().contentLength();
				GzipSource responseBody = new GzipSource(response.body().source());
				Headers strippedHeaders = response.headers().newBuilder().build();
				return response.newBuilder().headers(strippedHeaders)
						.body(new RealResponseBody(response.body().contentType().toString(), contentLength, Okio.buffer(responseBody)))
						.build();
			}
			else if (contentEncoding != null && StringUtils.equalsIgnoreCase(contentEncoding, "br")) {
				Long contentLength = response.body().contentLength();
				//GzipSource responseBody = new GzipSource(response.body().source());
				Headers strippedHeaders = response.headers().newBuilder().build();
				BufferedSource src = Okio.buffer(Okio.source(new BrotliInputStream(response.body().source().inputStream())));
				return response.newBuilder().headers(strippedHeaders)
						.body(new RealResponseBody(response.body().contentType().toString(), contentLength, src))
						.build();
			}
			else
			{
				return response;
			}
		}
	}


}

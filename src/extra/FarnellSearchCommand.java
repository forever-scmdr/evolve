package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.net.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

public class FarnellSearchCommand extends Command {
	private static final LinkedHashMap<String, String> URL_MANDATORY_CONSTANTS = new LinkedHashMap<String, String>(){{
		put("callInfo.responseDataFormat", "XML");
		put("callInfo.omitXmlSchema", "true");
		put("storeInfo.id", "ru.farnell.com");
		put("callInfo.apiKey", "83a298tssh6jxt4rct6fuskk ");
		put("resultsSettings.responseGroup", "large");
	}};

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public static void main(String[] args) {
		String encodedString = "The quick brown fox jumps over the lazy dog";
		String secretKey = "key";
		String alg = "HmacSHA1";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), alg);
			Mac mac = Mac.getInstance(alg);
			mac.init(keySpec);
			String hash = Base64.encodeBase64String(mac.doFinal(encodedString.getBytes()));
			System.out.println(hash);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}

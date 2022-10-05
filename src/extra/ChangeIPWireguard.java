package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChangeIPWireguard extends Command {

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private List<String> out;

		public StreamGobbler(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			out = br.lines().collect(Collectors.toList());
		}
	}

	@Override
	public ResultPE execute() throws Exception {
		Process process = Runtime.getRuntime().exec("systemctl restartwg-quick@wg0.service");
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream());
		Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);

		int exitCode = process.waitFor();
		assert exitCode == 0;

		future.get(); // waits for streamGobbler to finish
		return getResult("success").setValue(StringUtils.join(streamGobbler.out, "\r\n"));
	}

	public static void main(String[] args) {
		System.out.println("https://www.digikey.com/en/products/detail/jst-sales-america-inc/SM24B-ZPDSS-TF-LF-SN/2472591".hashCode());
	}
}

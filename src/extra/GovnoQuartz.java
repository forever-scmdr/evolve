package extra;

import ecommander.controllers.PageController;
import ecommander.controllers.SessionContext;
import ecommander.fwk.ServerLogger;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageModelRegistry;
import ecommander.pages.ResultPE;

import java.io.ByteArrayOutputStream;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MILLIS;

public class GovnoQuartz extends Command {

	private static volatile boolean flag;
	private static LocalTime time = LocalTime.of(18, 30);
	private static volatile Thread startIntegration;

	@Override
	public ResultPE execute() throws Exception {
		if (startIntegration == null) {
			startIntegration = new Thread(() -> {
				while (!flag) {
					LocalTime now = LocalTime.now();
					if (now.compareTo(time) >= 0) {
						try {
							ExecutablePagePE page = PageModelRegistry.getRegistry().getExecutablePage("integrate_nasklade_auto", getUrlBase(), SessionContext.createSessionContext(null));
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							PageController.newSimple().executePage(page, bos);
							flag = true;
						} catch (Exception e) {
							e.printStackTrace();
							flag = true;
						}
					}else{
						long diff = MILLIS.between(now, time);
						try {
							flag = false;
							startIntegration.sleep(diff);
						} catch (InterruptedException e) {
							e.printStackTrace();
							ServerLogger.error(e);
						}
					}
				}
				if(flag){
					LocalTime now = LocalTime.now();
					long diff = MILLIS.between(LocalTime.of(0, 0), now);
					try {
						flag = false;
						startIntegration.sleep(diff);

					} catch (InterruptedException e) {
						e.printStackTrace();
						ServerLogger.error(e);
					}
				}
			});
			startIntegration.setDaemon(true);
			//boolean async = getVarSingleValueDefault("mode", "async").equalsIgnoreCase("async");
			startIntegration.start();
		}
		return null;
	}
}

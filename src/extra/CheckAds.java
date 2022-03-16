package extra;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.utils.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;


public class CheckAds extends Command {

	private static final String TEST_URL = "https://googleads.g.doubleclick.net/pagead/ads?client=ca-pub-3159421778745159&output=html&h=280&slotname=9423289872&adk=462199312&adf=3703621180&pi=t.ma~as.9423289872&w=700&fwrn=4&fwrnh=100&lmt=1628763017&rafmt=1&psa=1&format=700x280&url=https%3A%2F%2Ftempting.pro%2F&flash=0&fwr=0&fwrattr=true&rpe=1&resp_fmts=3&wgl=1&dt=1628763016000&bpp=11&bdt=3570&idt=519&shv=r20210809&mjsv=m202108100101&ptt=9&saldr=aa&abxe=1&cookie=ID%3D109c975f47f4cce4-2277e9b66cb900f0%3AT%3D1608464845%3ART%3D1608464845%3AR%3AS%3DALNI_MYshFdELg2dnpqREtsgV46Jz5krLA&correlator=6199541877183&frm=20&pv=2&ga_vid=1613146002.1596555169&ga_sid=1628763017&ga_hid=1863270543&ga_fc=0&u_tz=180&u_his=2&u_java=0&u_h=1024&u_w=1280&u_ah=984&u_aw=1280&u_cd=24&u_nplug=0&u_nmime=0&adx=281&ady=0&biw=1263&bih=267&scr_x=0&scr_y=76&eid=20211866%2C31062249&oid=3&pvsid=3340915515289711&pem=373&eae=0&fc=896&brdim=-8%2C-8%2C-8%2C-8%2C1280%2C0%2C1296%2C1000%2C1280%2C267&vis=1&rsz=%7C%7CoeE%7C&abl=CS&pfx=0&fu=128&bc=31&ifi=1&uci=a!1&fsb=1&xpc=NUELIqDL3F&p=https%3A//tempting.pro&dtd=1143";

	@Override
	public ResultPE execute() throws Exception {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("root");
		String url  = getVarSingleValue("url").toString();
		try {
			Document ads = Jsoup.parse(new URL(url), 1000);
			Element body = ads.body();
			boolean ok = StringUtils.isNotBlank(body.html());
			doc.addElement("result", ok);
		}catch (Exception e){
			ServerLogger.error(e);
			String stackTrace = ExceptionUtils.getStackTrace(e);
			doc.addElement("exception", stackTrace);
		}
		doc.endElement();
		ResultPE res = getResult("result");
		res.setValue(doc.toString());
		return res;
	}
}

package extra.belchip;

import com.lowagie.text.pdf.BaseFont;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;
import org.apache.commons.io.Charsets;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class OrderPDFCreator extends Command {
	private static Object MUTEX = new Object();
	
	
	@Override
	public ResultPE execute() throws Exception {
		synchronized (MUTEX) {
			File output = new File(AppContext.getFilesDirPath(false) + String.valueOf(new Date().getTime()) + ".pdf");
			//Getting executable page ByteArrayOutputStream
			ExecutablePagePE orderPage = getExecutablePage("download_cart");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(orderPage, bos);			

//			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
//		            .newInstance();
//		    documentBuilderFactory.setValidating(false);
//		    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
//		    builder.setEntityResolver(FSEntityResolver.instance());
		    String content = new String(bos.toByteArray(), StandardCharsets.UTF_8);
			bos.close();
						
			ITextRenderer renderer = new ITextRenderer();
			String fontPath = AppContext.getContextPath()+"ARIALUNI.TTF";
			renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			renderer.setDocumentFromString(content);
			//renderer.setDocument(document, null);
			renderer.layout();
			FileOutputStream fos = new FileOutputStream(output);
			renderer.createPDF(fos);
			fos.close();
			
			ResultPE res;
			res = getResult("success");
			res.setValue(AppContext.getFilesUrlPath(false) + output.getName());
			return res;
		}
	}


}

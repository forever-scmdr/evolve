package ecommander.controllers;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.fwk.Timer;
import ecommander.fwk.XmlDocumentBuilder;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.commons.io.FileUtils;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * Преобразует XML документ в html с помощью XSL файла
 * @author EEEE
 *
 */
public class XmlXslOutputController {

	private XmlXslOutputController() {};
	
	private static class Errors implements ErrorListener {

		private String errors = "";
		
		public void error(TransformerException exception) throws TransformerException {
			errors += "\nERROR " + exception.getMessageAndLocation();
		}

		public void fatalError(TransformerException exception) throws TransformerException {
			errors += "\nFATAL ERROR " + exception.getMessageAndLocation();
		}

		public void warning(TransformerException exception) throws TransformerException {
			errors += "\nWARNING " + exception.getMessageAndLocation();
		}
	}

	private static TransformerFactory factory;
	private static HashMap<String, Transformer> transformers;
	private static long lastModified = 0;
	private static long lastChecked = 0;

	private static TransformerFactory getTransformerFac() {
		if (factory == null)
			factory = TransformerFactoryImpl.newInstance();
		return factory;
	}

	private static Transformer getTransformer(File xslFile, TransformerFactory fac) throws TransformerConfigurationException {
		if (transformers == null)
			transformers = new HashMap<>();
		Transformer trans = transformers.get(xslFile.getAbsolutePath());
		if (trans == null || checkIfModified()) {
			trans = fac.newTransformer(new StreamSource(xslFile));
			transformers.put(xslFile.getAbsolutePath(), trans);
		}
		return trans;
	}


	private static boolean checkIfModified() {
		if (Math.abs(System.currentTimeMillis() - lastChecked) > 5000) {
			lastChecked = System.currentTimeMillis();
			File xslDir = new File(AppContext.getStylesDirPath());
			Collection<File> files = FileUtils.listFiles(xslDir, new String[]{"xsl"}, true);
			boolean modified = false;
			for (File file : files) {
				if (file.lastModified() > lastModified) {
					lastModified = file.lastModified();
					modified = true;
				}
			}
			if (modified)
				transformers.clear();
			return modified;
		}
		return false;
	}

	/**
	 * Вывести преобразованный документ, если преобразование требуется, либо просто XML документ
	 * @param ostream - куда выводится результат
	 * @param xml
	 * @param xslFileName
	 * @throws TransformerException
	 * @throws IOException
	 * @throws EcommanderException
	 */
	public static void outputXmlTransformed(OutputStream ostream, XmlDocumentBuilder xml, String xslFileName)
			throws TransformerException, IOException, EcommanderException {
		// Если режим отладки, то вывести содержимое XML документа
		File xslFile = new File(xslFileName);
		if (!xslFile.exists()/* || ServerLogger.isDebugMode()*/) {
			outputXml(ostream, xml);
		} else {
			Errors errors = new Errors();
			try {
				Timer.getTimer().start(Timer.XSL_TRANSFORM);
				TransformerFactory factory = getTransformerFac();
				Transformer transformer;
				factory.setErrorListener(errors);
				transformer = getTransformer(xslFile, factory);
				Reader reader = new StringReader(xml.toString());
				transformer.transform(new StreamSource(reader), new StreamResult(ostream));
			} catch (TransformerConfigurationException e) {
				factory = null;
				transformers = null;
				throw new EcommanderException(ErrorCodes.NO_SPECIAL_ERROR, errors.errors);
			} catch (Exception ex) {
				factory = null;
				transformers = null;
				throw ex;
			}
			finally {
				Timer.getTimer().stop(Timer.XSL_TRANSFORM);
			}
		}
	}
	/**
	 * Выводит XML документ в поток вывода
	 * @param ostream
	 * @param xml
	 * @throws IOException
	 */
	public static void outputXml(OutputStream ostream, XmlDocumentBuilder xml) throws IOException {
		// Настройка формата вывода документа
		ostream.write(xml.toString().getBytes("UTF-8"));
	}
}

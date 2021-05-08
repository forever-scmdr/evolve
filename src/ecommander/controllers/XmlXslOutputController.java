package ecommander.controllers;

import java.io.*;
import java.util.HashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ecommander.fwk.*;
import net.sf.saxon.TransformerFactoryImpl;

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
	private static HashMap<String, Pair<Long, Transformer>> transformers;

	private static TransformerFactory getTransformerFac() {
		if (factory == null)
			factory = TransformerFactoryImpl.newInstance();
		return factory;
	}

	private static Transformer getTransformer(File xslFile, TransformerFactory fac) throws TransformerConfigurationException {
		if (transformers == null)
			transformers = new HashMap<>();
		Pair<Long, Transformer> trans = transformers.get(xslFile.getAbsolutePath());
		if (trans == null || trans.getLeft() != xslFile.lastModified()) {
			Transformer transformer = fac.newTransformer(new StreamSource(xslFile));
			trans = new Pair<>(xslFile.lastModified(), transformer);
			transformers.put(xslFile.getAbsolutePath(), trans);
		}
		return trans.getRight();
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

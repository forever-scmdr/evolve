package ecommander.controllers;

import java.io.*;

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
			Timer.getTimer().start(Timer.XSL_TRANSFORM);
			TransformerFactory factory = TransformerFactoryImpl.newInstance();
			Errors errors = new Errors();
			Transformer transformer;
			try {
				factory.setErrorListener(errors);
				transformer = factory.newTransformer(new StreamSource(xslFile));
				Reader reader = new StringReader(xml.toString());
				transformer.transform(new StreamSource(reader), new StreamResult(ostream));
			} catch (TransformerConfigurationException e) {
				throw new EcommanderException(ErrorCodes.NO_SPECIAL_ERROR, errors.errors);
			} finally {
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

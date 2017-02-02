package ecommander.application.extra;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;

import nl.captcha.Captcha;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;

/**
 * http://simplecaptcha.git.sourceforge.net/git/gitweb.cgi?p=simplecaptcha/
 * simplecaptcha;a=blob_plain;f=Java/src/nl/captcha/servlet/SimpleCaptchaServlet.java;hb=HEAD
 * @author EEEE
 *
 */
public class SimpleCaptchaServlet extends HttpServlet {
	
	private static class NumericTextProducer implements TextProducer {
		//private static final String _RUSSIAN = "1234567890абвгдеёжзиыйклмнопрстуфхцчшщэюя";
		public String getText() {
			return RandomStringUtils.random(5, false, true);
		}
	}
	public static final String NAME = "capt";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4013652365986208139L;

    private static int _width = 110;
    private static int _height = 50;
    
    private static final List<Color> COLORS = new ArrayList<Color>(2);
    private static final List<Font> FONTS = new ArrayList<Font>(3);
    
    static {
        COLORS.add(/*Color.BLACK*/new Color(54, 54, 54));//#363636
        COLORS.add(/*Color.BLUE*/new Color(255, 210, 0));//#ffd200

        FONTS.add(new Font("Geneva", Font.ITALIC, 40));
        FONTS.add(new Font("Geneva", Font.ITALIC, 33));
        FONTS.add(new Font("Courier", Font.BOLD, 40));
        FONTS.add(new Font("Courier", Font.BOLD, 33));
        FONTS.add(new Font("Arial", Font.BOLD, 40));
        FONTS.add(new Font("Arial", Font.BOLD, 33));
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    	if (getInitParameter("captcha-height") != null) {
    		_height = Integer.valueOf(getInitParameter("captcha-height"));
    	}
    	
    	if (getInitParameter("captcha-width") != null) {
    		_width = Integer.valueOf(getInitParameter("captcha-width"));
    	}
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        ColoredEdgesWordRenderer wordRenderer = new ColoredEdgesWordRenderer(COLORS, FONTS);
    	DefaultWordRenderer wordRenderer = new DefaultWordRenderer(COLORS, FONTS);
        Captcha captcha = new Captcha.Builder(_width, _height)
        		.addText(new NumericTextProducer(), wordRenderer)
				.gimp()
        		.gimp(new BlockGimpyRenderer(2))
				.build();
        req.getSession().setAttribute(NAME, captcha);
        CaptchaServletUtil.writeImage(resp, captcha.getImage());
    }
}
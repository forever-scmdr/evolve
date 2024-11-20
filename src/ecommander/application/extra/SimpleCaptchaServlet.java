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

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
/**
 * http://simplecaptcha.git.sourceforge.net/git/gitweb.cgi?p=simplecaptcha/
 * simplecaptcha;a=blob_plain;f=Java/src/nl/captcha/servlet/SimpleCaptchaServlet.java;hb=HEAD
 * @author EEEE
 *
 */
public class SimpleCaptchaServlet extends HttpServlet {
	
	public static final String NAME = "capt";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4013652365986208139L;

    private static int _width = 125;
    private static int _height = 40;
    
    private static final List<Color> COLORS = new ArrayList<Color>(2);
    private static final List<Font> FONTS = new ArrayList<Font>(3);
    
    static {
        COLORS.add(Color.BLACK);
        COLORS.add(Color.BLUE);
//        COLORS.add(Color.GRAY);
        COLORS.add(Color.GREEN);
        COLORS.add(Color.MAGENTA);
        COLORS.add(Color.ORANGE);
        

        FONTS.add(new Font("Geneva", Font.ITALIC, 35));
        FONTS.add(new Font("Geneva", Font.ITALIC, 25));
        FONTS.add(new Font("Courier", Font.PLAIN, 38));
        FONTS.add(new Font("Courier", Font.TRUETYPE_FONT, 30));
        FONTS.add(new Font("Arial", Font.ITALIC, 39));
        FONTS.add(new Font("Arial", Font.PLAIN, 38));
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
        		.addText(new DefaultTextProducer(), wordRenderer)
				.addBackground(new GradiatedBackgroundProducer())
        		.gimp()
//				.gimp(new BlockGimpyRenderer(6))
//				.gimp(new RippleGimpyRenderer())
//        		.gimp(new StretchGimpyRenderer())
				.addNoise()
//        		.gimp(new DropShadowGimpyRenderer())
        		.build();
        req.getSession().setAttribute(NAME, captcha);
        CaptchaServletUtil.writeImage(resp, captcha.getImage());
    }
}

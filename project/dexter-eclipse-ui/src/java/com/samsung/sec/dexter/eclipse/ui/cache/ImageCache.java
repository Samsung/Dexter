package com.samsung.sec.dexter.eclipse.ui.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageCache {
	  private static HashMap<String, Image> _ImageMap;

	    // what path to get to the "icons" directory without actually including it
	    private static final String           ICON_ROOT_PATH = "/";

	    static {
	        _ImageMap = new HashMap<String, Image>();
	    }

	    /**
	     * Returns an image that is also cached if it has to be created and does not already exist in the cache.
	     * 
	     * @param fileName Filename of image to fetch
	     * @return Image null if it could not be found
	     */
	    public static Image getImage(String fileName) {
	        fileName = ICON_ROOT_PATH + fileName;
	        Image image = _ImageMap.get(fileName);
	        if (image == null) {
	            image = createImage(fileName);
	            _ImageMap.put(fileName, image);
	        }
	        return image;
	    }

	    // creates the image, and tries really hard to do so
	    private static Image createImage(String fileName) {
	        ClassLoader classLoader = ImageCache.class.getClassLoader();
	        InputStream is = classLoader.getResourceAsStream(fileName);
	        if (is == null) {
	            // the old way didn't have leading slash, so if we can't find the image stream,
	            // let's see if the old way works.
	            is = classLoader.getResourceAsStream(fileName.substring(1));

	            if (is == null) {
	                is = classLoader.getResourceAsStream(fileName);
	                if (is == null) {
	                    is = classLoader.getResourceAsStream(fileName.substring(1));
	                    if (is == null) { return null; }
	                }
	            }
	        }

	        Image img = new Image(Display.getDefault(), is);
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return img;
	    }

	    /**
	     * Disposes ALL images that have been cached.
	     */
	    public static void dispose() {
	        Iterator<Image> e = _ImageMap.values().iterator();
	        while (e.hasNext())
	            e.next().dispose();

	    }
}

package com.samsung.sec.dexter.eclipse.ui.notifier;

import org.eclipse.swt.graphics.Image;
import com.samsung.sec.dexter.eclipse.ui.cache.ImageCache;

public enum NotificationType {

	  ERROR(ImageCache.getImage("error.png")),
	    DELETE(ImageCache.getImage("delete.png")),
	    WARN(ImageCache.getImage("warn.png")),
	    SUCCESS(ImageCache.getImage("ok.png")),
	    INFO(ImageCache.getImage("info.png")),
	    LIBRARY(ImageCache.getImage("library.png")),
	    HINT(ImageCache.getImage("hint.png")),
	    PRINTED(ImageCache.getImage("printer.png")),
	    CONNECTION_TERMINATED(ImageCache.getImage("terminated.png")),
	    CONNECTION_FAILED(ImageCache.getImage("connecting.png")),
	    CONNECTED(ImageCache.getImage("connected.png")),
	    DISCONNECTED(ImageCache.getImage("disconnected.png")),
	    TRANSACTION_OK(ImageCache.getImage("ok.png")),
	    TRANSACTION_FAIL(ImageCache.getImage("error.png"));

	    private Image _image;

	    private NotificationType(Image img) {
	        _image = img;
	    }

	    public Image getImage() {
	        return _image;
	    }
}

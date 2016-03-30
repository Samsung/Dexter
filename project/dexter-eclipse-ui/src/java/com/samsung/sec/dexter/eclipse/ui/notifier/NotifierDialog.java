package com.samsung.sec.dexter.eclipse.ui.notifier;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.samsung.sec.dexter.eclipse.ui.cache.ColorCache;
import com.samsung.sec.dexter.eclipse.ui.cache.FontCache;

public class NotifierDialog {

    // how long the the tray popup is displayed after fading in (in milliseconds)
    private static final int   DISPLAY_TIME  = 4500;
    // how long each tick is when fading in (in ms)
    private static final int   FADE_TIMER    = 50;
    // how long each tick is when fading out (in ms)
    private static final int   FADE_IN_STEP  = 30;
    // how many tick steps we use when fading out 
    private static final int   FADE_OUT_STEP = 8;

    // how high the alpha value is when we have finished fading in 
    private static final int   FINAL_ALPHA   = 225;

    // title foreground color
    private static Color       _titleFgColor = ColorCache.getColor(40, 73, 97);
    // text foreground color
    private static Color       _fgColor      = _titleFgColor;

    // shell gradient background color - top
    private static Color       _bgFgGradient = ColorCache.getColor(226, 239, 249);
    // shell gradient background color - bottom    
    private static Color       _bgBgGradient = ColorCache.getColor(177, 211, 243);
    // shell border color
    private static Color       _borderColor  = ColorCache.getColor(40, 73, 97);

    // contains list of all active popup shells
    private static List<Shell> _activeShells = new ArrayList<Shell>();

    // image used when drawing
    private static Image       _oldImage;

    private static Shell       _shell;

    /**
     * Creates and shows a notification dialog with a specific title, message and a
     * 
     * @param title
     * @param message
     * @param type
     */
    public static void notify(String title, String message, NotificationType type) {
        _shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.NO_TRIM);
        _shell.setLayout(new FillLayout());
        _shell.setForeground(_fgColor);
        _shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
        _shell.addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event event) {
                _activeShells.remove(_shell);
            }
        });

        final Composite inner = new Composite(_shell, SWT.NONE);

        GridLayout gl = new GridLayout(2, false);
        gl.marginLeft = 5;
        gl.marginTop = 0;
        gl.marginRight = 5;
        gl.marginBottom = 5;

        inner.setLayout(gl);
        _shell.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event e) {
                try {
                    // get the size of the drawing area
                    Rectangle rect = _shell.getClientArea();
                    // create a new image with that size
                    Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
                    // create a GC object we can use to draw with
                    GC gc = new GC(newImage);

                    // fill background
                    gc.setForeground(_bgFgGradient);
                    gc.setBackground(_bgBgGradient);
                    gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

                    // draw shell edge
                    gc.setLineWidth(2);
                    gc.setForeground(_borderColor);
                    gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
                    // remember to dipose the GC object!
                    gc.dispose();

                    // now set the background image on the shell
                    _shell.setBackgroundImage(newImage);

                    // remember/dispose old used iamge
                    if (_oldImage != null) {
                        _oldImage.dispose();
                    }
                    _oldImage = newImage;
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        GC gc = new GC(_shell);

        String lines[] = message.split("\n");
        Point longest = null;
        int typicalHeight = gc.stringExtent("X").y;

        for (String line : lines) {
            Point extent = gc.stringExtent(line);
            if (longest == null) {
                longest = extent;
                continue;
            }

            if (extent.x > longest.x) {
                longest = extent;
            }
        }
        gc.dispose();

        int minHeight = typicalHeight * lines.length;

        CLabel imgLabel = new CLabel(inner, SWT.NONE);
        imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
        imgLabel.setImage(type.getImage());

        CLabel titleLabel = new CLabel(inner, SWT.NONE);
        titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
        titleLabel.setText(title);
        titleLabel.setForeground(_titleFgColor);
        Font f = titleLabel.getFont();
        FontData fd = f.getFontData()[0];
        fd.setStyle(SWT.BOLD);
        fd.height = 11;
        titleLabel.setFont(FontCache.getFont(fd));

        Label text = new Label(inner, SWT.WRAP);
        Font tf = text.getFont();
        FontData tfd = tf.getFontData()[0];
        tfd.setStyle(SWT.BOLD);
        tfd.height = 8;
        text.setFont(FontCache.getFont(tfd));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        text.setLayoutData(gd);
        text.setForeground(_fgColor);
        text.setText(message);

        minHeight = 100;

        _shell.setSize(350, minHeight);

        if (Display.getDefault().getActiveShell() == null || Display.getDefault().getActiveShell().getMonitor() == null) { return; }

        Rectangle clientArea = Display.getDefault().getActiveShell().getMonitor().getClientArea();

        int startX = clientArea.x + clientArea.width - 352;
        int startY = clientArea.y + clientArea.height - 102;

        // move other shells up
        if (!_activeShells.isEmpty()) {
            List<Shell> modifiable = new ArrayList<Shell>(_activeShells);
            Collections.reverse(modifiable);
            for (Shell shell : modifiable) {
                Point curLoc = shell.getLocation();
                shell.setLocation(curLoc.x, curLoc.y - 100);
                if (curLoc.y - 100 < 0) {
                    _activeShells.remove(shell);
                    shell.dispose();
                }
            }
        }

        _shell.setLocation(startX, startY);
        _shell.setAlpha(0);
        _shell.setVisible(true);

        _activeShells.add(_shell);

        fadeIn(_shell);
    }

    private static void fadeIn(final Shell _shell) {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    if (_shell == null || _shell.isDisposed()) { return; }

                    int cur = _shell.getAlpha();
                    cur += FADE_IN_STEP;

                    if (cur > FINAL_ALPHA) {
                        _shell.setAlpha(FINAL_ALPHA);
                        startTimer(_shell);
                        return;
                    }

                    _shell.setAlpha(cur);
                    Display.getDefault().timerExec(FADE_TIMER, this);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        };
        Display.getDefault().timerExec(FADE_TIMER, run);
    }

    private static void startTimer(final Shell _shell) {
        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    if (_shell == null || _shell.isDisposed()) { return; }

                    fadeOut(_shell);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        };
        Display.getDefault().timerExec(DISPLAY_TIME, run);

    }

    private static void fadeOut(final Shell _shell) {
        final Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    if (_shell == null || _shell.isDisposed()) { return; }

                    int cur = _shell.getAlpha();
                    cur -= FADE_OUT_STEP;

                    if (cur <= 0) {
                        _shell.setAlpha(0);
                        if (_oldImage != null) {
                            _oldImage.dispose();
                        }
                        _shell.dispose();
                        _activeShells.remove(_shell);
                        return;
                    }

                    _shell.setAlpha(cur);

                    Display.getDefault().timerExec(FADE_TIMER, this);

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        };
        Display.getDefault().timerExec(FADE_TIMER, run);

    }

}


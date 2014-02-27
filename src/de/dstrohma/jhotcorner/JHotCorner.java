package de.dstrohma.jhotcorner;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Endogen on 14.02.14.
 */
// TODO: Add possibility for scaling corner image
// TODO: Support multi monitor environments
public class JHotCorner {
    private static final JHotCorner instance = new JHotCorner();
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private HashMap<Corner, HashSet<JHotCornerInterface>> cornerMap;
    private Point currentCornerPoint = null;
    private Thread cornerThread = null;
    private long cornerThreadId = 0;

    private int sleepMillis = 20;
    private double pixelRadius = 6;
    private boolean showImage = false;
    private URL imageUrl = getClass().getClassLoader().getResource("default.png");

    private JHotCorner() {
        cornerMap = new HashMap<Corner, HashSet<JHotCornerInterface>>();
        for (Corner corner : Corner.values()) {
            cornerMap.put(corner, new HashSet<JHotCornerInterface>());
        }
    }

    public static JHotCorner getInstance() {
        return instance;
    }

    private void initObservationThread() {
        cornerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean switchBool = true;
                Point lastPosition = new Point();

                while (Thread.currentThread().getId() == cornerThreadId) {
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Point currentPosition = MouseInfo.getPointerInfo().getLocation();
                    if (!currentPosition.equals(lastPosition)) {
                        lastPosition = currentPosition;

                        boolean insideTopLeft = isInCorner(Corner.TOP_LEFT, currentPosition);
                        if (insideTopLeft && switchBool) {
                            for (JHotCornerInterface owner : cornerMap.get(Corner.TOP_LEFT)) {
                                if (owner != null) {
                                    owner.onTopLeftCorner();
                                    if (showImage) {
                                        showImage(Corner.TOP_LEFT);
                                    }
                                }
                                switchBool = false;
                            }
                        }

                        boolean insideTopRight = isInCorner(Corner.TOP_RIGHT, currentPosition);
                        if (insideTopRight && switchBool) {
                            for (JHotCornerInterface owner : cornerMap.get(Corner.TOP_RIGHT)) {
                                if (owner != null) {
                                    owner.onTopRightCorner();
                                    if (showImage) {
                                        showImage(Corner.TOP_RIGHT);
                                    }
                                }
                                switchBool = false;
                            }
                        }

                        boolean insideBottomLeft = isInCorner(Corner.BOTTOM_LEFT, currentPosition);
                        if (insideBottomLeft && switchBool) {
                            for (JHotCornerInterface owner : cornerMap.get(Corner.BOTTOM_LEFT)) {
                                if (owner != null) {
                                    owner.onBottomLeftCorner();
                                    if (showImage) {
                                        showImage(Corner.BOTTOM_LEFT);
                                    }
                                }
                                switchBool = false;
                            }
                        }

                        boolean insideBottomRight = isInCorner(Corner.BOTTOM_RIGHT, currentPosition);
                        if (insideBottomRight && switchBool) {
                            for (JHotCornerInterface owner : cornerMap.get(Corner.BOTTOM_RIGHT)) {
                                if (owner != null) {
                                    owner.onBottomRightCorner();
                                    if (showImage) {
                                        showImage(Corner.BOTTOM_RIGHT);
                                    }
                                }
                                switchBool = false;
                            }
                        }

                        if (!insideTopLeft && !insideTopRight && !insideBottomLeft && !insideBottomRight) {
                            switchBool = true;
                        }
                    }
                }
            }
        });
    }

    private boolean isInCorner(Corner corner, Point currentPosition) {
        switch (corner) {
            case TOP_LEFT:
                currentCornerPoint = new Point(0, 0);
                break;
            case TOP_RIGHT:
                currentCornerPoint = new Point((int)screenSize.getWidth(), 0);
                break;
            case BOTTOM_LEFT:
                currentCornerPoint = new Point(0, (int)screenSize.getHeight());
                break;
            case BOTTOM_RIGHT:
                currentCornerPoint = new Point((int)screenSize.getWidth(), (int)screenSize.getHeight());
        }

        double x = currentCornerPoint.getX() - (pixelRadius /2);
        double y = currentCornerPoint.getY() - (pixelRadius /2);
        Ellipse2D circle = new Ellipse2D.Double(x, y, pixelRadius, pixelRadius);

        if (circle.contains(currentPosition.getX(), currentPosition.getY())) {
            return true;
        }

        return false;
    }

    public void registerHotCorner(final Corner corner, JHotCornerInterface owner) {
        cornerMap.get(corner).add(owner);
        if (cornerThread == null) {
            initObservationThread();
            cornerThreadId = cornerThread.getId();
            cornerThread.start();
        }
    }

    public void unregisterHotCorner(Corner corner, JHotCornerInterface owner) {
        cornerMap.get(corner).remove(owner);

        boolean tl = cornerMap.get(Corner.TOP_LEFT).isEmpty();
        boolean tr = cornerMap.get(Corner.TOP_RIGHT).isEmpty();
        boolean bl = cornerMap.get(Corner.BOTTOM_LEFT).isEmpty();
        boolean br = cornerMap.get(Corner.BOTTOM_RIGHT).isEmpty();
        if (tl && tr && bl && br) {
            cornerThreadId = 0;
            cornerThread = null;
        }
    }

    public void setCornerRadius(int radius) {
        this.pixelRadius = radius;
    }

    public double getCornerRadius() {
        return pixelRadius;
    }

    public void setShowCornerImage(boolean showGraphic) {
        this.showImage = showGraphic;
    }

    public boolean getShowCornerImage() {
        return showImage;
    }

    public void setCornerImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public URL getCornerImageUrl() {
        return imageUrl;
    }

    public int getSleepMillis() {
        return sleepMillis;
    }

    public void setSleepMillis(int sleepMillis) {
        if (sleepMillis >= 0) {
            this.sleepMillis = sleepMillis;
        }
    }

    private void showImage(Corner corner) {
        Point currentImagePoint = null;
        int cornerX = (int)currentCornerPoint.getX();
        int cornerY = (int)currentCornerPoint.getY();
        CornerImage cornerImage = new CornerImage(imageUrl, corner);

        switch (corner) {
            case TOP_LEFT:
                currentImagePoint = currentCornerPoint;
                break;
            case TOP_RIGHT:
                currentImagePoint = new Point(cornerX - cornerImage.getIconWidth(), cornerY);
                break;
            case BOTTOM_LEFT:
                currentImagePoint = new Point(cornerX, cornerY - cornerImage.getIconHeight());
                break;
            case BOTTOM_RIGHT:
                currentImagePoint = new Point(cornerX - cornerImage.getIconWidth(), cornerY - cornerImage.getIconHeight());
                break;
        }

        final JWindow window = new JWindow();
        window.setAlwaysOnTop(true);
        window.setBackground(new Color(0f, 0f, 0f, 0.0f));
        window.setOpacity(0);
        window.add(new JLabel(cornerImage));
        window.pack();
        window.setLocation(currentImagePoint);
        window.setVisible(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (window.getOpacity() <= 0.90F) {
                        Thread.sleep(10);
                        window.setOpacity(window.getOpacity() + 0.05F);
                    }
                    Thread.sleep(600);
                    while (window.getOpacity() >= 0.04F) {
                        Thread.sleep(20);
                        window.setOpacity(window.getOpacity() - 0.02F);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                window.dispose();
            }
        }).start();
    }

    public enum Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

    public class CornerImage extends ImageIcon {
        private Corner corner;

        public CornerImage(URL imageUrl, Corner corner) {
            super(imageUrl);
            this.corner = corner;
        }

        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g.create();

            switch (corner) {
                case TOP_LEFT:
                    break;
                case TOP_RIGHT:
                    g2.translate(x + getIconWidth(), y);
                    g2.rotate(Math.toRadians(90));
                    break;
                case BOTTOM_LEFT:
                    g2.translate(x, y + getIconHeight());
                    g2.rotate(Math.toRadians(-90));
                    break;
                case BOTTOM_RIGHT:
                    g2.translate(x + getIconWidth(), y + getIconHeight());
                    g2.rotate(Math.toRadians(180));
                    break;
            }

            g2.setBackground(new Color(0,0,0,0));
            g2.clearRect(0, 0, getIconWidth(), getIconHeight());
            super.paintIcon(c, g2, x, y);
        }
    }
}
package examples;

import de.dstrohma.jhotcorner.JHotCorner;
import de.dstrohma.jhotcorner.JHotCornerInterface;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Endogen on 15.02.14.
 */
public class HotCornerDemo extends JFrame implements JHotCornerInterface {

    public static void main(String[] args) {
        new HotCornerDemo().setVisible(true);
    }

    public HotCornerDemo() {
        setPreferredSize(new Dimension(400, 500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        pack();
        setLocationRelativeTo(null);

        JHotCorner.getInstance().setCornerRadius(20);
        JHotCorner.getInstance().setShowCornerImage(true);
        JHotCorner.getInstance().setCornerImageUrl(getClass().getClassLoader().getResource("examples/big.png"));

        JHotCorner.getInstance().registerHotCorner(JHotCorner.Corner.TOP_LEFT, this);
        JHotCorner.getInstance().registerHotCorner(JHotCorner.Corner.TOP_RIGHT, this);
        JHotCorner.getInstance().registerHotCorner(JHotCorner.Corner.BOTTOM_LEFT, this);
        JHotCorner.getInstance().registerHotCorner(JHotCorner.Corner.BOTTOM_RIGHT, this);
    }

    @Override
    public void onTopLeftCorner() {
        setVisible(!isVisible());
        System.out.println("TOP_LEFT");
    }

    @Override
    public void onTopRightCorner() {
        System.out.println("TOP_RIGHT");
    }

    @Override
    public void onBottomLeftCorner() {
        System.out.println("BOTTOM_LEFT");
    }

    @Override
    public void onBottomRightCorner() {
        System.out.println("BOTTOM_RIGHT");
    }
}
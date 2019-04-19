package com.alex.Cockroach;

import com.alex.MovableObject.MovableObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.security.InvalidParameterException;

public  class Cockroach extends MovableObject {
    private JPanel currentPane;
    private Container parent;

    @Override
    protected void onMove() {
        if (parent != null) {
            int x = (int) ((parent.getWidth() * 18 * getX()) / 20);
            currentPane.setBounds(x, 0, parent.getWidth() / 20, parent.getHeight());
        }
    }

    public Cockroach(double xSpeed, Container parent) throws InvalidParameterException {
        super(xSpeed);
        currentPane = new JPanel(){
            {
                setDoubleBuffered(true);
            }
            @Override
            public void paintComponent(Graphics g){
                g.setColor(Color.BLACK);
                int w = getWidth();
                int h = getHeight();
                g.drawLine(0, 0, w, h);
                g.drawLine(w, 0, 0, h);
                g.drawLine(w / 4, 0, w / 4, h);
                g.drawLine((w * 3) / 4, 0, (w * 3) / 4, h);
                g.setColor(Color.yellow);
                ((Graphics2D)g).fill(new Ellipse2D.Double(0, h / 4.0, w, h / 2.0));
            }
        };
        onMove();

        this.parent = parent;
        parent.setLayout(null);
        parent.add(currentPane);
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                onMove();
            }
        });

        currentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && !e.isConsumed()){
                    Cockroach.this.accelerate();
                }
            }
        });

        currentPane.setVisible(true);
    }
}

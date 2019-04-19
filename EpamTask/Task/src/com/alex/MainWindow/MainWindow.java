package com.alex.MainWindow;

import com.alex.Cockroach.Cockroach;
import com.alex.RaceManager.RaceManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.awt.GridBagConstraints.BOTH;

public class MainWindow extends JFrame{

    private LeaderFrame leaderFrame = new LeaderFrame();
    private ReportFrame reportFrame = new ReportFrame();

    private class RoadPanel extends JPanel{
        private int index;

        RoadPanel(int index){
            this.index = index;
            setDoubleBuffered(true);
        }

        @Override
        public void paintComponent(Graphics g){
            g.setColor(Color.ORANGE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GREEN);
            g.setFont(new Font("SERIF", Font.BOLD, 18));
            g.drawString(String.valueOf(index + 1), 10, 30);
            g.setColor(Color.WHITE);
            int dx = getWidth() / 20;
            dx = dx / 4;
            int dy = getHeight() / 4;
            for (int i = 0; i < 4; i++){
                for (int j = 0; j < 4; j++){
                    if ((i + j) % 2 > 0){
                        g.setColor(Color.WHITE);
                    }else{
                        g.setColor(Color.BLACK);
                    }

                    g.fillRect(getWidth() - (4 - i) * dx, j * dy,
                            dx, dy);
                }
            }
        }
    }

    private class ReportFrame extends JDialog{
        JTextArea area;

        ReportFrame(){
            super(MainWindow.this);
            setBounds(0, 0, 200, 100);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setTitle("Races summary");
            setLayout(new GridLayout(1, 1));
            area = new JTextArea();
            add(new JScrollPane(area));
            area.setDoubleBuffered(true);
        }

        void updateAreaText(String value){
            area.setText(area.getText() + System.getProperty("line.separator") + value);
        }
    }

    private class LeaderFrame extends ReportFrame{
        LeaderFrame(){
            setTitle("Race reporter");
        }

        @Override
        public void setVisible(boolean values){
            super.setVisible(values);
            int w = MainWindow.this.getWidth();
            int h = MainWindow.this.getHeight();
            int x = MainWindow.this.getX();
            int y = MainWindow.this.getY();
            setBounds(x + w, y , w / 4, h / 4);
        }

        @Override
        void updateAreaText(String value){
            area.setText(value);
        }
    }

    private void createMenu(RaceManager manager){
        JMenu options = new  JMenu("Options");
        JMenuItem restartItem = new JMenuItem("New Race");
        JMenuItem stopItem = new JMenuItem("Stop");
        options.add(restartItem);
        options.add(stopItem);
        add(options);
        JMenuBar bar = new JMenuBar();
        bar.add(options);
        setJMenuBar(bar);
        setBounds(0, 0, 800, 600);

        restartItem.addActionListener(e -> {
            manager.startRace();
            leaderFrame.setVisible(true);
        });
        stopItem.addActionListener(e -> {
            manager.stopRace();
            leaderFrame.setVisible(false);
        });
    }

    public MainWindow(final int roads) {
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 1));

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panel);

        List<Cockroach> cockroaches = new ArrayList<>();
        for (int i = 0; i < roads; i++) {
            JTextField nameField = new JTextField("Cockroach" + (i + 1));
            JPanel road = new RoadPanel(i);
            road.setDoubleBuffered(true);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(2, 2, 2, 2);
            constraints.fill = BOTH;
            constraints.gridy = i;
            constraints.weighty = 5.0;


            constraints.gridx = 0;
            constraints.weightx = 1.0;
            panel.add(nameField, constraints);

            constraints.gridx = 1;
            constraints.weightx = 5.0;
            panel.add(road, constraints);

            cockroaches.add(new Cockroach(0.008, road){
                Supplier<String> nameSupplier;
                {
                    nameSupplier = nameField::getText;
                }
                @Override
                public void run(){
                    setName(nameSupplier.get());
                    super.run();
                }
            });
        }

        add(scrollPane);
        RaceManager manager = new RaceManager(cockroaches) {
            @Override
            protected void updateLeader(String name) {
                leaderFrame.updateAreaText("The leader is " + name);
            }

            @Override
            protected void writeSummary(String log) {
                reportFrame.updateAreaText(log);
            }

            @Override
            protected void writeInterrupted(String log) {
                reportFrame.updateAreaText(log);
            }
        };

        createMenu(manager);
        reportFrame.setVisible(true);
    }
}

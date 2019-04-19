package com.alex.Main;

import com.alex.MainWindow.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            int roads = Integer.parseInt(JOptionPane.showInputDialog("Enter roads count"));
            if (roads <= 0){
                throw new Exception();
            }
            new MainWindow(roads);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "You should supply correct integer value!");
            System.exit(0);
        }

    }
}

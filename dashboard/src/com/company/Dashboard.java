package com.company;
import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class Dashboard extends JFrame{
    public static JFrame dashboard;
    public static void main(String[] args) throws Exception{
        Dashboard dashboards = new Dashboard();

    }

    public Dashboard(){
        JFrame dashboard = new JFrame();
        dashboard.setTitle("Dashboard");
        dashboard.setSize(800, 450);
        dashboard.setLocationRelativeTo(null);
        try{
            dashboard.setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (NullPointerException e){
            System.out.println("EXIT");
        }

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(Box.createRigidArea(new Dimension(500, 30)));

        JLabel introduction = new JLabel("*** A CEP-based SIEM System Dashboard ***");
        introduction.setPreferredSize(new Dimension(100, 225));
        box.add(introduction);
        introduction.setAlignmentX(introduction.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(900, 15)));


        JLabel introduction2 = new JLabel("\"********************************************\"");
        introduction2.setPreferredSize(new Dimension(100, 20));
        box.add(introduction2);
        introduction2.setAlignmentX(introduction.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(900, 15)));
        JLabel guide = new JLabel("Choose one of the following tasks");
        guide.setAlignmentX(guide.CENTER_ALIGNMENT);
        box.add(guide);

        JPanel decisions = new JPanel();
        decisions.setLayout(new GridLayout(3, 2, 100, 50));
        decisions.setBorder(BorderFactory.createEmptyBorder(35, 85, 35, 85));



        try{
            JButton d0 = Dashboard.buttonInformationSummary();
            JButton d1 = Dashboard.buttonParameters();
            JButton d2 = Dashboard.buttonAccessEvent();
            JButton d3 = Dashboard.buttonAttackEvent();
            JButton d4 = Dashboard.buttonRefresh();
            JButton d5 = Dashboard.buttonExit();


            decisions.add(d0);
            decisions.add(d1);
            decisions.add(d2);
            decisions.add(d3);
            decisions.add(d4);
            decisions.add(d5);


            box.add(decisions);

        }
        catch(NullPointerException | NumberFormatException exception){
            System.out.println("Cancel");
        }
        dashboard.add(box);
        dashboard.setVisible(true);
    }
    public static boolean isNumeric(String str){
        try{
            Integer.parseInt(str);
        }
        catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    public static String printEnterAgainDialog(String object){
        String a = JOptionPane.showInputDialog(dashboard,"Please enter your desired " + object);
        while((!isNumeric(a) || Integer.parseInt(a)<0 || Integer.parseInt(a)> 60) && a != null ){
            a = null;
            JOptionPane.showMessageDialog(
                    null,
                    "Your " + object + " is invalid please enter again",
                    "Invalid " + object,
                    JOptionPane.INFORMATION_MESSAGE);
            a = JOptionPane.showInputDialog(dashboard, "Enter " + object + ":");
            if (a == null){
                break;
            }
        }
        return a;
    }


    public static JButton buttonInformationSummary(){
        JButton d0 = new JButton("Information Summary");
        d0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e0) {
                informationBoard();
            }
        });
        return d0;
    }

    public static JButton buttonParameters(){
        JButton d1 = new JButton("Change parameters");
        d1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e0) {
                try{
                    String x = printEnterAgainDialog("x");
                    String y = printEnterAgainDialog("y");

                    int i = Integer.parseInt(x);
                    int j = Integer.parseInt(y);

                    JOptionPane.showMessageDialog(
                            null,
                            "Allert condition: X number of faiures in Y minutes"+ "\n" +
                                    "Current X is : " + i + "\n" +
                                    "Current Y is : " + j,
                            "Parameters Properties",
                            JOptionPane.INFORMATION_MESSAGE);
                }catch(Exception exception){
                    System.out.println("Dialog has been closed");
                }
            }
        });
        return d1;
    }

    public static JButton buttonAccessEvent() {
        JButton d2 = new JButton("Print Access Event");
        d2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e0) {
                try{

                    String[] columnNames = new String[]{"Hostname","IP","Time","URL"};
                    Object [][] data =new Object[][] {{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                    };

                    JTable accesseventTable = new JTable(data,columnNames);

                    JScrollPane scrollPane = new JScrollPane(accesseventTable);
                    accesseventTable.setFillsViewportHeight(true);
                    JOptionPane.showMessageDialog(
                            null,
                            scrollPane,
                            "Access Event Table ",
                            JOptionPane.INFORMATION_MESSAGE);

                }catch(Exception exception){
                    System.out.println("Table has been closed");
                }
            }
        });
        return d2;
    }

    public static JButton buttonAttackEvent() {
        JButton d3 = new JButton("Print Attack Event");
        d3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e0) {
                try{

                    String[] columnNames = new String[]{"Hostname","IP","Time","URL"};
                    Object [][] data =new Object[][] {{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                            {"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"},
                    };

                    JTable accesseventTable = new JTable(data,columnNames);

                    JScrollPane scrollPane = new JScrollPane(accesseventTable);
                    accesseventTable.setFillsViewportHeight(true);
                    JOptionPane.showMessageDialog(
                            null,
                            scrollPane,
                            "Attack Event Table",
                            JOptionPane.INFORMATION_MESSAGE);

                }catch(Exception exception){
                    System.out.println("Table has been closed");
                }
            }
        });
        return d3;
    }
    public static JButton buttonRefresh(){
        JButton d4 = new JButton("Refresh");
        d4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dashboard.setVisible(false);
                    Dashboard dashboards = new Dashboard();
                } catch (Exception exception) {
                    System.out.println("");
                }
            }
        });
        return d4;
    }



    public static JButton buttonExit(){
        JButton d5 = new JButton("Exit");
        d5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent exit) {
                System.exit(0);
            }
        });
        return d5;
    }

    public static void informationBoard(){
        JFrame frame = new JFrame();
        frame.setTitle("Information Summary Board");
        JPanel panel = new JPanel();
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        panel.add(Box.createRigidArea(new Dimension(1000, 5)));
        JLabel introduction1 = new JLabel("***Information Summary Board***");
        introduction1.setAlignmentX(panel.CENTER_ALIGNMENT);
        panel.add(introduction1);
        panel.add(Box.createRigidArea(new Dimension(1000, 1)));


        JLabel text1 = new JLabel("Some information here");
        panel.add(text1);
        panel.add(Box.createRigidArea(new Dimension(1000, 1)));

        JLabel text2 = new JLabel("Some information here");
        panel.add(text2);

        panel.add(Box.createRigidArea(new Dimension(1000, 1)));

        JLabel text3 = new JLabel("Some information here");
        panel.add(text3);

        panel.add(Box.createRigidArea(new Dimension(1000, 1)));

        JLabel text4 = new JLabel("Some information here");
        panel.add(text4);

        panel.add(Box.createRigidArea(new Dimension(1000, 1)));















        JLabel introduction2 = new JLabel("********************************************");
        introduction2.setAlignmentX(panel.CENTER_ALIGNMENT);
        panel.add(introduction2);
        panel.add(Box.createRigidArea(new Dimension(1000, 20)));

        JLabel introduction3 = new JLabel("Access Event Table");
        JLabel introduction4 = new JLabel("Attack Event Table");

        String[] columnNames = new String[]{"Hostname","IP","Time","URL"};

        DefaultTableModel dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(columnNames);
        DefaultTableModel dtm2 = new DefaultTableModel(0,0);
        dtm2.setColumnIdentifiers(columnNames);

        for (int count = 0; count <= 30; count ++)
        {
            dtm.addRow(new Object[]{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"});
        }
        for (int count = 0; count <= 30; count ++)
        {
            dtm2.addRow(new Object[]{"hieule","127.0.0.1","10 Octocer 2020","facebook.com"});
        }


        JTable table1 = new JTable();
        JTable table2 = new JTable();

        table1.setModel(dtm);
        table2.setModel(dtm2);



        JScrollPane scrollPane1 = new JScrollPane(table1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(table2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane1.setPreferredSize(new Dimension(600,200));
        scrollPane2.setPreferredSize(new Dimension(600,200));


        JPanel decisions = new JPanel();
        decisions.setLayout(new GridLayout(6, 1, 80, 50));
        decisions.setBorder(BorderFactory.createEmptyBorder(35, 25, 25, 15));




        JButton buttonBack = new JButton("Back");
        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent exit) {
                frame.setVisible(false);
            }
        });

        JButton buttonRefresh = new JButton("Refresh");
        buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    frame.setVisible(false);
                    informationBoard();
                } catch (Exception exception) {
                    System.out.println("");
                }
            }
        });




        try{
            JButton d1 = Dashboard.buttonParameters();
            JButton d2 = Dashboard.buttonAccessEvent();
            JButton d3 = Dashboard.buttonAttackEvent();
            JButton d5 = Dashboard.buttonExit();


            decisions.add(d1);
            decisions.add(d2);
            decisions.add(d3);
            decisions.add(buttonRefresh);
            decisions.add(buttonBack);
            decisions.add(d5);



        }
        catch(NullPointerException | NumberFormatException exception){
            System.out.println("Cancel");
        }

        JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,introduction3,scrollPane1);
        JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,introduction4,scrollPane2);
        JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane1,jSplitPane2);
        JSplitPane jSplitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jSplitPane3,decisions);




        panel.add(jSplitPane4);
        frame.add(panel);
        frame.setVisible(true);


    }




}

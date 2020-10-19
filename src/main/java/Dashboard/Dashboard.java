package Dashboard;
import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class Dashboard extends JFrame {

    public static JFrame dashboard;

//    final static Color HILIT_COLOR = Color.LIGHT_GRAY;
//    final static Color ERROR_COLOR = Color.PINK;
//    final static String CANCEL_ACTION = "cancel-search";

    public static void main(String[] args) {

        Dashboard dashboards = new Dashboard();

//        // Some features in the future
//        DefaultHighlighter hilit = new DefaultHighlighter();
//        DefaultHighlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter();

    }

    public Dashboard() {

        // Set up dashboard properties

        JFrame dashboard = new JFrame();
        dashboard.setTitle("Dashboard");
        dashboard.setSize(1000, 750);
        dashboard.setLocationRelativeTo(null);
        try{
            dashboard.setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (NullPointerException e){
            System.out.println("EXIT");
        }

        // Create first panel

        JPanel panel = new JPanel();
        panel.add(Box.createRigidArea(new Dimension(1000, 5)));

        // Add title

        JLabel introduction = new JLabel("A CEP-based SIEM System Dashboard");
        panel.add(introduction);
        introduction.setAlignmentX(panel.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(900, 15)));
        panel.add(introduction);
        panel.add(Box.createRigidArea(new Dimension(1000, 1)));

//        // Add some information
//
//        JLabel text1 = new JLabel("Some information here");
//        panel.add(text1);
//        panel.add(Box.createRigidArea(new Dimension(1000, 1)));
//
//        JLabel text2 = new JLabel("Some information here");
//        panel.add(text2);
//        panel.add(Box.createRigidArea(new Dimension(1000, 1)));
//
//        JLabel text3 = new JLabel("Some information here");
//        panel.add(text3);
//        panel.add(Box.createRigidArea(new Dimension(1000, 1)));
//
//        JLabel text4 = new JLabel("Some information here");
//        panel.add(text4);
//        panel.add(Box.createRigidArea(new Dimension(1000, 1)));

        // Add dividing line

        JLabel dLine = new JLabel("********************************************");
        dLine.setAlignmentX(panel.CENTER_ALIGNMENT);
        panel.add(dLine);
        panel.add(Box.createRigidArea(new Dimension(1000, 10)));

        // Create table 1

        JLabel table1Title = new JLabel("Access Event Table");
        String[] columnNames1 = new String[]{"usersName","httpStatusCode","timeStamp","accepted"};
        DefaultTableModel dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(columnNames1);
        for (int count = 0; count <= 30; count ++) {
            dtm.addRow(new Object[]{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"});
        }
        JTable table1 = new JTable();
        table1.setModel(dtm);


        // Create table 2

        JLabel table1Title2 = new JLabel("Access Event Table");
        String[] columnNames2 = new String[]{"message","clientIpAddess","timeStamp","loggInCommand"};
        DefaultTableModel dtm2 = new DefaultTableModel(0,0);
        dtm2.setColumnIdentifiers(columnNames2);
        for (int count = 0; count <= 30; count ++) {
            dtm2.addRow(new Object[]{"hieule","127.0.0.1","10 Octocer 2020","facebook.com"});
        }
        JTable table2 = new JTable();
        table2.setModel(dtm2);


        // Add table to scrollPane

        JScrollPane scrollPane1 = new JScrollPane(table1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(table2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane1.setPreferredSize(new Dimension(600,200));
        scrollPane2.setPreferredSize(new Dimension(600,200));


        // Create change parameters panel
        JLabel x = new JLabel("X");
        JLabel y = new JLabel ("Y");



        // Create panel for buttons

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(5, 1, 120, 50));
        buttons.setBorder(BorderFactory.createEmptyBorder(45, 15, 45, 15));

        // Add buttons

        JButton b1 = Dashboard.buttonParameters();
        JButton b2 = Dashboard.buttonAccessEvent();
        JButton b3 = Dashboard.buttonAttackEvent();
        JButton b4 = Dashboard.buttonRefresh();
        JButton b5 = Dashboard.buttonExit();


        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);
        buttons.add(b5);

        // Create text field and text area

        JTextField textField = new JTextField(20);
        JTextArea textArea = new JTextArea(6,20);
        JLabel status = new JLabel();
        JLabel jLabel1 = new JLabel();
        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane jScrollPane3 = new JScrollPane();
//        jLabel1.setText("Event text to search:");
//        GroupLayout layout = new GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
        jScrollPane3.add(textArea);


        // Create Split Pane

        JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title,scrollPane1);
        JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title2,scrollPane2);
        JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane1,jSplitPane2);
        JSplitPane jSplitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jSplitPane3,buttons);
        JSplitPane jSplitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,textField,textArea);
        JSplitPane jSplitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane4,jSplitPane5);


        // Complete set up for dashboard

        panel.add(jSplitPane6);
        dashboard.add(panel);
        dashboard.setVisible(true);
    }

    // Check parameters is numeric or not

    public static boolean isNumeric(String str){
        try{
            Integer.parseInt(str);
        }
        catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    // Change parameters button

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

    // Create print parameters again dialog

    public static String printEnterAgainDialog(String object){
        String variable = JOptionPane.showInputDialog(dashboard,"Please enter your desired " + object);
        while((!isNumeric(variable) || Integer.parseInt(variable)<0) && variable != null ){
            variable = null;
            JOptionPane.showMessageDialog(
                    null,
                    "Your " + object + " is invalid please enter again",
                    "Invalid " + object,
                    JOptionPane.INFORMATION_MESSAGE);
            variable = JOptionPane.showInputDialog(dashboard, "Enter " + object + ":");
            if (variable == null){
                break;
            }
        }
        return variable;
    }

    // Create print Access Event table to file button

    public static JButton buttonAccessEvent(){
        JButton d2 = new JButton("Print Access Event");
        d2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        dashboard,
                        "Print Access Event Table and Alert to file" ,
                        "Print",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return d2;
    }

    // Create print Attack Event table to file button

    public static JButton buttonAttackEvent(){
        JButton d3 = new JButton("Print Attack Event");
        d3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        dashboard,
                        "Print Attack Event Table and Alert to file" ,
                        "Print",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return d3;
    }

    // Create Refresh button

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

    // Create Exit button

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

    public void search(){
    }
}
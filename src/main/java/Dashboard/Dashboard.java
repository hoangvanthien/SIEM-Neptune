package Dashboard;

import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Dashboard extends JFrame implements DocumentListener {

    public static JFrame dashboard;
    public JTextArea textArea;
    public static JScrollPane jScrollPane3;
    final static Color HILIT_COLOR = Color.LIGHT_GRAY;
    final static Color ERROR_COLOR = Color.PINK;
    final static String CANCEL_ACTION = "cancel-search";
    final static String ENTER_ACTION = "enter-search";
    public static int count= 0;
    public JTextField textField;


    final Color entryBg;
    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;

    public static void main(String[] args) throws Exception {
        Dashboard dashboards = new Dashboard();
        for (int count = 0; count <= 30; count ++) {
            dashboards.dtm.addRow(new Object[]{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"});
            Thread.sleep(1000);
        }
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                UIManager.put("swing.boldMetal", Boolean.FALSE);
//                try {
//
//
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        // Some features in the future
//        DefaultHighlighter hilit = new DefaultHighlighter();
//        DefaultHighlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter();
    }

    public DefaultTableModel dtm, dtm2;
    public Dashboard() throws InterruptedException {
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
        dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(columnNames1);
//        for (int count = 0; count <= 30; count ++) {
//            dtm.addRow(new Object[]{"thienhoang","127.0.0.1","10 Octocer 2020","facebook.com"});
//        }
        JTable table1 = new JTable();
        table1.setModel(dtm);
        // Create table 2
        JLabel table1Title2 = new JLabel("Access Event Table");
        String[] columnNames2 = new String[]{"message","clientIpAddess","timeStamp","loggInCommand"};
        dtm2 = new DefaultTableModel(0,0);
        dtm2.setColumnIdentifiers(columnNames2);
//        for (int count = 0; count <= 30; count ++) {
//            dtm2.addRow(new Object[]{"hieule","127.0.0.1","10 Octocer 2020","facebook.com"});
//        }
        JTable table2 = new JTable();
        table2.setModel(dtm2);
        // Add table to scrollPane
        JScrollPane scrollPane1 = new JScrollPane(table1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(table2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane1.setPreferredSize(new Dimension(600,200));
        scrollPane2.setPreferredSize(new Dimension(600,200));
        // Create change parameters panel
        JPanel parameters1 = new JPanel();
        parameters1.setLayout(new GridLayout(2, 2));
        JLabel x = new JLabel(" X: ");
        JLabel y = new JLabel(" Y: ");
        JTextField xTextField = new JTextField("",3);
        JTextField yTextField = new JTextField("",3);
        parameters1.add(x);
        parameters1.add(xTextField);
        parameters1.add(y);
        parameters1.add(yTextField);
        JPanel parameters2 = new JPanel();
        parameters2.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JTextField displayX = new JTextField(3);
        displayX.setEditable(false);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        parameters2.add(displayX,c);
        JTextField displayY = new JTextField(3);
        displayY.setEditable(false);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        parameters2.add(displayY,c);
        JButton setPara = new JButton("Set");
        setPara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String a = null;
                String b = null;
                a = xTextField.getText();
                b = yTextField.getText();
                a = printEnterAgain("X",a);
                b = printEnterAgain("Y",b);

                displayX.setText(a);
                displayY.setText(b);
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        parameters2.add(setPara,c);
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
        JButton searchButton = new JButton("Search");
        textField = new JTextField(20);
        textArea = new JTextArea("Team Nepturn \n",5,20);

        JLabel status = new JLabel();
        JLabel jLabel1 = new JLabel();


        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);


        JScrollPane jScrollPane3 = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        System.setOut(printStream);
        System.setErr(printStream);


        //Create Search box
        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
        textArea.setHighlighter(hilit);

        entryBg = textField.getBackground();
        textField.getDocument().addDocumentListener(this);

        InputMap im = textField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = textField.getActionMap();
        im.put(KeyStroke.getKeyStroke("ESCAPE"),CANCEL_ACTION);
        am.put(CANCEL_ACTION,new CancelAction());

        InputMap im2 = textField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am2 = textField.getActionMap();
        im2.put(KeyStroke.getKeyStroke("ENTER"),ENTER_ACTION);
        am2.put(ENTER_ACTION,new addCount());



        // Enable drag and drop
        table1.setDragEnabled(true);
        table2.setDragEnabled(true);
        textArea.setDragEnabled(true);
        textField.setDragEnabled(true);



//        jLabel1.setText("Event text to search:");
//        GroupLayout layout = new GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
        // Create Split Pane
        JSplitPane parameters3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters1,parameters2);
        JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title,scrollPane1);
        JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title2,scrollPane2);
        JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane1,jSplitPane2);
        JSplitPane jSplitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,parameters3,buttons);
        JSplitPane jSplitPane5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jSplitPane3,jSplitPane4);
        JSplitPane jSplitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,textField,jScrollPane3);
        JSplitPane jSplitPane7 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane5,jSplitPane6);
        // Complete set up for dashboard
        panel.add(jSplitPane7);
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

    public static String printEnterAgain(String object,String variable){
        if((!isNumeric(variable) || Integer.parseInt(variable)<0) || variable == null ){
            variable = null;
            System.out.println("Your " + object + " is invalid please enter again");
        }
        return variable;
    }


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
//                JOptionPane.showMessageDialog(
//                        dashboard,
//                        "Print Access Event Table and Alert to file" ,
//                        "Print",
//                        JOptionPane.INFORMATION_MESSAGE);

                System.out.println("Print Access Event Table and Alert to file" );
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
//                JOptionPane.showMessageDialog(
//                        dashboard,
//                        "Print Attack Event Table and Alert to file" ,
//                        "Print",
//                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Print Access Event Table and Alert to file" );

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
                    dashboard.revalidate();
                    dashboard.repaint();
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
        hilit.removeAllHighlights();
        String s = textField.getText();
        if (s.length()<=0){
            return;
        }

        String content = textArea.getText();
        int index = content.indexOf(s,count);
        if (index>=0){
            try{
                int end = index + s.length();
                hilit.addHighlight(index,end,painter);
                textArea.setCaretPosition(end);
                textField.setBackground(entryBg);
            }
            catch (BadLocationException e){
                e.printStackTrace();
            }
        }
        else {
            textField.setBackground(ERROR_COLOR);
        }
    }



    class addCount extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = textField.getText();
            String content = textArea.getText();
            count = content.indexOf(s,count) + s.length();
            search();
        }
    }



    @Override
    public void insertUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
    class CancelAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            count = 0;
            hilit.removeAllHighlights();
            textField.setText("");
            textField.setBackground(entryBg);
        }
    }

}

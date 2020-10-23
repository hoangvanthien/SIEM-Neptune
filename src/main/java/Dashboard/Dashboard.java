package Dashboard;

import CEP.WebserverMonitor.ApacheAccessLogCEP;
import CEP.WebserverMonitor.FailedRegisterDuplicateEvent;
import CEP.WebserverMonitor.Monitor;
import CEP.WebserverMonitor.NeptuneErrorLogCEP;
import Utilities.EPAdapter;

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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.PrintStream;
import java.util.ArrayList;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Dashboard extends JFrame implements DocumentListener, ActionListener {

    public static JFrame dashboard;
    public static JScrollPane jScrollPane3;
    final static Color HILIT_COLOR = Color.LIGHT_GRAY;
    final static Color ERROR_COLOR = Color.PINK;
    final static String CANCEL_ACTION = "cancel-search";
    final static String ENTER_ACTION = "enter-search";
    public static int count= 0;
    public static Dashboard dashboards;
    public static int index = 0;

    public JTextField textField;
    public JTextArea textArea;
    public DefaultTableModel dtm,dtm2,dtm3;
    public JTable table1;
    public int x=10;
    public int y=3;
    public int[] xList = {10,10,10,10};
    public int[] yList = {3,3,3,3};

    final Color entryBg;
    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;

    public static void main(String[] args) throws Exception {

        dashboards = new Dashboard();
        Monitor.execute();

    }

    public Dashboard() {

        // Set up dashboard properties

        JFrame dashboard = new JFrame();
        dashboard.setTitle("Dashboard");
        dashboard.setSize(1000, 815);
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
        JLabel dLine = new JLabel("********************************************");
        dLine.setAlignmentX(panel.CENTER_ALIGNMENT);
        panel.add(dLine);
        panel.add(Box.createRigidArea(new Dimension(1000, 10)));

        // Create table 1

        JLabel table1Title = new JLabel(" Access Log Table");
        String[] columnNames1 = new String[]{" Time "," Client Address "," URL "," Status Code "," Request Method "};
        dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(columnNames1);
        table1 = new JTable();
        table1.setModel(dtm);
        table1.getColumnModel().getColumn(0).setPreferredWidth(120);
        table1.getColumnModel().getColumn(2).setPreferredWidth(50);
        table1.getColumnModel().getColumn(3).setPreferredWidth(50);

        // Create table 2

        JLabel table1Title2 = new JLabel(" Error Log Table");
        String[] columnNames2 = new String[]{" Time "," Client Address "," URL "," Log Message"};
        dtm2 = new DefaultTableModel(0,0);
        dtm2.setColumnIdentifiers(columnNames2);
        JTable table2 = new JTable();
        table2.setModel(dtm2);

        // Create table 3

        JLabel table1Title3 = new JLabel(" Port Scan Table");
        String[] columnNames3 = new String[]{" Time "," Client Address "," Port "," Port Status"};
        dtm3 = new DefaultTableModel(0,0);
        dtm3.setColumnIdentifiers(columnNames3);
        JTable table3 = new JTable();
        table3.setModel(dtm3);

        // Add table to scrollPane

        JScrollPane scrollPane1 = new JScrollPane(table1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(table2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane4 = new JScrollPane(table3,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Make scroll always at bottom

        scrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        scrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        scrollPane4.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        // Set size of tables

        scrollPane1.setPreferredSize(new Dimension(600,135));
        scrollPane2.setPreferredSize(new Dimension(600,135));
        scrollPane4.setPreferredSize(new Dimension(600,135));

        // Create change parameters panel

        JPanel parameters1 = new JPanel();
        parameters1.setLayout(new GridLayout(2, 2));
        JLabel xLabel = new JLabel(" X: ");
        JLabel yLabel = new JLabel(" Y: ");
        JTextField xTextField = new JTextField("10",3);
        JTextField yTextField = new JTextField("3",3);
        xTextField.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        yTextField.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        parameters1.add(xLabel);
        parameters1.add(xTextField);
        parameters1.add(yLabel);
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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        displayX.setText("10");
        displayY.setText("3");

        // Set button

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
                if(isNumeric(a)&&isNumeric(b)) {
                    xList[index] = Integer.parseInt(a);
                    yList[index] = Integer.parseInt(b);
                }
                displayX.setText(Integer.toString(xList[index]));
                displayY.setText(Integer.toString(yList[index]));

                switch(index) {
                    case 1:
                        ApacheAccessLogCEP.setPeriod(yList[index]);
                        ApacheAccessLogCEP.setThreshold(xList[index]);
                    case 2:
                        NeptuneErrorLogCEP.setFailedLoginEvent_period(yList[index]);
                        NeptuneErrorLogCEP.setFailedLoginEventByUsername_threshold(xList[index]);
                    case 3:
                        NeptuneErrorLogCEP.setFailedLoginEvent_period(yList[index]) ;
                        NeptuneErrorLogCEP.setFailedLoginEventByPassword_threshold(xList[index]);
                    case 4:
                        NeptuneErrorLogCEP.setFailedRegisterDuplicateEvent_period(yList[index]);
                        NeptuneErrorLogCEP.setFailedRegisterDuplicateEvent_threshold(xList[index]);
                }
                new EPAdapter();
            }
        });
        setPara.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        parameters2.add(setPara,c);

        // Create panel for buttons

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(5, 1, 120, 50));
        buttons.setBorder(BorderFactory.createEmptyBorder(45, 15, 45, 15));

        // Add buttons

        JButton b1 = Dashboard.buttonParameters();
        JButton b2 = new JButton("Information Summary");
        JButton b3 = Dashboard.buttonAttackEvent();
        JButton b4 = Dashboard.buttonRefresh();
        JButton b5 = Dashboard.buttonExit();
        buttons.add(b2);
        buttons.add(b1);
        buttons.add(b3);
        buttons.add(b4);
        buttons.add(b5);

        // Listeners for buttton information summary

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dtm.getRowCount()==0) {
                    System.out.println("Access Log Table : No Event" );
                }
                else{
                    System.out.println("Access Log Table :" + dtm.getRowCount() + " Events From " + dtm.getValueAt(0,dtm.getColumnCount()-3) + " to "+  dtm.getValueAt(dtm.getRowCount()-1,dtm.getColumnCount()-3));
                }
                if(dtm2.getRowCount()==0) {
                    System.out.println("Event Log Table : No Event" );
                }
                else{
                    System.out.println("Event Log Table :" + dtm2.getRowCount() + " Events From " + dtm2.getValueAt(0,dtm2.getColumnCount()-2) + " to "+  dtm2.getValueAt(dtm2.getRowCount()-1,dtm2.getColumnCount()-2));
                }
                if(dtm3.getRowCount()==0) {
                    System.out.println("Port Scan Table : No Event" );
                }
                else{
                    System.out.println("Port Scan Table :" + dtm3.getRowCount() + " Events From " + dtm3.getValueAt(0,dtm3.getColumnCount()-2) + " to "+  dtm3.getValueAt(dtm3.getRowCount()-1,dtm3.getColumnCount()-2));
                }



            }
        });

        // Create text field and text area

        JButton searchButton = new JButton("Search");
        textField = new JTextField(20);
        textArea = new JTextArea("Team Nepturn \n",7,20);
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

        // Add Drop Down Menu

        String[] eventString = { "AccessEvent", "FailedLoginUsername", "FailedLoginPassword", "FailedRegisterDuplicate"};
        JComboBox eventsList = new JComboBox(eventString);
        eventsList.setSelectedIndex(0);
        eventsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index = eventsList.getSelectedIndex();
                displayX.setText(Integer.toString(xList[index]));
                displayY.setText(Integer.toString(yList[index]));
            }
        });

        // Create Split Pane

        JSplitPane parameters3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters1,parameters2);
        JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title,scrollPane1);
        JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title2,scrollPane2);
        JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane1,jSplitPane2);
        JSplitPane jSplitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,parameters3,buttons);
        JSplitPane jSplitPane10 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,eventsList,jSplitPane4);
        JSplitPane jSplitPane8 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title3,scrollPane4);
        JSplitPane jSplitPane9 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane3,jSplitPane8);
        JSplitPane jSplitPane5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jSplitPane9,jSplitPane10);
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

    // Print console note button

    public static JButton buttonParameters(){
        JButton d1 = new JButton("Print Console Note");
        d1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e0) {
                System.out.println("Print Console Note to file" );
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

    // Create print Attack Event table to file button

    public static JButton buttonAttackEvent(){
        JButton d3 = new JButton("Print Tables");
        d3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Print All Tables to file" );
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

    // Search function for search box

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

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    // Add Count for search

    class addCount extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = textField.getText();
            String content = textArea.getText();
            count = content.indexOf(s,count) + s.length();
            search();
        }
    }

    // Document Event

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

    // Cancel Action Search box

    class CancelAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            count = 0;
            hilit.removeAllHighlights();
            textField.setText("");
            textField.setBackground(entryBg);
        }
    }

    // Future features
//                try{
//                    String x = printEnterAgainDialog("x");
//                    String y = printEnterAgainDialog("y");
//                    int i = Integer.parseInt(x);
//                    int j = Integer.parseInt(y);
//                    JOptionPane.showMessageDialog(
//                            null,
//                            "Allert condition: X number of faiures in Y minutes"+ "\n" +
//                                    "Current X is : " + i + "\n" +
//                                    "Current Y is : " + j,
//                            "Parameters Properties",
//                            JOptionPane.INFORMATION_MESSAGE);
//                }catch(Exception exception){
//                    System.out.println("Dialog has been closed");
//                }

}
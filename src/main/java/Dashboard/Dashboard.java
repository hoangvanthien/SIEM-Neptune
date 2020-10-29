package Dashboard;

import CEP.WebserverMonitor.ApacheAccessLogCEP;
import CEP.WebserverMonitor.FailedRegisterDuplicateEvent;
import CEP.WebserverMonitor.Monitor;
import CEP.WebserverMonitor.NeptuneErrorLogCEP;
import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import de.siegmar.fastcsv.writer.CsvWriter;
import javax.swing.event.PopupMenuListener;
import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

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
    public DefaultTableModel dtm,dtm2,dtm3,dtm0;
    public JTable table1;
    public int x=10;
    public int y=3;
    public int[] xList = {3,3,3,3};
    public int[] yList = {10,10,10,10};
    public int[] xList2 = {3,3,3,3};
    public int[] yList2 = {10,10,10,10};
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
        dashboard.setSize(1000, 825);
        dashboard.setLocationRelativeTo(null);
        try{
            dashboard.setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (NullPointerException e){
            System.out.println("EXIT");
        }

        // Create panel

        JPanel panel = new JPanel();
        panel.add(Box.createRigidArea(new Dimension(1000, 5)));

        // Add title
        JLabel introduction = new JLabel("A CEP-based SIEM System Dashboard" );
        panel.add(introduction);
        introduction.setHorizontalAlignment(JLabel.CENTER);
        panel.add(Box.createRigidArea(new Dimension(2000, 15)));
        panel.add(introduction);
        panel.add(Box.createRigidArea(new Dimension(2000, 1)));
        JLabel dLine = new JLabel("********************************************");
        dLine.setHorizontalAlignment(JLabel.CENTER);
        panel.add(dLine);
        panel.add(Box.createRigidArea(new Dimension(2000, 10)));

        // Create table 1

        JLabel table1Title = new JLabel("Access Log Table");
        String[] columnNames1 = new String[]{" Time "," Client Address "," URL "," Status Code "," Request Method "};
        dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(columnNames1);
        table1 = new JTable();
        table1.setModel(dtm);
        table1.getColumnModel().getColumn(0).setPreferredWidth(120);
        table1.getColumnModel().getColumn(2).setPreferredWidth(50);
        table1.getColumnModel().getColumn(3).setPreferredWidth(50);

        // Create table 2

        JLabel table1Title2 = new JLabel("Error Log Table");
        String[] columnNames2 = new String[]{" Time "," Client Address "," URL "," Log Message"};
        dtm2 = new DefaultTableModel(0,0);
        dtm2.setColumnIdentifiers(columnNames2);
        JTable table2 = new JTable();
        table2.setModel(dtm2);
        table2.getColumnModel().getColumn(0).setPreferredWidth(100);

        // Create table 3

        JLabel table1Title3 = new JLabel("Port Scan Table");
        String[] columnNames3 = new String[]{" Time "," Client Address "," Port "," Port Status","Type"};
        dtm3 = new DefaultTableModel(0,0);
        dtm3.setColumnIdentifiers(columnNames3);
        JTable table3 = new JTable();
        table3.setModel(dtm3);
        table3.getColumnModel().getColumn(0).setPreferredWidth(120);
        table3.getColumnModel().getColumn(2).setPreferredWidth(50);
        table3.getColumnModel().getColumn(4).setPreferredWidth(50);


        // Create table 0

        JLabel table1Title0 = new JLabel("Alert Message Table");
        String[] columnNames0 = new String[]{" Time ","Priority","Message"};
        dtm0 = new DefaultTableModel(0,0);
        dtm0.setColumnIdentifiers(columnNames0);
        JTable table0 = new JTable();
        table0.setModel(dtm0);
        table0.getColumnModel().getColumn(0).setPreferredWidth(120);
        table0.getColumnModel().getColumn(1).setPreferredWidth(45);
        table0.getColumnModel().getColumn(2).setPreferredWidth(310);



        // Add table to scrollPane

        JScrollPane scrollPane1 = new JScrollPane(table1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane2 = new JScrollPane(table2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane4 = new JScrollPane(table3,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPane0 = new JScrollPane(table0,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        // Make scroll always at bottom and can scroll up when needed

       AtomicInteger verticalScrollBarMaximumValue = new AtomicInteger(scrollPane1.getVerticalScrollBar().getMaximum());
        scrollPane1.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((verticalScrollBarMaximumValue.get() - e.getAdjustable().getMaximum()) == 0)
                        return;
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    verticalScrollBarMaximumValue.set(scrollPane1.getVerticalScrollBar().getMaximum());
                });

        AtomicInteger verticalScrollBarMaximumValue2 = new AtomicInteger(scrollPane2.getVerticalScrollBar().getMaximum());
        scrollPane2.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((verticalScrollBarMaximumValue2.get() - e.getAdjustable().getMaximum()) == 0)
                        return;
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    verticalScrollBarMaximumValue2.set(scrollPane2.getVerticalScrollBar().getMaximum());
                });

        AtomicInteger verticalScrollBarMaximumValue4 = new AtomicInteger(scrollPane4.getVerticalScrollBar().getMaximum());
        scrollPane4.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((verticalScrollBarMaximumValue4.get() - e.getAdjustable().getMaximum()) == 0)
                        return;
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    verticalScrollBarMaximumValue4.set(scrollPane4.getVerticalScrollBar().getMaximum());
                });

        AtomicInteger verticalScrollBarMaximumValue0 = new AtomicInteger(scrollPane0.getVerticalScrollBar().getMaximum());
        scrollPane0.getVerticalScrollBar().addAdjustmentListener(
                e -> {
                    if ((verticalScrollBarMaximumValue0.get() - e.getAdjustable().getMaximum()) == 0)
                        return;
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    verticalScrollBarMaximumValue0.set(scrollPane0.getVerticalScrollBar().getMaximum());
                });





        // Set size of tables

        scrollPane1.setPreferredSize(new Dimension(600,135));
        scrollPane2.setPreferredSize(new Dimension(600,135));
        scrollPane4.setPreferredSize(new Dimension(600,135));
        scrollPane0.setPreferredSize(new Dimension(600,135));



        // Create change parameters panel


        // Create low priority panel

        JLabel lowPriority = new JLabel(" Low Priority ");
        lowPriority.setHorizontalAlignment(JLabel.CENTER);

        // First box

        JPanel parameters1 = new JPanel();
        parameters1.setLayout(new GridLayout(2, 1));
        JLabel xLabel = new JLabel(" Threshhold ");
        JLabel yLabel = new JLabel(" Period ");
        JTextField xTextField = new JTextField(""+xList[0],2);
        JTextField yTextField = new JTextField(""+yList[0],2);
        xTextField.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        yTextField.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        parameters1.add(xLabel);
        parameters1.add(yLabel);

        // Second box


        JPanel parameters4 = new JPanel();
        parameters4.setLayout(new GridLayout(2, 1));
        parameters4.add(xTextField);
        parameters4.add(yTextField);

        // Third box
        JPanel parameters2 = new JPanel();
        parameters2.setLayout(new GridLayout(2, 1));
        JTextField displayX = new JTextField(3);
        displayX.setEditable(false);
        parameters2.add(displayX);
        JTextField displayY = new JTextField(3);
        displayY.setEditable(false);
        parameters2.add(displayY);
        displayX.setText(""+xList[0] +" events");
        displayY.setText(""+yList[0]+" seconds");
        displayX.setHorizontalAlignment(JTextField.CENTER);
        displayY.setHorizontalAlignment(JTextField.CENTER);


        // Create high Priority panel

        JLabel highPriority = new JLabel(" High Priority ");
        highPriority.setHorizontalAlignment(JLabel.CENTER);

        // First box

        JPanel parameters8 = new JPanel();
        parameters8.setLayout(new GridLayout(2, 1));
        JLabel xLabel2 = new JLabel(" Threshhold ");
        JLabel yLabel2 = new JLabel(" Period ");
        JTextField xTextField2 = new JTextField(""+xList2[0],2);
        JTextField yTextField2 = new JTextField(""+yList2[0],2);
        xTextField2.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        yTextField2.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");
        parameters8.add(xLabel2);
        parameters8.add(yLabel2);

        // Second box

        JPanel parameters9 = new JPanel();
        parameters9.setLayout(new GridLayout(2, 1));
        parameters9.add(xTextField2);
        parameters9.add(yTextField2);
        JSplitPane parameters10 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters8,parameters9);

        // Third box

        JPanel parameters11 = new JPanel();
        parameters11.setLayout(new GridLayout(2, 1));
        JTextField displayX2 = new JTextField(3);
        displayX2.setEditable(false);
        parameters11.add(displayX2);
        JTextField displayY2 = new JTextField(3);
        displayY2.setEditable(false);
        parameters11.add(displayY2);
        displayX2.setText(""+xList2[0] +" events");
        displayY2.setText(""+yList2[0]+" seconds");
        displayX2.setHorizontalAlignment(JTextField.CENTER);
        displayY2.setHorizontalAlignment(JTextField.CENTER);


        // Create buttons

        // Create panel for buttons

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(6, 1, 120, 25));
        buttons.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Set buttons

        JButton setPara = new JButton("Set");
        setPara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String a = null;
                String b = null;
                String a2 = null;
                String b2 = null;
                a = xTextField.getText();
                b = yTextField.getText();
                a2 = xTextField2.getText();
                b2 = yTextField2.getText();
                a = printEnterAgain("X for Low Priority",a);
                b = printEnterAgain("Y for Low Priority",b);
                a2 = printEnterAgain("X for High Priority",a2);
                b2 = printEnterAgain("Y for High Priority",b2);

                if(isNumeric(a)&&isNumeric(b)&&isNumeric(a2)&&isNumeric(b2)) {
                    xList[index] = Integer.parseInt(a);
                    yList[index] = Integer.parseInt(b);
                    xList2[index] = Integer.parseInt(a2);
                    yList2[index] = Integer.parseInt(b2);
                }
                displayX.setText(Integer.toString(xList[index])+" events");
                displayY.setText(Integer.toString(yList[index])+" seconds");
                displayX2.setText(Integer.toString(xList2[index])+" events");
                displayY2.setText(Integer.toString(yList2[index])+" seconds");

                switch(index) {
                    case 0:
                        ApacheAccessLogCEP.setPeriod(yList[index]);
                        ApacheAccessLogCEP.setThreshold(xList[index]);
                        break;
                    case 1:
                        NeptuneErrorLogCEP.setFailedLoginEvent_period(yList[index]);
                        NeptuneErrorLogCEP.setFailedLoginEventByUsername_threshold(xList[index]);
                        break;
                    case 2:
                        NeptuneErrorLogCEP.setFailedLoginEvent_period(yList[index]) ;
                        NeptuneErrorLogCEP.setFailedLoginEventByPassword_threshold(xList[index]);
                        break;
                    case 3:
                        NeptuneErrorLogCEP.setFailedRegisterDuplicateEvent_period(yList[index]);
                        NeptuneErrorLogCEP.setFailedRegisterDuplicateEvent_threshold(xList[index]);
                        break;
                }
                try {
                    ApacheAccessLogCEP.setup();
                    NeptuneErrorLogCEP.setup();
                } catch (EPCompileException | EPDeployException | NoSuchFieldException | IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        });
        setPara.setToolTipText("Raise an alert when X events of the chosen type occur in Y seconds");

        // Add buttons

        JButton b1 = Dashboard.buttonParameters();
        JButton b2 = new JButton("Information Summary");
        JButton b3 = new JButton("Print Tables");
        JButton b4 = new JButton("Refresh");
        JButton b5 = Dashboard.buttonExit();
        buttons.add(setPara);
        buttons.add(b2);
        buttons.add(b1);
        buttons.add(b3);
        buttons.add(b4);
        buttons.add(b5);

        // Listeners for buttton information summary

        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dtm0.getRowCount()==0) {
                    System.out.println("Alert Table : No Event" );
                }
                else{
                    System.out.println("Alert :" + dtm0.getRowCount() + " Events From " + dtm0.getValueAt(0,0) + " to "+  dtm0.getValueAt(dtm0.getRowCount()-1,0));
                }
               if(dtm.getRowCount()==0) {
                    System.out.println("Access Log Table : No Event" );
                }
                else{
                    System.out.println("Access Log Table :" + dtm.getRowCount() + " Events From " + dtm.getValueAt(0,0) + " to "+  dtm.getValueAt(dtm.getRowCount()-1,0));
                }
                if(dtm2.getRowCount()==0) {
                    System.out.println("Event Log Table : No Event" );
                }
                else{
                    System.out.println("Event Log Table :" + dtm2.getRowCount() + " Events From " + dtm2.getValueAt(0,0) + " to "+  dtm2.getValueAt(dtm2.getRowCount()-1,0));
                }
                if(dtm3.getRowCount()==0) {
                    System.out.println("Port Scan Table : No Event" );
                }
                else{
                    System.out.println("Port Scan Table :" + dtm3.getRowCount() + " Events From " + dtm3.getValueAt(0,0) + " to "+  dtm3.getValueAt(dtm3.getRowCount()-1,0));
                }
            }
        });

        // Clear button

        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea.setText("");
                    dtm.setRowCount(0);
                    dtm2.setRowCount(0);
                    dtm3.setRowCount(0);
                    dtm0.setRowCount(0);
                    dashboard.revalidate();
                    dashboard.repaint();
                } catch (Exception exception) {
                    System.out.println("");
                }
            }
        });



        // Add buttons print

        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file1 = new File("table1.csv");
                File file2 = new File("table2.csv");
                File file3 = new File("table3.csv");
                File file0 = new File("table0.csv");

                CsvWriter csvWriter = new CsvWriter();

                TableModel model = table1.getModel();
                TableModel model2 = table2.getModel();
                TableModel model3 = table3.getModel();
                TableModel model0 = table0.getModel();


                FileWriter csv = null;
                try {
                    csv = new FileWriter(file0);
                    for (int i = 0; i < model0.getColumnCount(); i++) {
                        csv.write(model0.getColumnName(i) + ",");

                    }
                    csv.write("\n");
                    for (int i = 0; i < model0.getRowCount(); i++) {
                        for (int j = 0; j < model0.getColumnCount(); j++) {
                            csv.write(model0.getValueAt(i, j).toString() + ",");
                        }
                    }
                    csv.write("\n");
                    csv.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                try {
                    csv = new FileWriter(file1);
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        csv.write(model.getColumnName(i) + ",");

                    }
                    csv.write("\n");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            csv.write(model.getValueAt(i, j).toString() + ",");
                        }
                    }
                    csv.write("\n");
                    csv.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                try {
                    csv = new FileWriter(file2);
                    for (int i = 0; i < model2.getColumnCount(); i++) {
                        csv.write(model2.getColumnName(i) + ",");

                    }
                    csv.write("\n");
                    for (int i = 0; i < model2.getRowCount(); i++) {
                        for (int j = 0; j < model2.getColumnCount(); j++) {
                            csv.write(model2.getValueAt(i, j).toString() + ",");
                        }
                    }
                    csv.write("\n");
                    csv.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                try {
                    csv = new FileWriter(file3);
                    for (int i = 0; i < model3.getColumnCount(); i++) {
                        csv.write(model3.getColumnName(i) + ",");

                    }
                    csv.write("\n");
                    for (int i = 0; i < model3.getRowCount(); i++) {
                        for (int j = 0; j < model3.getColumnCount(); j++) {
                            csv.write(model3.getValueAt(i, j).toString() + ",");
                        }
                    }
                    csv.write("\n");
                    csv.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                System.out.println("Print All Tables to file" );
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
         // Solution 1

//        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
//       System.setOut(printStream);
//        System.setErr(printStream);

        // Solution 2

//        OutputStream outputStream = new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//
//            }
//        };
//        TextAreaPrintStream textAreaPrintStream = new TextAreaPrintStream(textArea,outputStream);
//        System.setOut(textAreaPrintStream);
//        System.setErr(textAreaPrintStream);

        // Solution 3

        MessageConsole mc = new MessageConsole(textArea);
        mc.redirectOut();
        mc.redirectErr(RED, null);
        mc.setMessageLines(1000);


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
        table3.setDragEnabled(true);
        table0.setDragEnabled(true);
        displayX.setDragEnabled(true);
        displayY.setDragEnabled(true);
        displayX2.setDragEnabled(true);
        displayY2.setDragEnabled(true);
        
        // Add Drop Down Menu

        String[] eventString = { "Bad requests", "Failed logins on one username", "Failed logins on one password", "Failed registrations from one client"};
        JComboBox eventsList = new JComboBox(eventString);
        eventsList.setSelectedIndex(0);
        eventsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                index = eventsList.getSelectedIndex();
                displayX.setText(Integer.toString(xList[index])+" events");
                displayY.setText(Integer.toString(yList[index])+ " seconds");
                displayX2.setText(Integer.toString(xList2[index])+" events");
                displayY2.setText(Integer.toString(yList2[index])+ " seconds");
                xTextField.setText(Integer.toString(xList[index]));
                yTextField.setText(Integer.toString(yList[index]));
                xTextField2.setText(Integer.toString(xList2[index]));
                yTextField2.setText(Integer.toString(yList2[index]));
            }
        });

        // Make popup more convenient

        eventsList.setLightWeightPopupEnabled(true);
        eventsList.setPrototypeDisplayValue("XXXXX");
        BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
        eventsList.addPopupMenuListener(listener);
        eventsList.setPrototypeDisplayValue("XXXXX");



        // Some alignment for tables's title

        table1Title.setHorizontalAlignment(JLabel.CENTER);
        table1Title2.setHorizontalAlignment(JLabel.CENTER);
        table1Title3.setHorizontalAlignment(JLabel.CENTER);
        table1Title0.setHorizontalAlignment(JLabel.CENTER);



        // Create Split Pane

        JSplitPane parameters5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters1,parameters4);
        JSplitPane parameters12 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters10,parameters11);
        JSplitPane parameters14 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,highPriority,parameters12);
        JSplitPane parameters3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,parameters5,parameters2);
        JSplitPane parameters6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,lowPriority,parameters3);
        JSplitPane parameters7 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,parameters6,parameters14);
        JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title,scrollPane1);
        JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title2,scrollPane2);
        JSplitPane jSplitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,parameters7,buttons);
        JSplitPane jSplitPane10 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,eventsList,jSplitPane4);
        JSplitPane jSplitPane8 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title3,scrollPane4);
        JSplitPane jSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,table1Title0,scrollPane0);



        // Create Tab for Tables

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Alert Message Table",null,jSplitPane3,"Click to show table 1");
        tabbedPane.addTab("Access Log Table",null,jSplitPane1,"Click to show table 2");
        tabbedPane.addTab("Error Log Table",null,jSplitPane2,"Click to show table 3");
        tabbedPane.addTab("Port Scan Table",null,jSplitPane8,"Click to show table 4");



        // Complete set up for dashboard

        JSplitPane jSplitPane5 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tabbedPane,jSplitPane10);
        JSplitPane jSplitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,textField,jScrollPane3);
        JSplitPane jSplitPane7 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jSplitPane5,jSplitPane6);
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
}

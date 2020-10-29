package Dashboard;

import java.io.*;
import javax.swing.*;

public class TextAreaPrintStream extends PrintStream {

    private JTextArea textArea;



    public TextAreaPrintStream(JTextArea area, OutputStream out) {
        super(out);
        textArea = area;
    }


    public void println(String string) {
        textArea.append(string+"\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.update(textArea.getGraphics());


    }


    public void print(String string) {
        textArea.append(string);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.update(textArea.getGraphics());
    }
}
package Dashboard;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * setup the console of dashboard
 * @author Le Dinh Trung Hieu
 */

public class MessageConsole
{
    private JTextComponent textComponent;
    private Document document;
    private boolean isAppend;
    private DocumentListener limitLinesListener;

    /**
     * constructor
     * @param textComponent
     */
    public MessageConsole(JTextComponent textComponent)
    {
        this(textComponent, true);
    }

    /**
     * default constructor
     * @param textComponent
     * @param isAppend
     */
    public MessageConsole(JTextComponent textComponent, boolean isAppend)
    {
        this.textComponent = textComponent;
        this.document = textComponent.getDocument();
        this.isAppend = isAppend;
        textComponent.setEditable( false );
    }


    public void redirectOut()
    {
        redirectOut(null, null);
    }

    /**
     * set the output stream of text's color printed on text area
     * @param textColor text's color
     * @param printStream stream of lines printed on dashboard
     */
    public void redirectOut(Color textColor, PrintStream printStream)
    {
        ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
        System.setOut( new PrintStream(cos, true) );
    }

    public void redirectErr()
    {
        redirectErr(null, null);
    }

    /**
     * set the error stream of text's color printed on text area
     * @param textColor
     * @param printStream
     */
    public void redirectErr(Color textColor, PrintStream printStream)
    {
        ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
        System.setErr( new PrintStream(cos, true) );
    }

    /**
     * add listener to handle lines printed on dashboard
     * @param lines the lines printed on dashboard
     */
    public void setMessageLines(int lines)
    {
        if (limitLinesListener != null)
            document.removeDocumentListener( limitLinesListener );

        limitLinesListener = new LimitLinesDocumentListener(lines, isAppend);
        document.addDocumentListener( limitLinesListener );
    }


    class ConsoleOutputStream extends ByteArrayOutputStream
    {
        private final String EOL = System.getProperty("line.separator");
        private SimpleAttributeSet attributes;
        private PrintStream printStream;
        private StringBuffer buffer = new StringBuffer(80);
        private boolean isFirstLine;

        /**
         * Specify the option text color and PrintStream
         * @param textColor
         * @param printStream
         */
        public ConsoleOutputStream(Color textColor, PrintStream printStream)
        {
            if (textColor != null)
            {
                attributes = new SimpleAttributeSet();
                StyleConstants.setForeground(attributes, textColor);
            }

            this.printStream = printStream;

            if (isAppend)
                isFirstLine = true;
        }

        /**
         * refresh and clear the output text area
         */
        public void flush()
        {
            String message = toString();

            if (message.length() == 0) return;

            if (isAppend)
                handleAppend(message);
            else
                handleInsert(message);

            reset();
        }

        /**
         * append the new line where the data has to be read in the log file
         * @param message the message after being parsed into predefined Java object
         */
        private void handleAppend(String message)
        {
            if (document.getLength() == 0)
                buffer.setLength(0);

            if (EOL.equals(message))
            {
                buffer.append(message);
            }
            else
            {
                buffer.append(message);
                clearBuffer();
            }

        }

        /**
         * load data to dashboard
         * @param message
         */
        private void handleInsert(String message)
        {
            buffer.append(message);

            if (EOL.equals(message))
            {
                clearBuffer();
            }
        }

        /**
         * clear cache in dashboard
         */
        private void clearBuffer()
        {


            if (isFirstLine && document.getLength() != 0)
            {
                buffer.insert(0, "\n");
            }

            isFirstLine = false;
            String line = buffer.toString();

            try
            {
                if (isAppend)
                {
                    int offset = document.getLength();
                    document.insertString(offset, line, attributes);
                    textComponent.setCaretPosition( document.getLength() );
                }
                else
                {
                    document.insertString(0, line, attributes);
                    textComponent.setCaretPosition( 0 );
                }
            }
            catch (BadLocationException ble) {}

            if (printStream != null)
            {
                printStream.print(line);
            }

            buffer.setLength(0);
        }
    }
}

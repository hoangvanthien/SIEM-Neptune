package Dashboard;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * add listener to handle number of lines printed on dashboard
 * @author Hieu Le
 */
public class LimitLinesDocumentListener implements DocumentListener
{
    private int maximumLines;
    private boolean isRemoveFromStart;

    /**
     * constructor
     * @param maximumLines maximum lines printed on dashboard
     */
    public LimitLinesDocumentListener(int maximumLines)
    {
        this(maximumLines, true);
    }

    /**
     * default constructor
     * @param maximumLines maximum lines printed on dashboard
     * @param isRemoveFromStart true iff the printed oldest line is removed
     */
    public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart)
    {
        setLimitLines(maximumLines);
        this.isRemoveFromStart = isRemoveFromStart;
    }

    /**
     * return the maximum number of lines printed on dashboard
     * @return the value of maximum limited lines
     */
    public int getLimitLines()
    {
        return maximumLines;
    }

    /**
     * limit the printed lines on dashboard
     * @param maximumLines
     */
    public void setLimitLines(int maximumLines)
    {
        if (maximumLines < 1)
        {
            String message = "Maximum lines must be greater than 0";
            throw new IllegalArgumentException(message);
        }

        this.maximumLines = maximumLines;
    }

    /**
     * Gives notification that there was an insert into the document.
     * @param e document event
     */
    public void insertUpdate(final DocumentEvent e)
    {

        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                removeLines(e);
            }
        });
    }

    /**
     * gives notification that a portion of the document has been removed.
     * @param e document event
     */
    public void removeUpdate(DocumentEvent e) {}

    /**
     * gives notification that an attribute or set of attributes changed.
     * @param e the document event
     */
    public void changedUpdate(DocumentEvent e) {}

    /**
     * remove the line when the limit of number of printed lines on dashboard is reached
     * @param e document event
     */
    private void removeLines(DocumentEvent e)
    {

        Document document = e.getDocument();
        Element root = document.getDefaultRootElement();

        while (root.getElementCount() > maximumLines)
        {
            if (isRemoveFromStart)
            {
                removeFromStart(document, root);
            }
            else
            {
                removeFromEnd(document, root);
            }
        }
    }

    /**
     * Remove lines from the start of the Document
     * @param document the whole printed lines on dashboard
     * @param root the first line in dashboard
     */
    private void removeFromStart(Document document, Element root)
    {
        Element line = root.getElement(0);
        int end = line.getEndOffset();

        try
        {
            document.remove(0, end);
        }
        catch(BadLocationException ble)
        {
            System.out.println(ble);
        }
    }

    /**
     * Remove lines from the end of the Document
     * @param document the whole printed lines on dashboard
     * @param root the first line in dashboard
     */
    private void removeFromEnd(Document document, Element root)
    {

        Element line = root.getElement(root.getElementCount() - 1);
        int start = line.getStartOffset();
        int end = line.getEndOffset();

        try
        {
            document.remove(start - 1, end - start);
        }
        catch(BadLocationException ble)
        {
            System.out.println(ble);
        }
    }
}

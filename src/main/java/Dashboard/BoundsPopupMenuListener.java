package Dashboard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;

/**
 * limit the size of drop-down, setup the scroll pane user and combo box
 * @author Hieu Le
 */
public class BoundsPopupMenuListener implements PopupMenuListener
{
    //  class for a horizontal scrollbar can be displayed when necessary
    // the popup can be wider than the combo box
    // the popup can be displayed above the combo box


    private boolean scrollBarRequired = false;
    private boolean popupWider;
    private int maximumWidth = -1;
    private boolean popupAbove;
    private JScrollPane scrollPane;


    public BoundsPopupMenuListener() {
        this(false, false, -1, false);
    }


    public BoundsPopupMenuListener(boolean popupWider, boolean popupAbove) {
        this(false, popupWider, -1, popupAbove);
    }


    public BoundsPopupMenuListener(int maximumWidth) {
        this(false, true, maximumWidth, false);
    }

    /**
     * default constructor
     * @param scrollBarRequired true if the combo box has too many line then the scroll bar will appear
     * @param popupWider true if the length of line greater than the width of combo box
     * @param maximumWidth maximum width of popup window
     * @param popupAbove true if the popup window go up
     */
    public BoundsPopupMenuListener(boolean  scrollBarRequired, boolean popupWider, int maximumWidth, boolean popupAbove) {
        setScrollBarRequired( scrollBarRequired );
        setPopupWider( popupWider );
        setMaximumWidth( maximumWidth );
        setPopupAbove( popupAbove );
    }

    /**
     * get the maximum width of popup window
     * @return the limited width of popup window
     */
    public int getMaximumWidth() {
        return maximumWidth;
    }

    /**
     * set the maximum width of popup window
     * @param maximumWidth the limited width of popup window
     */
    public void setMaximumWidth(int maximumWidth) {
        this.maximumWidth = maximumWidth;
    }

    /**
     * get the popup alove
     * @return true if the popup window go up and vice versa
     */
    public boolean isPopupAbove() {
        return popupAbove;
    }

    /**
     * set the popup above
     * @param popupAbove true if the popup window go up and vice versa
     */
    public void setPopupAbove(boolean popupAbove) {
        this.popupAbove = popupAbove;
    }

    /**
     * get the popup's width
     * @return true if length of line greater than width of combo box and vice versa
     */
    public boolean isPopupWider() {
        return popupWider;
    }

    /**
     * set the popup's width
     * @param popupWider true if length of line greater than width of combo box and vice versa
     */
    public void setPopupWider(boolean popupWider) {
        this.popupWider = popupWider;
    }

    /**
     * get the scroll bar
     * @return true if combo box has too many line
     */

    public boolean isScrollBarRequired() {
        return scrollBarRequired;
    }

    /**
     * set the scroll bar
     * @param scrollBarRequired true if combo box has too many line
     */
    public void setScrollBarRequired(boolean scrollBarRequired) {
        this.scrollBarRequired = scrollBarRequired;
    }

    /**
     * This method is called before the popup menu becomes visible
     * @param e a Popup Menu Event containing the source of the event
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {
        JComboBox comboBox = (JComboBox)e.getSource();

        if (comboBox.getItemCount() == 0) return;

        final Object child = comboBox.getAccessibleContext().getAccessibleChild(0);

        if (child instanceof BasicComboPopup)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    customizePopup((BasicComboPopup)child);
                }
            });
        }
    }

    /**
     * use to customize the scroll pane frame and combo box in dashboard
     * @param popup class object of BasicComboPopup class
     */
    protected void customizePopup(BasicComboPopup popup) {

        scrollPane = getScrollPane(popup);

        if (popupWider)
            popupWider( popup );

        checkHorizontalScrollBar( popup );


        Component comboBox = popup.getInvoker();
        Point location = comboBox.getLocationOnScreen();

        if (popupAbove) {
            int height = popup.getPreferredSize().height;
            popup.setLocation(location.x, location.y - height);
        }
        else {
            int height = comboBox.getPreferredSize().height;
            popup.setLocation(location.x, location.y + height - 1);
            popup.setLocation(location.x, location.y + height);
        }
    }

    /**
     * set the width of popup window to show fully the option in combo box
     * @param popup
     */
    protected void popupWider(BasicComboPopup popup) {

        JList list = popup.getList();

        int popupWidth = list.getPreferredSize().width
                + getScrollBarWidth(popup, scrollPane);

        if (maximumWidth != -1) {
            popupWidth = Math.min(popupWidth, maximumWidth);
        }

        Dimension scrollPaneSize = scrollPane.getPreferredSize();
        popupWidth = Math.max(popupWidth, scrollPaneSize.width);


        scrollPaneSize.width = popupWidth;
        scrollPane.setPreferredSize(scrollPaneSize);
        scrollPane.setMaximumSize(scrollPaneSize);
    }

    /**
     * Use to reset the scrollable view on dashboard every time scroll the bar from left to right and vice versa
     * (hide information was scrolled and show current information)
     *
     * @param popup
     */
    private void checkHorizontalScrollBar(BasicComboPopup popup) {

        //  Reset the viewport to the left

        JViewport viewport = scrollPane.getViewport();
        Point p = viewport.getViewPosition();
        p.x = 0;
        viewport.setViewPosition( p );


        if (! scrollBarRequired) {
            scrollPane.setHorizontalScrollBar( null );
            return;
        }


        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();

        if (horizontal == null) {
            horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
            scrollPane.setHorizontalScrollBar( horizontal );
            scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        }


        if (horizontalScrollBarWillBeVisible(popup, scrollPane)) {
            Dimension scrollPaneSize = scrollPane.getPreferredSize();
            scrollPaneSize.height += horizontal.getPreferredSize().height;
            scrollPane.setPreferredSize(scrollPaneSize);
            scrollPane.setMaximumSize(scrollPaneSize);
            scrollPane.revalidate();
        }
    }

    /**
     * Get the scroll pane frame
     * @param popup
     * @return the predefined scroll pane
     */
    protected JScrollPane getScrollPane(BasicComboPopup popup) {

        JList list = popup.getList();
        Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

        return (JScrollPane)c;
    }

    /**
     * get the predefined scroll bar's width
     * @param popup
     * @param scrollPane instance represent the viewpoint when using scroll bar
     * @return value of scroll bar's width
     */
    protected int getScrollBarWidth(BasicComboPopup popup, JScrollPane scrollPane) {

        int scrollBarWidth = 0;
        JComboBox comboBox = (JComboBox)popup.getInvoker();

        if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            scrollBarWidth = vertical.getPreferredSize().width;
        }

        return scrollBarWidth;
    }

    /**
     * set the horizontal scroll bar to be visible if the printed line is too long
     * @param popup
     * @param scrollPane
     * @return the popup width which dashboard can show the very long line
     */
    protected boolean horizontalScrollBarWillBeVisible(BasicComboPopup popup, JScrollPane scrollPane) {

        JList list = popup.getList();
        int scrollBarWidth = getScrollBarWidth(popup, scrollPane);
        int popupWidth = list.getPreferredSize().width + scrollBarWidth;

        return popupWidth > scrollPane.getPreferredSize().width;
    }
    /**
     * This method is called when the popup menu is canceled
     *
     * @param e a PopupMenuEvent containing the source of the event
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {}
    /**
     * This method is called before the popup menu becomes invisible
     *
     * @param e a PopupMenuEvent containing the source of the event
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

        if (scrollPane != null) {
            scrollPane.setHorizontalScrollBar( null );
        }
    }
}

package Dashboard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;


public class BoundsPopupMenuListener implements PopupMenuListener
{
    //  class for a horizontal scrollbar can be displayed when necessary
    // the popup can be wider than the combo box
    // the popup can be displayed above the combo box


    private boolean scrollBarRequired = true;
    private boolean popupWider;
    private int maximumWidth = -1;
    private boolean popupAbove;
    private JScrollPane scrollPane;


    public BoundsPopupMenuListener() {
        this(true, false, -1, false);
    }


    public BoundsPopupMenuListener(boolean popupWider, boolean popupAbove) {
        this(true, popupWider, -1, popupAbove);
    }


    public BoundsPopupMenuListener(int maximumWidth) {
        this(true, true, maximumWidth, false);
    }


    public BoundsPopupMenuListener(boolean  scrollBarRequired, boolean popupWider, int maximumWidth, boolean popupAbove) {
        setScrollBarRequired( scrollBarRequired );
        setPopupWider( popupWider );
        setMaximumWidth( maximumWidth );
        setPopupAbove( popupAbove );
    }


    public int getMaximumWidth() {
        return maximumWidth;
    }


    public void setMaximumWidth(int maximumWidth) {
        this.maximumWidth = maximumWidth;
    }


    public boolean isPopupAbove() {
        return popupAbove;
    }


    public void setPopupAbove(boolean popupAbove) {
        this.popupAbove = popupAbove;
    }

    public boolean isPopupWider() {
        return popupWider;
    }

    public void setPopupWider(boolean popupWider) {
        this.popupWider = popupWider;
    }


    public boolean isScrollBarRequired() {
        return scrollBarRequired;
    }


    public void setScrollBarRequired(boolean scrollBarRequired) {
        this.scrollBarRequired = scrollBarRequired;
    }


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


    protected JScrollPane getScrollPane(BasicComboPopup popup) {

        JList list = popup.getList();
        Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

        return (JScrollPane)c;
    }


    protected int getScrollBarWidth(BasicComboPopup popup, JScrollPane scrollPane) {

        int scrollBarWidth = 0;
        JComboBox comboBox = (JComboBox)popup.getInvoker();

        if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            scrollBarWidth = vertical.getPreferredSize().width;
        }

        return scrollBarWidth;
    }


    protected boolean horizontalScrollBarWillBeVisible(BasicComboPopup popup, JScrollPane scrollPane) {

        JList list = popup.getList();
        int scrollBarWidth = getScrollBarWidth(popup, scrollPane);
        int popupWidth = list.getPreferredSize().width + scrollBarWidth;

        return popupWidth > scrollPane.getPreferredSize().width;
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {}

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

        if (scrollPane != null) {
            scrollPane.setHorizontalScrollBar( null );
        }
    }
}

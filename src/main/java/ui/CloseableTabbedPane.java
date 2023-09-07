package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.plaf.basic.BasicButtonUI;

import persist.Constants;
import persist.PersistedOpenFileList;

/**Simple class for closing a JTabbedPane by painting an X over the end 
 * and adding an action listener to it. Parent clas extends JTabbedPane but offers a
 * basic close button via BasicButtonUI
 * @author echic
 *
 */
public class CloseableTabbedPane extends JTabbedPane {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseableTabbedPane() {
        super();
    }

    private class TabCloseButton extends JButton {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TabCloseButton(final Component component) {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText(Constants.LABEL_CLOSE_TAB);
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            setRolloverEnabled(true);
            setOpaque(false);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tabIndex = indexOfComponent(component);
                    if (tabIndex != -1) {
                        removeTabAt(tabIndex);
                        PersistedOpenFileList.removeFromMap(tabIndex);
                    }
                }
            });
        }

        @Override
        public void updateUI() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    @Override
    public void addTab(String title, final Component component) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel label = new JLabel(title);
        tabPanel.add(label);

        TabCloseButton closeButton = new TabCloseButton(component);
        tabPanel.add(closeButton);

        super.addTab(null, component);
        setTabComponentAt(getTabCount() - 1, tabPanel);
    }
}

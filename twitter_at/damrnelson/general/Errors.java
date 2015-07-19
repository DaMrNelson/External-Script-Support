package twitter_at.damrnelson.general;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Errors {

	public static void error(String message) {
		error(message, "No trace");
	}
	
	public static void error(String message, Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		error(message, sw.getBuffer().toString());
	}
	
	public static void error(String message, String exception) {
		JDialog dialog = new JDialog();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		
		JPanel panel = (JPanel) dialog.getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		JLabel errorLabel = new JLabel(message);
		errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorLabel.setForeground(Color.BLACK);
		panel.add(errorLabel);
		
		panel.add(Box.createVerticalStrut(4));
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.LINE_AXIS));
		
		JLabel preEmailLabel = new JLabel("Please email the following to ");
		preEmailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		emailPanel.add(preEmailLabel);
		
		JTextField myEmail = new JTextField(" damrnelson@gmail.com");
		myEmail.setMinimumSize(myEmail.getPreferredSize());
		myEmail.setMaximumSize(myEmail.getPreferredSize());
		myEmail.setEditable(false);
		emailPanel.add(myEmail);
		
		JLabel postEmailLabel = new JLabel(" for assistance.");
		postEmailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		emailPanel.add(postEmailLabel);
		
		panel.add(emailPanel);
		
		JTextArea trace = new JTextArea(exception);
		trace.setEditable(false);
		
		JScrollPane scroller = new JScrollPane();
		scroller.setAlignmentX(Component.CENTER_ALIGNMENT);
		scroller.setMinimumSize(new Dimension(200, 100));
		scroller.setPreferredSize(new Dimension(700, 300));
		scroller.setMaximumSize(new Dimension(3000, 300));
		scroller.setViewportView(trace);
		panel.add(scroller);
		
		dialog.setResizable(false);
		dialog.setLocation(new Point(20, 20));
		dialog.pack();
		dialog.setVisible(true);
	}
	
}

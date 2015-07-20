package twitter_at.damrnelson.roblox.external_script_support;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import twitter_at.damrnelson.general.Errors;

public class Launcher {
	
	public static Server server;
	public static MainWindow window;

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set look and feel.");
			e.printStackTrace(System.err);
		}
		
		try {
			Files.init();
			Images.init();
		} catch (IOException e) {
			Errors.error("Unable to load. Program will exit.", e);
			System.exit(0);
		}
		
		window = new MainWindow();
		
		if (SystemTray.isSupported()) {
			try {
				window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				
				final PopupMenu popup = new PopupMenu();
				SystemTray tray = SystemTray.getSystemTray();
				
				TrayIcon icon = new TrayIcon(Images.scriptIcons[2].getScaledInstance(new TrayIcon(Images.scriptIcons[2]).getSize().width, -1, Image.SCALE_SMOOTH));
				icon.setToolTip("External Script Support");
				icon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							window.setVisible(true);
							window.requestFocus();
						}
					}
				});
				
				MenuItem open = new MenuItem("Open");
				open.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						window.setVisible(true);
						window.requestFocus();
					}
				});
				popup.add(open);
				
				MenuItem exit = new MenuItem("Exit");
				exit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							server.stop();
						} catch (Exception err) { // IOException / NullPointerException
							err.printStackTrace();
						}
						
						System.exit(0);
					}
				});
				popup.add(exit);
				
				icon.setPopupMenu(popup);
				tray.add(icon);
			} catch (AWTException e) {
				Errors.error("Unable to create system tray.", e);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		}
		
		try {
			server = new Server();
			server.start();
		} catch (IOException e) {
			window.setVisible(false);
			Errors.error("Unable to start sharing server. Check your system tray to see if the program is already running.", e);
			System.exit(0);
		}
	}
	
}

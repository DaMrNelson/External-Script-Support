package twitter_at.damrnelson.roblox.external_script_support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.oracle.SpringUtilities;

public class MainWindow extends JFrame {
	
	public String[] infoLabels = {"File ID", "File Name", "File Path"};
	public JLabel[] iLabels = new JLabel[4]; // 3 + 1 for error label
	public JTextField[] iBoxes = new JTextField[3];
	
	public String[] inputLabels = {"ID", "File"};
	
	public JList<String> selectionList;

	public MainWindow() {
		super("ROBLOX External Script Support");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImages(Arrays.asList(Images.scriptIcons));
		
		JPanel panel = (JPanel) getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		////////////////////////
		//////// HEADER ////////
		////////////////////////
		
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
		panel.add(header);
		
		JButton create = new JButton(" Add", new ImageIcon(Images.addIcon));
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCreateDialog();
			}
		});
		header.add(create);
		JButton remove = new JButton(" Remove", new ImageIcon(Images.removeIcon));
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = selectionList.getSelectedIndex();
				
				if (index >= 0) {
					Files.scriptList.remove(index);
					Files.saveScripts();
					updateScriptList();
				}
			}
		});
		header.add(remove);
		
		header.add(Box.createHorizontalGlue());
		
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
		header.add(info);
		
		JLabel label = new JLabel(" External Script Support V1.0  ");
		label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		info.add(label);
		label = new JLabel(" By DaMrNelson  ");
		label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		info.add(label);
		
		////////////////////////
		//////// SELECT ////////
		////////////////////////
		
		selectionList = new JList<String>();
		selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (selectionList.getSelectedIndex() == -1) {
						clearSelectionInfo();
					} else {
						setSelectionInfo(selectionList.getSelectedIndex());
					}
				}
			}
		});
		
		JScrollPane sScroll = new JScrollPane(selectionList);
		sScroll.setMinimumSize(new Dimension(10, 10));
		sScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		panel.add(sScroll);
		
		////////////////////////
		///////// INFO /////////
		////////////////////////
		
		info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
		panel.add(info);
		
		JPanel infoEditable = new JPanel();
		infoEditable.setLayout(new SpringLayout());
		info.add(infoEditable);
		
		for (int i = 0; i < infoLabels.length; i++) {
			JLabel l = new JLabel(infoLabels[i] + ": ", JLabel.TRAILING);
			infoEditable.add(l);
			
			JTextField t = new JTextField(20);
			t.setMaximumSize(t.getPreferredSize());
			t.setEditable(false);
			l.setLabelFor(t);
			infoEditable.add(t);
			
			iLabels[i] = l;
			iBoxes[i] = t;
		}
		
		SpringUtilities.makeCompactGrid(infoEditable, infoLabels.length, 2, 6, 6, 6, 6);
		
		JPanel infoButtons = new JPanel();
		infoButtons.setLayout(new BoxLayout(infoButtons, BoxLayout.LINE_AXIS));
		info.add(infoButtons);
		
		final JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tempId = iBoxes[0].getText().toLowerCase();
				
				if (tempId.contains("*")) {
					iLabels[3].setText("ID cannot contain '*'.");
					iLabels[3].setVisible(true);
					return;
				} else if (tempId.equals("waiting-page")) {
					iLabels[3].setText("ID cannot be 'waiting-page'.");
					iLabels[3].setVisible(true);
					return;
				}
				
				boolean matched = false;
				
				for (ScriptInfo inf : Files.scriptList) {
					if (inf.id.toLowerCase().equals(tempId)) {
						matched = true;
						break;
					}
				}
				
				if (matched) {
					iLabels[3].setText("ID already exists.");
					iLabels[3].setVisible(true);
				} else {
					int id = selectionList.getSelectedIndex();
					Files.scriptList.get(selectionList.getSelectedIndex()).id = iBoxes[0].getText();
					Files.saveScripts();
					updateScriptList();
					selectionList.setSelectedIndex(id);
				}
			}
		});
		infoButtons.add(apply);
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Files.scriptList.remove(selectionList.getSelectedIndex());
				Files.saveScripts();
				updateScriptList();
			}
		});
		infoButtons.add(delete);
		
		JLabel errorLabel = new JLabel("Error");
		errorLabel.setVisible(false);
		errorLabel.setForeground(Color.RED);
		iLabels[3] = errorLabel;
		infoButtons.add(errorLabel);
		
		////////////////////////
		//////// DISPLAY ///////
		////////////////////////
		
		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(600, 400));
		setLocation(new Point(100, 100));
		
		updateScriptList();
		
		setVisible(true);
	}
	
	public void updateScriptList() {
		String[] data = new String[Files.scriptList.size()];
		
		for (int i = 0; i < Files.scriptList.size(); i++) {
			data[i] = Files.scriptList.get(i).getListString();
		}
		
		selectionList.setListData(data);
		revalidate();
	}
	
	public void clearSelectionInfo() {
		for (int i = 0; i < infoLabels.length; i++) {
			iBoxes[i].setText("");
			iBoxes[i].setEditable(false);
		}
	}
	
	public void setSelectionInfo(int index) {
		ScriptInfo info = Files.scriptList.get(index);
		
		for (int i = 0; i < infoLabels.length; i++) {
			iBoxes[i].setEditable(i < 1);
			
			switch (i) {
				case 0:
					iBoxes[i].setText(info.id);
					break;
					
				case 1:
					iBoxes[i].setText(info.script.getName());
					break;
					
				case 2:
					iBoxes[i].setText(info.script.getAbsolutePath());
					break;
			}
		}
	}
	
	public void showCreateDialog() {
		final JDialog dialog = new JDialog(Launcher.window);
		dialog.setTitle("Add Script");
		dialog.setResizable(false);
		
		JPanel panel = (JPanel) dialog.getContentPane();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JPanel inputs = new JPanel();
		inputs.setLayout(new SpringLayout());
		panel.add(inputs);
		
		final File[] file = new File[1]; // So we can modify the file from different threads
		final JTextField[] fields = new JTextField[2];
		
		for (int i = 0; i < inputLabels.length; i++) {
			JLabel l = new JLabel(inputLabels[i] + ": ", JLabel.TRAILING);
			inputs.add(l);
			
			JTextField t = new JTextField(20);
			t.setMaximumSize(t.getPreferredSize());
			t.setEditable(i == 0);
			l.setLabelFor(t);
			inputs.add(t);
			
			
			if (i == 1) {
				t.setText("Click to select a file.");
				
				t.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(new FileNameExtensionFilter("Lua Files", "lua"));
							
							if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
								file[0] = chooser.getSelectedFile();
								fields[1].setText(file[0].getAbsolutePath());	
							} else {
								file[0] = null;
								fields[1].setText("Click to select a file.");
							}
						}
					}
				});
			}
			
			fields[i] = t;
		}
		
		SpringUtilities.makeCompactGrid(inputs, inputLabels.length, 2, 6, 6, 6, 6);
		
		final JLabel errorLabel = new JLabel("Error");
		errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		errorLabel.setVisible(false);
		errorLabel.setForeground(Color.RED);
		// Added later down
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		panel.add(buttons);
		
		final JButton apply = new JButton("Add");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = fields[0].getText();
				
				if (id.isEmpty()) {
					errorLabel.setVisible(true);
					errorLabel.setText("No ID entered.");
					dialog.pack();
					// TODO: Display error
				} else if (id.contains("*")) {
					errorLabel.setVisible(true);
					errorLabel.setText("ID cannot contain '*'.");
					dialog.pack();
				} else if (id.toLowerCase().equals("waiting-page")) {
					errorLabel.setVisible(true);
					errorLabel.setText("ID cannot by 'waiting-page'.");
					dialog.pack();
				} else {
					String tempId = id.toLowerCase();
					boolean matched = false;
					
					for (ScriptInfo inf : Files.scriptList) {
						if (inf.id.toLowerCase().equals(tempId)) {
							matched = true;
							break;
						}
					}
					
					if (matched) {
						errorLabel.setVisible(true);
						errorLabel.setText("ID already exists.");
						dialog.pack();
					} else {
						if (file[0] == null) {
							errorLabel.setVisible(true);
							errorLabel.setText("No file selected.");
							dialog.pack();							
						} else {
							ScriptInfo info = new ScriptInfo(file[0], id);
							Files.scriptList.add(info);
							Files.saveScripts(); // TODO: Make sure this method is implemented
							
							updateScriptList();
							selectionList.setSelectedIndex(Files.scriptList.size() - 1);
							setSelectionInfo(selectionList.getSelectedIndex());
							
							dialog.dispose();
						}
					}
				}
			}
		});
		buttons.add(apply);
		
		JButton delete = new JButton("Cancel");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		buttons.add(delete);
		
		dialog.add(Box.createVerticalStrut(4));
		dialog.add(errorLabel);
		
		dialog.pack();
		dialog.setVisible(true);
	}
	
}

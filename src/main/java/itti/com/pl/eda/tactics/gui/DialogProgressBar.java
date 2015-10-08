package itti.com.pl.eda.tactics.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DialogProgressBar extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textAreaProgressBar = new JTextArea("maslo maslane");

	public DialogProgressBar(){
		super();
		setTitle("Progress bar");
		JPanel rootPanel = new JPanel();
		this.setLayout(new GridLayout(1,1));
		textAreaProgressBar.setEditable(false);
		this.setSize(new Dimension(300, 60));
		this.setLocation(((GuiUtils.getScreenWidth() - 300) /2), ((GuiUtils.getScreenHeight() - 60) /2));
		this.getContentPane().add(rootPanel);
		rootPanel.setLayout(new GridLayout(1,1));
		rootPanel.add(textAreaProgressBar);
		setProgress(0);
	}

	public void setProgress(int progress){
		setTitle("Progress: " + progress + "/100");
		textAreaProgressBar.setText("Progress: " + progress + "/100");
		textAreaProgressBar.invalidate();
		textAreaProgressBar.repaint();
	}

}

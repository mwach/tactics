package itti.com.pl.eda.tactics.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 * wrapper for default java ModalDialog
 * used to display messages on the screen
 * @author marcin
 *
 */
public class ModalDialog {

	private static JDialog errDialog = null;
	private static JTextArea textArea = new JTextArea();
	private static JButton buttonOk = null;

	private static final int WIDTH = 400;
	private static final int HEIGHT = 200;


	/**
	 * displays message on the screen
	 * @param title title of the dialog
	 * @param data displayed text message
	 * @param critical flag indicates, if message is critical or not
	 */
	public static void setMessage(String title, Collection<String> data,
			boolean critical) {

		if(errDialog == null){
			initGui();
		}

		if(title != null){
			errDialog.setTitle(title);
		}

		textArea.setText("");

		if(data != null){
			for (String item : data) {
				textArea.append(item + "\n");	
			}
		}

		buttonOk.requestFocus();
		errDialog.setVisible(true);
	}


	private static void initGui(){

		errDialog = new JDialog();
		errDialog.setModal(true);

		errDialog.setLayout(null);
		errDialog.setSize(new Dimension(WIDTH, HEIGHT));

		int screenWidth = GuiUtils.getScreenWidth();
		int screenHeight = GuiUtils.getScreenHeight();

		errDialog.setLocation((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setSize(WIDTH, HEIGHT - 60);
		textArea.setLocation(0, 0);
		errDialog.getContentPane().add(textArea);

		buttonOk = new JButton("OK");
		buttonOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				errDialog.setVisible(false);
			}
		});
		buttonOk.setSize(60, 20);
		buttonOk.setLocation((WIDTH - 60) / 2, HEIGHT - 60);
		errDialog.getContentPane().add(buttonOk);
	}
}

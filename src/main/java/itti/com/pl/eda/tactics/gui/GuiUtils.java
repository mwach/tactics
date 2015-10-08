package itti.com.pl.eda.tactics.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * some helpful methods used during working with GUI
 * @author marcin
 *
 */
public class GuiUtils {

	private static final Dimension dimensionDefault = new Dimension(200, 20);
	private static final Dimension dimensionDuble = new Dimension(410, 20);
	private static final Dimension dimensionButton = new Dimension(160, 20);
	private static final Dimension dimensionList = new Dimension(200, 80);

	private static final Font fontLabel = new Font("arial", Font.BOLD, 12);
	private static final Font fontButton = new Font("arial", Font.BOLD, 12);
	private static final Font fontComboBox = new Font("arial", Font.BOLD, 12);

	private static int[] ROWS = null;

	private static final int COLUMN_LABELS = 20;
	private static final int COLUMN_BOXES = 200;
	private static final int COLUMN_BUTTONS = 450;


	/**
	 * sets number of rows of the frame
	 * @param rows
	 */
	public static void setRows(int[] rows){
		ROWS = rows;
	}


	/**
	 * fills one row on frame with specified controls
	 * @param row number of the row on frame
	 * @param labelDesc text on the label control (left control by default)
	 * @param box component in the middle of the row
	 * @param button reference to the button (right control by default)
	 * @param panel reference to the parent panel
	 */
	public static synchronized void fillRow(int row, String labelDesc, JComponent box, JButton button, Container panel){

		fillRow(row, new JLabel(labelDesc), box, button, panel);
	}

	public static synchronized void fillRow(int row, String labelDesc, JComponent[] boxes, JButton button, Container panel){

		fillRow(row, new JLabel(labelDesc), boxes, button, panel);
	}

	/**
	 * fills one row on frame with specified controls
	 * @param row number of the row on frame
	 * @param leftComp reference to the left control
	 * @param middleComp reference to the middle control
	 * @param rightComp reference to the right control
	 * @param panel reference to the parent control
	 */
	public static synchronized void fillRow(int row, JComponent leftComp, JComponent middleComp, JComponent rightComp, Container panel){

		if(leftComp != null){
			if(leftComp instanceof JButton){
				leftComp.setSize(dimensionButton);
				leftComp.setFont(fontButton);
			}else{
				leftComp.setSize(dimensionDefault);
				leftComp.setFont(fontLabel);
				leftComp.setBackground(Color.white);
			}
			leftComp.setLocation(COLUMN_LABELS, ROWS[row]);
			panel.add(leftComp);
		}

		if(middleComp != null){
			if(middleComp instanceof JButton){
				middleComp.setSize(dimensionButton);
				middleComp.setFont(fontButton);
				middleComp.setLocation(COLUMN_BOXES + (COLUMN_BOXES - (int)dimensionButton.getWidth()) / 2, ROWS[row]);
			}else{
				middleComp.setSize(dimensionDefault);
				middleComp.setFont(fontComboBox);
				middleComp.setBackground(Color.white);
				middleComp.setLocation(COLUMN_BOXES, ROWS[row]);
			}
			panel.add(middleComp);
		}

		if(rightComp != null){
			rightComp.setSize(dimensionButton);
			rightComp.setFont(fontButton);
			rightComp.setLocation(COLUMN_BUTTONS, ROWS[row]);
			panel.add(rightComp);
		}
	}

	/**
	 * fills one row on frame with specified controls
	 * @param row number of the row on frame
	 * @param leftComp reference to the left control
	 * @param middleComp reference to the middle control
	 * @param rightComp reference to the right control
	 * @param panel reference to the parent control
	 */
	public static synchronized void fillRow(int row, int rowCount, String text, JComponent middleComp, Container panel){

		if(text != null){
			JLabel label = new JLabel(text);
			label.setSize(dimensionDefault);
			label.setFont(fontLabel);
			label.setBackground(Color.white);
			label.setLocation(COLUMN_LABELS, ROWS[row]);
			panel.add(label);
		}

		if(middleComp != null){
			middleComp.setSize(dimensionDuble.width, dimensionDuble.height * rowCount);
			middleComp.setFont(fontComboBox);
			middleComp.setBackground(Color.white);
			middleComp.setLocation(COLUMN_BOXES, ROWS[row]);
			panel.add(middleComp);
		}
	}

	public static synchronized void fillRow(int row, JComponent leftComp, JComponent[] middleComps, JComponent rightComp, Container panel){

		if(leftComp != null){
			if(leftComp instanceof JButton){
				leftComp.setSize(dimensionButton);
				leftComp.setFont(fontButton);
			}else{
				leftComp.setSize(dimensionDefault);
				leftComp.setFont(fontLabel);
				leftComp.setBackground(Color.white);
			}
			leftComp.setLocation(COLUMN_LABELS, ROWS[row]);
			panel.add(leftComp);
		}

		if(middleComps != null){
			int width = dimensionDefault.width/middleComps.length;
			int i=0;
			for (JComponent component : middleComps) {
				component.setSize(width, dimensionDefault.height);
				component.setFont(fontComboBox);
				component.setBackground(Color.white);
				component.setLocation(COLUMN_BOXES + (i++)*width, ROWS[row]);
				panel.add(component);
			}
		}

		if(rightComp != null){
			rightComp.setSize(dimensionButton);
			rightComp.setFont(fontButton);
			rightComp.setLocation(COLUMN_BUTTONS, ROWS[row]);
			panel.add(rightComp);
		}
	}

	/**
	 * fills one row on frame with specified controls
	 * @param row number of the row on frame
	 * @param labelDesc text on the label (left control by default)
	 * @param middleComp control in the middle of the row
	 * @param buttons arrays of buttons (located on the right side of the panel)
	 * @param panel reference to the parent panel
	 */
	public static synchronized void fillRow(int row, String labelDesc, JComponent middleComp, JButton[] buttons, JComponent panel) {

		if(labelDesc != null){
			JLabel label = new JLabel(labelDesc);
			label.setSize(dimensionDefault);
			label.setFont(fontLabel);
			label.setLocation(COLUMN_LABELS, ROWS[row]);
			panel.add(label);
		}

		if(middleComp != null){
			middleComp.setSize(dimensionList);
			middleComp.setFont(fontComboBox);
			middleComp.setLocation(COLUMN_BOXES, ROWS[row]);
			panel.add(middleComp);
		}

		if(buttons != null){

			for(int btnNr = 0 ; btnNr < buttons.length ; btnNr++){
				buttons[btnNr].setSize(dimensionButton);
				buttons[btnNr].setFont(fontButton);
				buttons[btnNr].setLocation(COLUMN_BUTTONS, ROWS[row + btnNr]);
				panel.add(buttons[btnNr]);
			}
		}
	}


	/**
	 * returns screen width in pixels
	 * @return width
	 */
	public static int getScreenWidth(){
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}

	/**
	 * returns screen height in pixels
	 * @return height
	 */
	public static int getScreenHeight(){
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	}


	/**
	 * prepares comboBox control
	 * @param comboBox reference to the control
	 * @param size size of the control
	 * @param min minimum value of the comboBox item 
	 * @param max maximum value of the comboBox item
	 * @param list list of comboBox items
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void prepareComboBox(JComboBox comboBox, Dimension size, int min, int max,
			int[] list) {

		comboBox.setLayout(null);
		comboBox.setSize(size);
		comboBox.setFont(fontComboBox);
		comboBox.removeAllItems();
		for(; min<=max ; min++){
			comboBox.addItem(min);
		}

		if(list != null){
			for(int i=0 ; i<list.length ; i++){
				comboBox.addItem(list[i]);
			}
		}
	}


	/**
	 * prepares comboBox control
	 * @param comboBox reference to the control
	 * @param size size of the control
	 * @param list list of comboBox items
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void prepareComboBox(JComboBox comboBox,
			Dimension size, List<String> list) {

		comboBox.setLayout(null);
		comboBox.setSize(size);
		comboBox.setFont(fontComboBox);
		comboBox.removeAllItems();

		if(list != null){
			for (String item : list) {
				comboBox.addItem(item);
			}
		}
	}
}
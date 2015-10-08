package itti.com.pl.eda.tactics.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import itti.com.pl.eda.tactics.policy.DateUnit;
import itti.com.pl.eda.tactics.policy.TimeCondition;
import itti.com.pl.eda.tactics.policy.TimeCondition.Period;


/**
 * class derives from JDialog
 * used to construct modal dialog displaying policy time conditions
 * @author marcin
 *
 */
public class DialogTimeConditions extends JDialog{

	private static final long serialVersionUID = 1L;

	private IFormDataExchangeIntegrace requesterForm = null;

	private int desktopWidth = -1;
	private int desktopHeight = -1;

	private final int panelWidth = 600;
	private final int panelHeight = 400;
	private final int buttonWidth = 100;
	private final int buttonHeight = 20;

	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("Cancel");
	private JPanel panelButtons = new JPanel();

	private JComboBox<Integer> comboBoxStartYear = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxStartMonth = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxStartDay = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxStartHour = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxStartMinute = new JComboBox<Integer>();
	private JPanel panelStartDate = new JPanel();

	private JComboBox<Integer> comboBoxEndYear = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxEndMonth = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxEndDay = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxEndHour = new JComboBox<Integer>();
	private JComboBox<Integer> comboBoxEndMinute = new JComboBox<Integer>();
	private JPanel panelEndDate = new JPanel();

	private JComboBox<Integer> comboBoxPeriod = new JComboBox<Integer>();
	private JPanel panelPeriod = new JPanel();

	private List<String> periodsList = null;


	/**
	 * default constructor
	 * @param owner JFrame owner of this modal dialog
	 * @param parentForm events listener - all events from this object will be sent to the listener
	 */
	public DialogTimeConditions(JFrame owner, IFormDataExchangeIntegrace parentForm){

		super(owner, "Policy time conditions", true);

		requesterForm = parentForm;

		setModal(true);

		desktopWidth = GuiUtils.getScreenWidth();
		desktopHeight = GuiUtils.getScreenHeight();

		this.setLayout(new GridLayout(4, 1));

		setSize(panelWidth, panelHeight);
		setLocation((desktopWidth - panelWidth)/ 2, (desktopHeight - panelHeight)/ 2);

		Dimension comboBoxSize = new Dimension(100, 50);
		int cbWidth = comboBoxSize.width;
		int cbHeight = comboBoxSize.height;

		int defMiddlePos = (panelHeight / 4 - cbHeight) / 2;
		int defMarginWidth = (panelWidth - 5 * cbWidth) / 6;

		int currYear = Calendar.getInstance().get(Calendar.YEAR);

		GuiUtils.prepareComboBox(comboBoxStartYear, comboBoxSize, currYear, currYear + 50, new int[]{-1});
		comboBoxStartYear.setBorder(BorderFactory.createTitledBorder("Year"));
		comboBoxStartYear.setLocation(defMarginWidth, defMiddlePos);
		panelStartDate.add(comboBoxStartYear);

		GuiUtils.prepareComboBox(comboBoxStartMonth, comboBoxSize, 1, 12, new int[]{-1});
		comboBoxStartMonth.setBorder(BorderFactory.createTitledBorder("Month"));
		comboBoxStartMonth.setLocation(defMarginWidth * 2 + cbWidth, defMiddlePos);
		panelStartDate.add(comboBoxStartMonth);

		GuiUtils.prepareComboBox(comboBoxStartDay, comboBoxSize, 1, 31, new int[]{-1});
		comboBoxStartDay.setBorder(BorderFactory.createTitledBorder("Day"));
		comboBoxStartDay.setLocation(defMarginWidth * 3 + 2 * cbWidth, defMiddlePos);
		panelStartDate.add(comboBoxStartDay);

		GuiUtils.prepareComboBox(comboBoxStartHour, comboBoxSize, 0, 23, new int[]{-1});
		comboBoxStartHour.setBorder(BorderFactory.createTitledBorder("Hour"));
		comboBoxStartHour.setLocation(defMarginWidth * 4 + 3 * cbWidth, defMiddlePos);
		panelStartDate.add(comboBoxStartHour);

		GuiUtils.prepareComboBox(comboBoxStartMinute, comboBoxSize, 0, 59, new int[]{-1});
		comboBoxStartMinute.setBorder(BorderFactory.createTitledBorder("Minute"));
		comboBoxStartMinute.setLocation(defMarginWidth * 5 + 4 * cbWidth, defMiddlePos);
		panelStartDate.add(comboBoxStartMinute);


		GuiUtils.prepareComboBox(comboBoxEndYear, comboBoxSize, currYear, currYear + 50, new int[]{-1});
		comboBoxEndYear.setBorder(BorderFactory.createTitledBorder("Year"));
		comboBoxEndYear.setLocation(defMarginWidth, defMiddlePos);
		panelEndDate.add(comboBoxEndYear);

		GuiUtils.prepareComboBox(comboBoxEndMonth, comboBoxSize, 1, 12, new int[]{-1});
		comboBoxEndMonth.setBorder(BorderFactory.createTitledBorder("Month"));
		comboBoxEndMonth.setLocation(defMarginWidth * 2 + cbWidth, defMiddlePos);
		panelEndDate.add(comboBoxEndMonth);

		GuiUtils.prepareComboBox(comboBoxEndDay, comboBoxSize, 1, 31, new int[]{-1});
		comboBoxEndDay.setBorder(BorderFactory.createTitledBorder("Day"));
		comboBoxEndDay.setLocation(defMarginWidth * 3 + 2 * cbWidth, defMiddlePos);
		panelEndDate.add(comboBoxEndDay);

		GuiUtils.prepareComboBox(comboBoxEndHour, comboBoxSize, 0, 23, new int[]{-1});
		comboBoxEndHour.setBorder(BorderFactory.createTitledBorder("Hour"));
		comboBoxEndHour.setLocation(defMarginWidth * 4 + 3 * cbWidth, defMiddlePos);
		panelEndDate.add(comboBoxEndHour);

		GuiUtils.prepareComboBox(comboBoxEndMinute, comboBoxSize, 0, 59, new int[]{-1});
		comboBoxEndMinute.setBorder(BorderFactory.createTitledBorder("Minute"));
		comboBoxEndMinute.setLocation(defMarginWidth * 5 + 4 * cbWidth, defMiddlePos);
		panelEndDate.add(comboBoxEndMinute);

		periodsList = Period.getList();

		GuiUtils.prepareComboBox(comboBoxPeriod, comboBoxSize, periodsList);
		comboBoxPeriod.setBorder(BorderFactory.createTitledBorder("Period"));
		comboBoxPeriod.setLocation(defMarginWidth * 3 + 2 * cbWidth, defMiddlePos);
		comboBoxPeriod.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {

				if(comboBoxPeriod.getSelectedItem() != null){
					Period period = Period.getValue(comboBoxPeriod.getSelectedItem().toString());
					updateForm(period);
				}
			}

		});
		panelPeriod.add(comboBoxPeriod);


		int buttonMarginWidth = (panelWidth - 2 * buttonWidth) / 3;

		buttonOk.setSize(buttonWidth, buttonHeight);
		int widthButtonOk = buttonMarginWidth;
		buttonOk.setLocation(widthButtonOk , defMiddlePos);
		panelButtons.add(buttonOk);

		buttonCancel.setSize(buttonWidth, buttonHeight);
		int widthButtonCancel = 2 * buttonMarginWidth + buttonWidth;
		buttonCancel.setLocation(widthButtonCancel, defMiddlePos);
		panelButtons.add(buttonCancel);


		panelPeriod.setLayout(null);
		panelPeriod.setBorder(BorderFactory.createTitledBorder("Period"));
		this.getContentPane().add(panelPeriod);

		panelStartDate.setLayout(null);
		panelStartDate.setBorder(BorderFactory.createTitledBorder("Start date"));
		this.getContentPane().add(panelStartDate);

		panelEndDate.setLayout(null);
		panelEndDate.setBorder(BorderFactory.createTitledBorder("End date"));
		this.getContentPane().add(panelEndDate);

		panelButtons.setLayout(null);
		panelButtons.setSize(panelWidth, buttonHeight);
		this.getContentPane().add(panelButtons);


		addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {}

			public void windowClosed(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				dispose();
			}

			public void windowDeactivated(WindowEvent arg0) {}

			public void windowDeiconified(WindowEvent arg0) {}

			public void windowIconified(WindowEvent arg0) {}

			public void windowOpened(WindowEvent arg0) {}
			
		});

		buttonOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(prepareCondition()){
					dispose();
				}
			}
		});

		buttonCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}


	private void updateForm(Period period) {

		if(period != null){
			if(period == Period.Once){
				setComboBoxesState(true);
			}else if(period == Period.EveryYear){
				setComboBoxesState(true);
				comboBoxStartYear.setEnabled(false);
				comboBoxEndYear.setEnabled(false);
			}else if(period == Period.EveryMonth){
				setComboBoxesState(true);
				comboBoxStartYear.setEnabled(false);
				comboBoxEndYear.setEnabled(false);
				comboBoxStartMonth.setEnabled(false);
				comboBoxEndMonth.setEnabled(false);
			}else if(period == Period.EveryDay){
				setComboBoxesState(true);
				comboBoxStartYear.setEnabled(false);
				comboBoxEndYear.setEnabled(false);
				comboBoxStartMonth.setEnabled(false);
				comboBoxEndMonth.setEnabled(false);
				comboBoxStartDay.setEnabled(false);
				comboBoxEndDay.setEnabled(false);
			}else if(period == Period.EveryHour){
				setComboBoxesState(true);
				comboBoxStartYear.setEnabled(false);
				comboBoxEndYear.setEnabled(false);
				comboBoxStartMonth.setEnabled(false);
				comboBoxEndMonth.setEnabled(false);
				comboBoxStartDay.setEnabled(false);
				comboBoxEndDay.setEnabled(false);
				comboBoxStartHour.setEnabled(false);
				comboBoxEndHour.setEnabled(false);
			}
		}
	}

	private void setComboBoxesState(boolean state){
		comboBoxStartYear.setEnabled(state);
		comboBoxStartMonth.setEnabled(state);
		comboBoxStartDay.setEnabled(state);
		comboBoxStartHour.setEnabled(state);
		comboBoxStartMinute.setEnabled(state);
		comboBoxEndYear.setEnabled(state);
		comboBoxEndMonth.setEnabled(state);
		comboBoxEndDay.setEnabled(state);
		comboBoxEndHour.setEnabled(state);
		comboBoxEndMinute.setEnabled(state);
	}

	private boolean prepareCondition() {

		int startYear = (comboBoxStartYear.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxStartYear.getSelectedItem().toString()) : -1);
		int startMonth = (comboBoxStartMonth.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxStartMonth.getSelectedItem().toString()) : -1);
		int startDay = (comboBoxStartDay.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxStartDay.getSelectedItem().toString()) : -1);
		int startHour = (comboBoxStartHour.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxStartHour.getSelectedItem().toString()) : -1);
		int startMinute = (comboBoxStartMinute.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxStartMinute.getSelectedItem().toString()) : -1);

		DateUnit startUnit = new DateUnit(startYear, startMonth, startDay, startHour, startMinute);


		int endYear = (comboBoxEndYear.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxEndYear.getSelectedItem().toString()) : -1);
		int endMonth = (comboBoxEndMonth.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxEndMonth.getSelectedItem().toString()) : -1);
		int endDay = (comboBoxEndDay.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxEndDay.getSelectedItem().toString()) : -1);
		int endHour = (comboBoxEndHour.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxEndHour.getSelectedItem().toString()) : -1);
		int endMinute = (comboBoxEndMinute.getSelectedItem() != null ? 
				Integer.parseInt(comboBoxEndMinute.getSelectedItem().toString()) : -1);

		DateUnit endUnit = new DateUnit(endYear, endMonth, endDay, endHour, endMinute);

		Period period = comboBoxPeriod.getSelectedItem() != null ?
				Period.getValue(comboBoxPeriod.getSelectedItem().toString()) : null;

		boolean result = false;
		try{
			TimeCondition tc = new TimeCondition(startUnit, endUnit, period);
			requesterForm.processTimeCondition(tc);
			result = true;
		}catch (Exception e) {
			List<String> err = new ArrayList<String>();
			err.add(e.toString());
			if(startUnit != null){
				err.add("Start: " + startUnit.toString());
			}
			if(endUnit != null){
				err.add("End: " + endUnit.toString());
			}
			if(period != null){
				err.add("Period: " + period.toString());
			}
			ModalDialog.setMessage("Time condition is invalid", err, false);
		}
		return result;
	}
}

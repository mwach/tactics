package itti.com.pl.eda.tactics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * wrapper for JDialog component
 * used to construct GUI dialogs
 * @author marcin
 *
 */
public class DialogActCond extends JDialog{

	private static final long serialVersionUID = 1L;


	/**
	 * data type defined for dialog
	 * @author marcin
	 *
	 */
	public enum ElementTypes{
		Condition,
		TimeCondition,
		Action
	};

	private ElementTypes currentType = null;

	private IFormDataExchangeIntegrace formParent = null;

	private List<String[]> elements = null;

	private boolean[] states = null;

	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("Cancel");
	private JPanel panelButtons = new JPanel();

	private int desktopWidth = -1;
	private int desktopHeight = -1;

	private final int panelWidth = 500;
	private final int buttonWidth = 100;
	private final int buttonHeight = 20;


	/**
	 * default constructor
	 * @param owner JFrame object
	 * @param requestForm observer form - all events from this object will be sent to the observer
	 */
	public DialogActCond(JFrame owner, IFormDataExchangeIntegrace requestForm){

		super(owner, "Policy conditions", true);
		formParent = requestForm;

		setModal(true);

		desktopWidth = GuiUtils.getScreenWidth();
		desktopHeight = GuiUtils.getScreenHeight();

		this.setLayout(null);

		int buttonMarginWidth = (panelWidth - 3 * buttonWidth) / 4;

		panelButtons.setLayout(null);
		panelButtons.setSize(panelWidth, buttonHeight);

		buttonOk.setSize(buttonWidth, buttonHeight);
		int widthButtonOk = buttonMarginWidth;
		buttonOk.setLocation(widthButtonOk , 0);
		panelButtons.add(buttonOk);

		buttonCancel.setSize(buttonWidth, buttonHeight);
		int widthButtonCancel = 3 * buttonMarginWidth + 2 * buttonWidth;
		buttonCancel.setLocation(widthButtonCancel, 0);
		panelButtons.add(buttonCancel);

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

				if(currentType != null && formParent != null){

					if(currentType == ElementTypes.Action){
						formParent.processUpdateActions(states);
					}else if(currentType == ElementTypes.Condition){
						formParent.processUpdateConditions(states);
					}else if(currentType == ElementTypes.TimeCondition){
						formParent.processUpdateTimeConditions(states);
					}
				}
				dispose();
			}
		});

		buttonCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}


	/**
	 * sets type of the dialog
	 * @param type
	 */
	public void setElementsType(ElementTypes type){
		currentType = type;
	}


	/**
	 * sets elements, which will be displayed on the dialog
	 * @param elements list of elements
	 */
	public void setElements(List<String[]> elements){

		this.elements = elements;

		if(elements != null){
			states = new boolean[elements.size()];
			for(int i=0 ; i<states.length ; i++){
				states[i] = true;
			}
		}else{
			states = null;
		}
	}


	@Override
	public void setVisible(boolean state) {

		if(state){
			int conditionsHeight = (elements == null ? 0 : elements.size() * 20);
			int dialogWidth = panelWidth;
			int dialogHeight = conditionsHeight + 100;

			setSize(dialogWidth, dialogHeight);
			setLocation((desktopWidth - dialogWidth)/ 2, (desktopHeight - dialogHeight)/ 2);

			panelButtons.setLocation(0,  dialogHeight - 50);
			this.getContentPane().add(panelButtons);

			if(elements != null){
				int cnt = 0;
				for (String[] condition : elements) {

					String conditionStr = null;
					if(condition.length == 3){
						conditionStr = condition[0] + "   " + condition[1] + "   " + condition[2];
					}else{
						conditionStr = condition[0];
					}

					final JCheckBox checkBoxCond = new JCheckBox(conditionStr, true);
					checkBoxCond.setSize(panelWidth, buttonHeight);
					checkBoxCond.setLocation(20, 10 + cnt * buttonHeight);
					this.getContentPane().add(checkBoxCond);

					checkBoxCond.setName(cnt + "");
					checkBoxCond.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) {
							boolean state = checkBoxCond.isSelected();
							int cnt = Integer.parseInt(checkBoxCond.getName());
							states[cnt] = state;
						}
						
					});
					cnt++;
				}
			}
		}

		super.setVisible(state);
	}
}
package itti.com.pl.eda.tactics.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.utils.StringUtils;

public class DialogAdvancedActions extends JDialog{

	private static final long serialVersionUID = 1L;

	private IFormDataExchangeIntegrace formParent = null;

	private PolicyTripleVariable[] vars = new PolicyTripleVariable[]{
			PolicyTripleVariable.Bandwidth,
			PolicyTripleVariable.Delay,
			PolicyTripleVariable.Jitter,
			PolicyTripleVariable.Loss,
	};
	private JTextArea[] textAreas = new JTextArea[vars.length];

	private int desktopWidth = -1;
	private int desktopHeight = -1;

	private final int panelWidth = 500;
	private final int buttonWidth = 100;
	private final int buttonHeight = 20;

	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("Cancel");
	private JPanel panelButtons = new JPanel();

	public DialogAdvancedActions(JFrame owner, IFormDataExchangeIntegrace requestForm){

		super(owner, "QoS policy actions", true);
		formParent = requestForm;

		setModal(true);

		desktopWidth = GuiUtils.getScreenWidth();
		desktopHeight = GuiUtils.getScreenHeight();

		this.setLayout(null);

		int buttonMarginWidth = (panelWidth - 3 * buttonWidth) / 4;

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

		buttonOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {

				boolean valid = true;
				int[] values = new int[textAreas.length];

				int i=0;
				for (JTextArea area : textAreas) {
					if(area.getText() == null || area.getText().trim().length() == 0 || !StringUtils.isInt(area.getText())){
						area.setBackground(Color.yellow);
						valid = false;
					}else{
						area.setBackground(Color.white);
						values[i] = StringUtils.getIntValue(area.getText());
					}
					i++;
				}
				if(valid){
					formParent.setActions(vars, values);
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

	@Override
	public void setVisible(boolean state) {

		if(state){

			int conditionsHeight = (vars.length + 2) * 20;
			int dialogWidth = panelWidth;
			int dialogHeight = conditionsHeight + 100;

			setSize(dialogWidth, dialogHeight);
			setLocation((desktopWidth - dialogWidth)/ 2, (desktopHeight - dialogHeight)/ 2);

			for(int i=0 ; i<vars.length ; i++){
				textAreas[i] = new JTextArea();
				textAreas[i].setBackground(Color.white);
				GuiUtils.fillRow(i + 1, vars[i].name(), textAreas[i], null, this);
			}

			panelButtons.setLocation(0,  dialogHeight - 50);
			this.getContentPane().add(panelButtons);
		}

		super.setVisible(state);
	}
}

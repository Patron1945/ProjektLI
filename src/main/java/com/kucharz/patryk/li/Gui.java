package com.kucharz.patryk.li;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Gui extends JFrame
{
	private JTextField m_textField;

	Gui()
	{
		setSize(new Dimension(500, 100));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		m_textField = new JTextField();
		m_textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				m_textField.setText("");
			}
		});
		m_textField.setBounds(10, 6, 375, 55);
		m_textField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		m_textField.setText("Wpisz zdanie logiczne");
		panel.add(m_textField);
		m_textField.setColumns(10);
		
		JButton m_checkBtn = new JButton("Sprawdz");
		m_checkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				if(m_textField.getText().contains("help"))
				{
					m_textField.setText("blabla");
				}
				else
				{
					LogicParser l_lp = LogicParser.getInstance();
					m_textField.setText(l_lp.parse(m_textField.getText()));
				}
			}
		});
		m_checkBtn.setBounds(395, 6, 89, 55);
		m_checkBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(m_checkBtn);
				
		this.setVisible(true);
		
	}
	
	
}

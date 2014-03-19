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

public class Gui extends JFrame
{
	private JTextField m_textField;

	Gui()
	{
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		m_textField = new JTextField();
		m_textField.setBounds(10, 6, 375, 45);
		m_textField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		m_textField.setText("Wpisz zdanie logiczne");
		panel.add(m_textField);
		m_textField.setColumns(10);
		
		JButton m_checkBtn = new JButton("Sprawdz");
		m_checkBtn.setBounds(395, 6, 79, 45);
		m_checkBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(m_checkBtn);
		
	}
	
	
}

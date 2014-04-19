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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends JFrame
{
	private JTextField m_textField;

	Gui()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(550, 100));

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		m_textField = new JTextField();
		m_textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0)
			{
				if (arg0.getClickCount() == 2)
				{
					m_textField.setText("");
				}

			}
		});
		m_textField.setBounds(10, 6, 375, 45);
		m_textField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		m_textField.setText("Wpisz zdanie logiczne");
		panel.add(m_textField);
		m_textField.setColumns(10);

		JButton m_checkBtn = new JButton("Sprawdz");
		m_checkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{

				int result = -1;
				try
				{
					result = LogicResolver.getInstance().resolve(m_textField.getText());
				}
				catch (Exception e)
				{
					m_textField.setText(e.getMessage());
				}

				if (result == 0)
					m_textField.setText("Tautologia");
				else if(result == 1)
					m_textField.setText("Tautologia spełnialna");
				else if(result == 2)
					m_textField.setText("Tautologia niespełnialna");

			}
		});
		m_checkBtn.setBounds(395, 6, 129, 45);
		m_checkBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(m_checkBtn);

		this.setVisible(true);

	}
	
	public static void main(String[] args)
	{
		Gui gui = new Gui();

	}

}

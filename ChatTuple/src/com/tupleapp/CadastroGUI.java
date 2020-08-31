package com.tupleapp;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.jini.space.JavaSpace;

public class CadastroGUI {
	private JFrame jf_menu;
	private JTextField tx_menu;
	private JButton bt_iniciar;
	private ServerTuplas cliente;
	
	public static void main(String[] args) {
		Lookup finder = new Lookup(JavaSpace.class);
		JavaSpace space = (JavaSpace) finder.getService();
		if (space == null) {
			System.out.println("Serviço JavaSpace nao encontrado. Encerrando...");
			System.exit(-1);
		}
		CadastroGUI main = new CadastroGUI(space);
		main.cliente = new ServerTuplas();
		main.cliente.criaEspacoSala(space);
		main.apresenta();
	}
	
	public CadastroGUI(JavaSpace space) {
		jf_menu = new JFrame("Chat das Tuplas");
		tx_menu = new JTextField("Digite seu nome");
		bt_iniciar = new JButton("Iniciar");
		
		bt_iniciar.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            	if (tx_menu.getText() == null || tx_menu.getText().isEmpty()) {
            		
            	} else {
            		Boolean retorno = cliente.criaUsuario(space, tx_menu.getText());
            		if(retorno) {
            			jf_menu.dispose();
            			MenuGUI menu = new MenuGUI(space, cliente);
            			menu.apresenta();
            		}
            	}
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
        });
	}
	
	public void apresenta(){
		tx_menu.setBounds(120,150, 400, 30);
		bt_iniciar.setBounds(280,200,100,50);
		bt_iniciar.setBackground(new Color(0,0,0,0));
		bt_iniciar.setFocusable(false);
		jf_menu.setLayout(null);
    	jf_menu.add(tx_menu);
		jf_menu.add(bt_iniciar);
		jf_menu.getContentPane().setBackground(new Color(71,74,81));
		jf_menu.setSize(640, 480);
		jf_menu.setLocationRelativeTo(null);
		jf_menu.setResizable(false);
		jf_menu.setVisible(true);
	}
}

package com.tupleapp;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import net.jini.space.JavaSpace;

public class MenuGUI {

	public JavaSpace space;
	public ServerTuplas cliente;
	public JFrame menu;
	public JButton criarSala;
	public JButton listarSalas;
	public JButton entrarEmSala;
	public JButton listarUsuarios;
	public JButton conversaPrivada;

	public MenuGUI(JavaSpace space, ServerTuplas cliente) {
		this.space = space;
		this.cliente = cliente;
		this.menu = new JFrame("Menu Principal");
		this.criarSala = new JButton("Criar sala!");
		this.listarSalas = new JButton("Listar salas!");
		this.entrarEmSala = new JButton("Entrar em uma sala!");
		this.listarUsuarios = new JButton("Listar usuários!");
		this.conversaPrivada = new JButton("Conversar com um usuário!");

		eventHandler(criarSala, 0);
		eventHandler(listarSalas, 1);
		eventHandler(entrarEmSala, 2);
		eventHandler(listarUsuarios, 3);
		eventHandler(conversaPrivada, 4);
	}

	public void apresenta() {
		criarSala.setBounds(10, 10, 230, 30);
		criarSala.setBackground(new Color(0, 0, 0, 0));
		criarSala.setFocusable(false);
		listarSalas.setBounds(240, 10, 230, 30);
		listarSalas.setBackground(new Color(0, 0, 0, 0));
		listarSalas.setFocusable(false);
		entrarEmSala.setBounds(10, 40, 230, 30);
		entrarEmSala.setBackground(new Color(0, 0, 0, 0));
		entrarEmSala.setFocusable(false);
		listarUsuarios.setBounds(240, 40, 230, 30);
		listarUsuarios.setBackground(new Color(0, 0, 0, 0));
		listarUsuarios.setFocusable(false);
		conversaPrivada.setBounds(10, 80, 230, 30);
		conversaPrivada.setBackground(new Color(0, 0, 0, 0));
		conversaPrivada.setFocusable(false);
		menu.setLayout(new GridLayout(3, 2));
		menu.add(criarSala);
		menu.add(listarSalas);
		menu.add(entrarEmSala);
		menu.add(listarUsuarios);
		menu.add(conversaPrivada);
		menu.setBackground(new Color(0, 0, 0));
		menu.setSize(640, 480);
		menu.setLocationRelativeTo(null);
		menu.setResizable(false);
		menu.setVisible(true);
	}

	public void eventHandler(JButton botao, Integer id) {
		botao.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				handlerBotao(id);
			}

			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
	}

	public void handlerBotao(Integer id) {
		switch (id) {
		case 0: {
			criarSala();
			break;
		}
		case 1: {
			listaSala();
			break;
		}
		case 2: {
			cliente.adicionarUsuario(space);
			break;
		}
		case 3: {
			cliente.listaUsuarios(space);
			break;
		}
		case 4: {
			cliente.conversaComUsuario(space);
			break;
		}

		default:
			throw new IllegalArgumentException("Unexpected value: " + id);
		}
	}
	
	public void listaSala() {
		List<String> salas = cliente.listaSalas(space);
		JTextArea obj = new JTextArea();
		obj.setBounds(120,150, 100, 200);
		String texto = "";
		for (String sala : salas) {
			texto = texto + sala + "\n";
			obj.setText(texto);
		}
		JOptionPane.showMessageDialog(null, obj);
		
	}
	
	public void criarSala() {
		String input = JOptionPane.showInputDialog(null, "Digite o nome da sala", "Criar Sala", JOptionPane.INFORMATION_MESSAGE);
		if (input == null || input.equals("")) {

		} else {
			Boolean retorno = cliente.criaSala(space, input);
			if (retorno) {
				JOptionPane.showMessageDialog(null, "Sala criada com sucesso!");
			} else {
				JOptionPane.showMessageDialog(null, "Sala ja cadastrada!");
			}
		}
	}

}

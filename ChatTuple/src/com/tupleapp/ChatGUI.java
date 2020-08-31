package com.tupleapp;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import net.jini.core.entry.Entry;
import net.jini.space.JavaSpace;

public class ChatGUI {

	public static Boolean readFlag = true;
	public JavaSpace space;
	public ServerTuplas cliente;
	public JFrame chat;
	public JTextArea conteudoChat = new JTextArea();
	public JTextField textoChat;
	public Entry destinatario;

	public ChatGUI(JavaSpace space, ServerTuplas cliente, Entry destinatario) {
		this.destinatario = destinatario;
		chat = new JFrame("Chat");
		textoChat = new JTextField();
		this.space = space;
		this.cliente = cliente;

		lerChat();

		handleTexto();
	}

	public void handleTexto() {
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conteudoChat.append(textoChat.getText() + "\n");
				cliente.chatPrincipal(space, destinatario, textoChat.getText());
				if (textoChat.getText().toLowerCase().equals("quit")) {
					readFlag = false;
					chat.dispose();
					MenuGUI menu = new MenuGUI(space, cliente);
					menu.apresenta();
				}
				textoChat.setText("");
			}
		};

		textoChat.addActionListener(action);
	}

	public void lerChat() {
		readFlag = true;

		Thread t1 = new Thread(() -> {
			if (destinatario instanceof Sala) {
				Message mensagem = new Message(null, ServerTuplas.usuario, null);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, 60 * 2000);
						if (retorno != null) {
							System.out.println(retorno.conteudo);
							String content = retorno.remetente + ": " + retorno.conteudo;
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									conteudoChat.append(content + "\n");
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Usuario remetente = (Usuario) destinatario;
				Message mensagem = new Message(null, ServerTuplas.usuario, remetente.nome);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, 60 * 2000);
						if (retorno != null) {
							if (retorno.remetente.equals(remetente.nome)) {
								System.out.println(retorno.conteudo);
								String content = retorno.remetente + ": " + retorno.conteudo;
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										conteudoChat.append(content + "\n");
									}
								});
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t1.start();
	}

	public void apresenta() {
		textoChat.setText("Digite quit para sair");
		DefaultCaret caret = (DefaultCaret) conteudoChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		conteudoChat.setBounds(0, 0, 640, 420);
		conteudoChat.setForeground(Color.white);
		conteudoChat.setBackground(new Color(71, 74, 81));
		conteudoChat.setEditable(false);
		textoChat.setBounds(0, 420, 640, 40);
		chat.setLayout(null);
		chat.add(conteudoChat);
		chat.add(textoChat);
		chat.getContentPane().setBackground(new Color(71, 74, 81));
		chat.setSize(640, 480);
		chat.setLocationRelativeTo(null);
		chat.setResizable(false);
		chat.setVisible(true);
	}
}

package com.tupleapp;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ChatGUI {
	
	public static Boolean readFlag = true;
	public JavaSpace space;
	public ServerTuplas cliente;
	public JFrame chat;
	public JTextArea conteudoChat;
	public JTextField textoChat;
	public Entry destinatario;
	public String texto = "";

	public ChatGUI(JavaSpace space, ServerTuplas cliente, Entry destinatario) {
		this.destinatario = destinatario;
		this.chat = new JFrame("Chat");
		this.conteudoChat = new JTextArea();
		this.textoChat = new JTextField();
		this.space = space;
		this.cliente = cliente;
		
		handleTexto();
		lerChat();
	}
	
	public void handleTexto() {
		Action action = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
				texto = texto + textoChat.getText() + "\n";
				conteudoChat.setText(texto);
		    	cliente.chatPrincipal(space, destinatario, textoChat.getText());
		    	if (textoChat.getText().toLowerCase().equals("quit")) {
		    		MenuGUI menu = new MenuGUI(space,cliente);
		    		menu.apresenta();
		    		chat.dispose();
		    		readFlag = false;
		    	}
		    	textoChat.setText("");
		    }
		};

		textoChat.addActionListener( action );
	}
	
	public void lerChat() {
		readFlag = true;

		Thread t1 = new Thread(() -> {
			if (destinatario instanceof Sala) {
				Message mensagem = new Message(null, ServerTuplas.usuario, null);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, Lease.FOREVER);
						String content = retorno.remetente + ": " + retorno.conteudo;
						texto = texto + content + "\n";
						conteudoChat.setText(texto);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Usuario remetente = (Usuario) destinatario;
				Message mensagem = new Message(null, ServerTuplas.usuario, remetente.nome);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, Lease.FOREVER);
						if (retorno.remetente.equals(remetente.nome)) {
							String content = retorno.remetente + ": " + retorno.conteudo;
							texto = texto + content + "\n";
							conteudoChat.setText(texto);
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
		conteudoChat.setEditable(false);
		conteudoChat.setBounds(0, 0, 640, 420);
		textoChat.setBounds(0, 420, 640, 40);
		chat.setLayout(null);
		chat.add(conteudoChat);
		chat.add(textoChat);
		chat.setBackground(new Color(0, 0, 0));
		chat.setSize(640, 480);
		chat.setLocationRelativeTo(null);
		chat.setResizable(false);
		chat.setVisible(true);
	}
}

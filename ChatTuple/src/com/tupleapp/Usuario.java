package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class Usuario implements Entry {
	private static final long serialVersionUID = 1L;
	public String nome;
	public List<Message> mensagens;
	
    public Usuario(String nome, List<Message> mensagens) {
    	this.nome = nome;
    	this.mensagens = mensagens;
    }
    
    public Usuario() {
    	
    }
}
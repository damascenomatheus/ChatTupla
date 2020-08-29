package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class Sala implements Entry {
	private static final long serialVersionUID = 1L;
	public String nome;
	public List<Usuario> contatos;
	public List<String> mensagens;
	
    public Sala(String nome, List<Usuario> contatos, List<String> mensagens) {
    	this.nome = nome;
    	this.contatos = contatos;
    	this.mensagens = mensagens;
    }
    
    public Sala() {
    	
    }
}
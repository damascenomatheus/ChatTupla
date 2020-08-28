package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class Sala implements Entry {
	private static final long serialVersionUID = 1L;
	public String nome;
	public List<String> contatos;
	
    public Sala(String nome, List<String> contatos) {
    	this.nome = nome;
    	this.contatos = contatos;
    }
}
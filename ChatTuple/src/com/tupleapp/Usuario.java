package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class Usuario implements Entry {
	private static final long serialVersionUID = 1L;
	public String nome;
	
    public Usuario(String nome) {
    	this.nome = nome;
    }
    
    public Usuario() {
    	
    }
}
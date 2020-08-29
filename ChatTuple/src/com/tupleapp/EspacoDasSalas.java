package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class EspacoDasSalas implements Entry {
	private static final long serialVersionUID = 1L;
	public String nome;
	public List<Sala> salas;

	public EspacoDasSalas(String nome, List<Sala> salas) {
		this.nome = nome;
		this.salas = salas;
	}
    
    public EspacoDasSalas() {
    	
    }
}
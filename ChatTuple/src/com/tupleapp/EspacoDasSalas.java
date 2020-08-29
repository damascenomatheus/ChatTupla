package com.tupleapp;
import java.util.List;
import net.jini.core.entry.Entry;

public class EspacoDasSalas implements Entry {
	private static final long serialVersionUID = 1L;
	public List<Sala> salas;
	
    public EspacoDasSalas(List<Sala> salas) {
    	this.salas = salas;
    }
    
    public EspacoDasSalas() {
    	
    }
}
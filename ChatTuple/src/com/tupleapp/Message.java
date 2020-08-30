package com.tupleapp;
import net.jini.core.entry.Entry;

public class Message implements Entry {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String conteudo;
	public Entry receptor;
	public String remetente;
	
	public Message(String conteudo, Entry receptor, String remetente) {
		this.conteudo = conteudo;
		this.receptor = receptor;
		this.remetente = remetente;
	}
	
    public Message() {
    
    }
}

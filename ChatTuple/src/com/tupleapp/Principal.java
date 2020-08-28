package com.tupleapp;
import java.util.Scanner;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.util.ArrayList;
import java.util.List;

public class Principal {
	private static List<Sala> salas;

	public static void main(String[] args) {
		 Lookup finder = new Lookup(JavaSpace.class);
         JavaSpace space = (JavaSpace) finder.getService();
         if (space == null) {
             System.out.println("Serviço JavaSpace nao encontrado. Encerrando...");
             System.exit(-1);
         } 
         
         criaUsuario(space);
        
         while(true) {
        	 System.out.println("1 - Criar Sala");
             System.out.println("2 - Listar Salas");
             System.out.print("Digite o número que deseja: ");
             Scanner scanner = new Scanner(System.in);
             String message = scanner.nextLine();
                 
            switch (message) {
    		case "1": {
    			criaSala(space);
    			break;
    		}
    		case "2": {
    			listaSalas(space);
    			break;
    		}
    		default:
    			throw new IllegalArgumentException("Unexpected value: " + message);
    		}
         }
	}
	
	public static void criaSala(JavaSpace space) {
		System.out.print("Digite o nome da Sala: ");
		Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        
        if (message == null || message.equals("")) {
            System.exit(0);
        }
        Sala sala = new Sala(message.toLowerCase(), null, null);
        
        try {
        	Entry retorno = space.readIfExists(sala, null, 60 * 1000);
        	if (retorno == null) {
        		System.out.println("\nCriou a sala: " + message + "\n");
                
                try { 
               	 space.write(sala, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	} else {
        		System.out.println("\nSala já cadastrado\n");
        		criaSala(space);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void criaUsuario(JavaSpace space) {
		System.out.print("Digite seu nome: ");
		Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        
        if (message == null || message.equals("")) {
            System.exit(0);
        }
        Usuario user = new Usuario(message.toLowerCase(), null);
        
        try {
        	Entry retorno = space.readIfExists(user, null, 60 * 1000);
        	if (retorno == null) {
        		System.out.println("\nCriou o usuario: " + message + "\n");
                
                try { 
               	 space.write(user, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	} else {
        		System.out.println("\nUsuario já cadastrado\n");
        		criaUsuario(space);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void listaSalas(JavaSpace space) {
		Sala sala = new Sala();
		salas = new ArrayList<>();
		int i = 0;
		try {
        	Sala retorno = (Sala) space.takeIfExists(sala, null, 60 * 1000);
        	if (retorno == null) {
        		System.out.println("\nNenhuma sala criada\n");
        	} else {
        		while (retorno != null) {
            		System.out.println(retorno.nome);
            		salas.add(retorno);
            	} 
            	while (salas.isEmpty() != true) {
            		try {
            			space.write(salas.get(i), null, Lease.FOREVER);
            			i++;
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}

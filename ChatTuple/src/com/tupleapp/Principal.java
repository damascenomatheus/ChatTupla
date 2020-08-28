package com.tupleapp;
import java.util.Scanner;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class Principal {
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
		Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome da Sala: ");
        String message = scanner.nextLine();
        if (message == null || message.equals("")) {
            System.exit(0);
        }
        Sala sala = new Sala(message, null, null);
        System.out.println("criou a sala: " + message);
        try { 
       	 space.write(sala, null, 60 * 1000);
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
		System.out.println("\nTem sala\n");
	}
}

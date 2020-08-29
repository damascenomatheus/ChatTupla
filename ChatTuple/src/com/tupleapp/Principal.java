package com.tupleapp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class Principal {
	
	public static Usuario usuario;
	public static EspacoDasSalas salaoTemplate = new EspacoDasSalas("Salão", null);

	public static void main(String[] args) {
		 Lookup finder = new Lookup(JavaSpace.class);
         JavaSpace space = (JavaSpace) finder.getService();
         if (space == null) {
             System.out.println("Serviço JavaSpace nao encontrado. Encerrando...");
             System.exit(-1);
         } 
         criaEspacoSala(space);
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
	
	public static void criaEspacoSala(JavaSpace space) {
		EspacoDasSalas instanciaSalao = new EspacoDasSalas("Salão", new ArrayList<Sala>());
		try {
        	Entry retorno = space.readIfExists(salaoTemplate, null, 60 * 1000);
        	if (retorno == null) {
        		System.out.println("\nCriou o salão: " + instanciaSalao.nome + "!\n");
                try { 
               	 space.write(instanciaSalao, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	} else {
        		System.out.println("\nSalão já cadastrado\n");
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void criaSala(JavaSpace space) {
		System.out.print("Digite o nome da Sala: ");
		Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        
        if (message == null || message.equals("")) {
            
        } else {
        	Sala sala = new Sala(message.toLowerCase(), null, null);
            try {
            	EspacoDasSalas retorno = (EspacoDasSalas) space.takeIfExists(salaoTemplate, null, 60 * 1000);
            	System.out.println("\nSalão: " + retorno.nome + " Encontrado!\n");
            	var salaNomeIgual = retorno.salas.stream().filter((el) ->  el.nome.equals(sala.nome)).findFirst();
            	if (salaNomeIgual.isEmpty()) {
            		System.out.println("\nCriou a sala: " + message + "\n");
            		try { 
            			retorno.salas.add(sala);
                   	 	space.write(retorno, null, Lease.FOREVER);
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	} else {
            		System.out.println("\nSala já cadastrada\n");
            		criaSala(space);
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
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
        usuario = user;
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
		try {
        	EspacoDasSalas retorno = (EspacoDasSalas) space.readIfExists(salaoTemplate, null, 60 * 1000);
        	if (retorno != null) {
                for(Sala sala: retorno.salas) {
                	System.out.println("\n" + sala.nome + "\n");
                }
        	} else {
        		System.out.println("\nNão existem salas criadas\n");
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}

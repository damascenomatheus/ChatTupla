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
        	 System.out.println("\n1 - Criar Sala");
             System.out.println("2 - Listar Salas");
             System.out.println("3 - Entrar em uma Sala");
             System.out.println("4 - Listar Usuários de uma Sala");
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
    		case "3": {
    			adicionarUsuario(space);
    			break;
    		}
    		case "4": { 
    			listaUsuarios(space);
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
        		System.out.println("\nSalão já cadastrado.\n");
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
        	Sala sala = new Sala(message.toLowerCase(), new ArrayList<Usuario>(), new ArrayList<Message>());
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
            		System.out.println("\nSala já cadastrad.\n");
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
        Usuario user = new Usuario(message.toLowerCase(), new ArrayList<Message>());
        try {
        	Entry retorno = space.readIfExists(user, null, 60 * 1000);
        	if (retorno == null) {
        		System.out.println("\nCriou o usuario: " + message + "\n");
                
                try { 
               	 space.write(user, null, Lease.FOREVER);
               	 usuario = user;
                } catch (Exception e) {
                    e.printStackTrace();
                } 
        	} else {
        		System.out.println("\nUsuario já cadastrado.\n");
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
        		if (retorno.salas.isEmpty()) {
        			System.out.println("\nNão existem salas criadas\n");
        		} else {
        			for(Sala sala: retorno.salas) {
                    	System.out.println("\n" + sala.nome);
                    }
        		}
        	} else {
        		System.out.println("\nNão existe salão!\n");
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void adicionarUsuario(JavaSpace space) {
		System.out.print("Digite o nome da Sala que deseja entrar: ");
		Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        
        if (message == null || message.equals("")) {
            
        } else {
        	
        	EspacoDasSalas salao = null;
            try {
            	salao = (EspacoDasSalas) space.takeIfExists(salaoTemplate, null, 60 * 1000);
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            var salaNomeIgual = salao.salas.stream().filter((el) ->  el.nome.equals(message.toLowerCase())).findFirst().orElse(null);
        	if (salaNomeIgual != null) {
        		try { 
        			System.out.println("\nSala: " + salaNomeIgual.nome + " Encontrada!\n");
        			salaNomeIgual.contatos.add(usuario);
               	 	space.write(salao, null, Lease.FOREVER);
               	 	System.out.println("\nUsuario inserido!\n");
               	 	//aqui chama o chat
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }
	}
	
	public static void listaUsuarios(JavaSpace space) {
		System.out.print("Digite o nome da Sala que deseja ler a lista de usuários: ");
		Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        
		try {
			Sala sala = new Sala(message.toLowerCase(), null, null);
        	Sala retorno = (Sala) space.readIfExists(sala, null, 60 * 1000);
        	if (retorno != null) {
        		if (retorno.contatos.isEmpty()) {
        			System.out.println("\nNão existem usuários nessa sala.\n");
        		} else {
        			for(Usuario user: retorno.contatos) {
                    	System.out.println("\n" + user.nome);
                    }
        		}
        	} else {
        		System.out.println("\nNão existe essa sala!\n");
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void chatPrincipal(JavaSpace space, Entry destinatario) {
		System.out.print("Escreva suas mensagens(quit para sair da sala): ");
		Scanner scanner = new Scanner(System.in);
		
        while(true) { 
        	String message = scanner.nextLine();
        	EspacoDasSalas salaoChat = null;
			try {
				salaoChat = (EspacoDasSalas) space.take(salaoTemplate, null, 60 * 1000);
			} catch (Exception e) {
				 e.printStackTrace();
			}
            if (message.toLowerCase() == "quit") {
            	 if (destinatario instanceof Usuario == false) {
            		 Sala sala = salaoChat.salas.stream().filter((el) ->  el.contatos.contains(usuario)).findFirst().orElse(null);
            		 sala.contatos.remove(usuario);
        			 try {
            			 space.write(salaoChat, null, Lease.FOREVER);
            		 } catch (Exception e) {
            			 e.printStackTrace();
            		 }
            		 break;
            	 } else {
            		 break;
            	 }
            } else {
            	try {
                	Message mensagem = new Message(message, destinatario, usuario.nome);
                	space.write(mensagem, null, 60 * 5000);
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
            scanner.close();
            }
        }
}

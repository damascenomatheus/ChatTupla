package com.tupleapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ServerTuplas {

	public static Boolean readFlag = true;
	public static Usuario usuario;
	public static EspacoDasSalas salaoTemplate = new EspacoDasSalas("Salão", null);


	public void criaEspacoSala(JavaSpace space) {
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

	public void criaSala(JavaSpace space) {
		System.out.print("\nDigite o nome da Sala: ");
		Scanner scanner = new Scanner(System.in);
		String message = scanner.nextLine();

		if (message == null || message.equals("")) {

		} else {
			Sala sala = new Sala(message.toLowerCase(), new ArrayList<Usuario>(), new ArrayList<Message>());
			try {
				EspacoDasSalas retorno = (EspacoDasSalas) space.takeIfExists(salaoTemplate, null, 60 * 1000);
				System.out.println("\nSalão: " + retorno.nome + " Encontrado!\n");
				var salaNomeIgual = retorno.salas.stream().filter((el) -> el.nome.equals(sala.nome)).findFirst();
				if (salaNomeIgual.isEmpty()) {
					System.out.println("\nCriou a sala: " + message + "\n");
					try {
						retorno.salas.add(sala);
						space.write(retorno, null, Lease.FOREVER);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("\nSala já cadastrada.\n");
					try {
						space.write(retorno, null, Lease.FOREVER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					criaSala(space);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Boolean criaUsuario(JavaSpace space, String nome) {
		Usuario user = new Usuario(nome.toLowerCase());
		try {
			Entry retorno = space.readIfExists(user, null, 60 * 1000);
			if (retorno == null) {
				try {
					space.write(user, null, Lease.FOREVER);
					usuario = user;
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public List<String> listaSalas(JavaSpace space) {
		List<String> nomeDasSalas = new ArrayList<String>();
		
		try {
			EspacoDasSalas retorno = (EspacoDasSalas) space.readIfExists(salaoTemplate, null, 60 * 1000);
			if (retorno != null) {
				if (retorno.salas.isEmpty()) {
					System.out.println("\nNão existem salas criadas\n");
				} else {
					for (Sala sala : retorno.salas) {
						nomeDasSalas.add(sala.nome);
					}
				}
			} else {
				System.out.println("\nNão existe salão!\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nomeDasSalas;
	}

	public void adicionarUsuario(JavaSpace space) {
		System.out.print("\nDigite o nome da Sala que deseja entrar: ");
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

			var salaNomeIgual = salao.salas.stream().filter((el) -> el.nome.equals(message.toLowerCase())).findFirst()
					.orElse(null);
			if (salaNomeIgual != null) {
				try {
					System.out.println("\nSala: " + salaNomeIgual.nome + " Encontrada!");
					salaNomeIgual.contatos.add(usuario);
					space.write(salao, null, Lease.FOREVER);
					System.out.println("\nUsuario inserido!\n");
					chatPrincipal(space, salaNomeIgual);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					space.write(salao, null, Lease.FOREVER);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void conversaComUsuario(JavaSpace space) {
		System.out.print("\nDigite o nome do usuário que deseja conversar: ");
		Scanner scanner = new Scanner(System.in);
		String message = scanner.nextLine();

		if (message == null || message.equals("")) {

		} else {
			Usuario user = new Usuario(message.toLowerCase());
			try {
				Usuario destino = (Usuario) space.readIfExists(user, null, 60 * 1000);
				if (destino == null) {
					System.out.println("Usuario não existe.");
				} else {
					chatPrincipal(space, destino);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void listaUsuarios(JavaSpace space) {
		System.out.print("\nDigite o nome da Sala que deseja ler a lista de usuários: ");
		Scanner scanner = new Scanner(System.in);
		String message = scanner.nextLine();

		try {
			EspacoDasSalas retorno = (EspacoDasSalas) space.readIfExists(salaoTemplate, null, 60 * 1000);
			Sala sala = retorno.salas.stream().filter((el) -> el.nome.equals(message.toLowerCase())).findFirst()
					.orElse(null);
			if (sala.contatos.isEmpty()) {
				System.out.println("\nNão existem usuarios nessa sala\n");
			} else {
				for (Usuario user : sala.contatos) {
					System.out.println("\n" + user.nome);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void chatPrincipal(JavaSpace space, Entry destinatario) {
		System.out.print("Escreva suas mensagens(quit para sair da sala): \n");
		Scanner scanner = new Scanner(System.in);
		readFlag = true;

		Thread t1 = new Thread(() -> {
			if(destinatario instanceof Sala) {
				Message mensagem = new Message(null, usuario, null);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, Lease.FOREVER);
						System.out.println(retorno.remetente + ": " + retorno.conteudo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Usuario remetente = (Usuario) destinatario;
				Message mensagem = new Message(null, usuario, remetente.nome);
				while (readFlag) {
					try {
						Message retorno = (Message) space.take(mensagem, null, Lease.FOREVER);
						if (retorno.remetente.equals(remetente.nome)) {
							System.out.println(retorno.remetente + ": " + retorno.conteudo);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t1.start();

		while (true) {
			String message = scanner.nextLine();
			EspacoDasSalas salaoChat = null;

			if (message.toLowerCase().equals("quit")) {
				if (destinatario instanceof Usuario == false) {
					try {
						salaoChat = (EspacoDasSalas) space.take(salaoTemplate, null, 60 * 1000);
					} catch (Exception e) {
						e.printStackTrace();
					}

					Sala sala = salaoChat.salas.stream()
							.filter((el) -> el.contatos.stream().map((obj) -> obj.nome).collect(Collectors.toList()).contains(usuario.nome))
							.findFirst().orElse(null);
					sala.contatos.removeIf((el) -> el.nome.equals(usuario.nome));
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
					salaoChat = (EspacoDasSalas) space.read(salaoTemplate, null, 60 * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (destinatario instanceof Sala) {
					Sala sala = salaoChat.salas.stream().filter((el) -> el.contatos.stream().map((obj) -> obj.nome)
							.collect(Collectors.toList()).contains(usuario.nome)).findFirst().orElse(null);
					if (sala != null) {
						for (Usuario contato : sala.contatos) {
							if (!contato.nome.equals(usuario.nome)) {
								Message mensagem = new Message(message, contato, usuario.nome);
								try {
									space.write(mensagem, null, 60 * 5000);
								} catch (Exception e) {
									e.printStackTrace();
								}	
							}
						}
					}
				} else {
					Message mensagem = new Message(message, destinatario, usuario.nome);
					try {
						space.write(mensagem, null, 60 * 5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}		
		readFlag = false;
	}
}

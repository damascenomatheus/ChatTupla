package com.tupleapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class ServerTuplas {

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

	public Boolean criaSala(JavaSpace space, String message) {
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
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else {
				try {
					space.write(retorno, null, Lease.FOREVER);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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

	public Sala chatEmSala(JavaSpace space, String message) {
		if (message == null || message.equals("")) {
			return null;
		} else {
			EspacoDasSalas salao = null;
			try {
				salao = (EspacoDasSalas) space.takeIfExists(salaoTemplate, null, 60 * 1000);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			var salaNomeIgual = salao.salas.stream().filter((el) -> el.nome.equals(message.toLowerCase())).findFirst()
					.orElse(null);
			if (salaNomeIgual != null) {
				try {
					System.out.println("\nSala: " + salaNomeIgual.nome + " Encontrada!");
					salaNomeIgual.contatos.add(usuario);
					space.write(salao, null, Lease.FOREVER);
					System.out.println("\nUsuario inserido!\n");
					return salaNomeIgual;
					//chatPrincipal(space, salaNomeIgual);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				try {
					space.write(salao, null, Lease.FOREVER);
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	public Boolean conversaComUsuario(JavaSpace space, String message) {
		if (message == null || message.equals("")) {
			return false;
		} else {
			Usuario user = new Usuario(message.toLowerCase());
			try {
				Usuario destino = (Usuario) space.readIfExists(user, null, 60 * 1000);
				if (destino == null) {
					System.out.println("Usuario não existe.");
					return false;
				} else {
					return true;
					// chatPrincipal(space, destino);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public List<String> listaUsuarios(JavaSpace space, String message) {
		List<String> nomeDosUsuarios = new ArrayList<String>();
		if (message == null || message.equals("")) {

		} else {
			try {
				EspacoDasSalas retorno = (EspacoDasSalas) space.readIfExists(salaoTemplate, null, 60 * 1000);
				Sala sala = retorno.salas.stream().filter((el) -> el.nome.equals(message.toLowerCase())).findFirst()
						.orElse(null);
				if (sala.contatos.isEmpty()) {

				} else {
					for (Usuario user : sala.contatos) {
						nomeDosUsuarios.add(user.nome);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nomeDosUsuarios;
	}

	public void chatPrincipal(JavaSpace space, Entry destinatario, String message) {
		EspacoDasSalas salaoChat = null;

		if (message.toLowerCase().equals("quit")) {
			if (destinatario instanceof Usuario == false) {
				try {
					salaoChat = (EspacoDasSalas) space.take(salaoTemplate, null, 60 * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Sala sala = salaoChat.salas.stream().filter((el) -> el.contatos.stream().map((obj) -> obj.nome)
						.collect(Collectors.toList()).contains(usuario.nome)).findFirst().orElse(null);
				sala.contatos.removeIf((el) -> el.nome.equals(usuario.nome));
				try {
					space.write(salaoChat, null, Lease.FOREVER);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
}

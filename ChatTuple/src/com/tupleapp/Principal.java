package com.tupleapp;
import java.util.Scanner;

import net.jini.space.JavaSpace;

public class Principal {
	public static void main(String[] args) {
		 Lookup finder = new Lookup(JavaSpace.class);
         JavaSpace space = (JavaSpace) finder.getService();
         if (space == null) {
             System.out.println("Serviço JavaSpace nao encontrado. Encerrando...");
             System.exit(-1);
         } 
         

         
	}
	
	public void criaSala(JavaSpace space) {
		Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Entre com o texto da mensagem (ENTER para sair): ");
            String message = scanner.nextLine();
            if (message == null || message.equals("")) {
                System.exit(0);
            }
            Sala sala = new Sala(message, null);
            try { 
           	 space.write(sala, null, 60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}

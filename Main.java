package TrabalhoFinal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import ListaEncadeada.NoLista;
import Pilha.PilhaVetor;
import TabelaDispersao.MapaDispersao;
import TabelaDispersao.NoMapa;

public class Main {

    private static String lerArquivo(String arquivo) throws IOException {
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        }
        return conteudo.toString();
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Insira o caminho do arquivo a ser analisado: ");
        String arquivo = s.nextLine();
        s.close();

        try {
            String htmlContent = lerArquivo(arquivo);
            MapaDispersao<Integer> mapaTags = new MapaDispersao<>(100);

            ValidaHTML validaHTML = new ValidaHTML();
            PilhaVetor<String> pilha = validaHTML.analisaHTML(htmlContent, mapaTags);

            if (pilha.estaVazia()) {
                System.out.println("Arquivo bem formatado");
            } else {
                System.out.println("Arquivo mal formatado" + " conteudo da pilha  - " + pilha.toString());
            }

            System.out.println("Ocorrências de cada tag de abertura: ");
            for (int i = 0; i < mapaTags.info.length; i++) {
                if (mapaTags.info[i] != null) {
                    NoLista<NoMapa<Integer>> no = mapaTags.info[i].getPrimeiro();
                    while (no != null) {
                        System.out.println("Tag: " + no.getInfo().getChave() + " - Ocorrências: " + no.getInfo().getInfo());
                        no = no.getProximo();
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Arquivo não encontrado");
        }
    }

}
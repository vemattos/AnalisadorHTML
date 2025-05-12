package TrabalhoFinal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import Pilha.PilhaVetor;

public class Validador {

    private static final Set<String> SINGLETON_TAGS = new HashSet<>();

    static {
        SINGLETON_TAGS.add("<meta>");
        SINGLETON_TAGS.add("<base>");
        SINGLETON_TAGS.add("<br>");
        SINGLETON_TAGS.add("<col>");
        SINGLETON_TAGS.add("<command>");
        SINGLETON_TAGS.add("<embed>");
        SINGLETON_TAGS.add("<hr>");
        SINGLETON_TAGS.add("<img>");
        SINGLETON_TAGS.add("<input>");
        SINGLETON_TAGS.add("<link>");
        SINGLETON_TAGS.add("<param>");
        SINGLETON_TAGS.add("<source>");
        SINGLETON_TAGS.add("<!DOCTYPE>");
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Insira o caminho do arquivo a ser analisado: ");
        String arquivo = s.nextLine();
        s.close();

        try {
            String htmlContent = lerArquivo(arquivo);
            Map<String, Integer> mapaTags = new HashMap<>();
            PilhaVetor<String> pilha = analisaHTML(htmlContent, mapaTags);

            if (pilha.estaVazia()) {
                System.out.println("Arquivo bem formatado");
            } else {
                System.out.println("Arquivo mal formatado" + " conteudo da pilha  - " + pilha.toString());
            }

            System.out.println("Ocorrências de cada tag de abertura: ");
            for (Map.Entry<String, Integer> entry : mapaTags.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        } catch (IOException e) {
            System.out.println("Arquivo não encontrado");
        }
    }

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

    private static PilhaVetor<String> analisaHTML(String conteudo, Map<String, Integer> mapaTags) {
        PilhaVetor<String> pilha = new PilhaVetor<>(9999);
        StringBuilder texto = new StringBuilder();
        boolean insideTag = false;
    
        for (int i = 0; i < conteudo.length(); i++) {
            char ch = conteudo.charAt(i);
            if (ch == '<') {
                if (texto.length() > 0) {
                    texto.setLength(0);
                }
                insideTag = true;
                texto.append(ch);
            } else if (insideTag && ch == '/') {
                texto.append(ch);
            } else if (ch == '>') {
                texto.append(ch);
                String tag = texto.toString().trim();
                String tagName = getTagName(tag);
    
                if (SINGLETON_TAGS.contains(tagName)) {
                    System.out.println("Singleton tag encontrada: " + tag);
                    mapaTags.put(tagName, mapaTags.getOrDefault(tagName, 0) + 1);
                } else {
                    if (tag.startsWith("</")) {
                        tagName = tag.substring(2, tag.length() - 1);
                        System.out.println("TagFim: " + tagName);
                        String result = acharTag(pilha, tagName);
                        if (!result.equals("erro")) {
                            System.out.println("Removendo da pilha: " + result);
                            while (!pilha.estaVazia()) {
                                String top = pilha.pop();
                                System.out.println("Removeu da pilha: " + top);
                                if (top.equalsIgnoreCase(result)) {
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Erro: Tag de fechamento inesperada.");
                        }
                    } else {
                        pilha.push(tag);
                        System.out.println("Empilhado: " + tag);
                        System.out.println("=====================\n" + pilha.toString());
                        mapaTags.put(tag, mapaTags.getOrDefault(tag, 0) + 1);
                    }
                }
                texto.setLength(0);
                insideTag = false;
            } else {
                texto.append(ch);
            }
        }
    
        if (texto.length() > 0) {
            pilha.push(texto.toString().trim());
        }
    
        return pilha;
    }

    private static String acharTag(PilhaVetor<String> pilha, String tag) {
        PilhaVetor<String> pilhaLocal = new PilhaVetor<>(pilha.tamanho);
        pilhaLocal.concatenar(pilha);

        String top;
        while (!pilhaLocal.estaVazia()) {
            top = pilhaLocal.pop();
            System.out.println("Removendo da pilha local: " + top);

            if (getTagName(top).equals(tag)) {
                System.out.println("Achou: " + top + " -> deve retirar até aqui");
                return top;
            } else if (top.startsWith("<") && !top.startsWith("</")) {
                System.out.println("Tag de abertura inesperadamente encontrada: " + top);
                return "erro";
            }
        }
        return "erro";
    }

    private static String getTagName(String tag) {
        int startIndex = tag.indexOf("<") + 1;
        int endIndex = tag.indexOf(" ");
        if (endIndex == -1) {
            endIndex = tag.indexOf(">");
        }
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return "";
        }
        return tag.substring(startIndex, endIndex).replace("/", "").trim();
    }
}

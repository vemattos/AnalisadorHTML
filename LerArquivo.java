package TrabalhoFinal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class LerArquivo {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Insira o caminho do arquivo: ");
        String nomeArquivo = s.next();

        try {
            FileReader fr = new FileReader(nomeArquivo);
            BufferedReader br = new BufferedReader(fr);

            String linha;
            while ((linha = br.readLine()) != null) {
                System.out.println(linha);
            }

            br.close();
            fr.close();
            s.close();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
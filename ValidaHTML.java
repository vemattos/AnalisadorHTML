package TrabalhoFinal;
import ListaEncadeada.ListaEncadeada;
import ListaEncadeada.NoLista;
import Pilha.PilhaVetor;
import TabelaDispersao.MapaDispersao;

public class ValidaHTML {

    private final ListaEncadeada<String> sTags;

    public ValidaHTML() {
        this.sTags = new ListaEncadeada<>();
        inicializarSTags();
    }

    private void inicializarSTags() {
        sTags.inserir("meta");
        sTags.inserir("base");
        sTags.inserir("br");
        sTags.inserir("col");
        sTags.inserir("command");
        sTags.inserir("embed");
        sTags.inserir("hr");
        sTags.inserir("img");
        sTags.inserir("input");
        sTags.inserir("link");
        sTags.inserir("param");
        sTags.inserir("source");
        sTags.inserir("!DOCTYPE");
    }


    public PilhaVetor<String> analisaHTML(String conteudo, MapaDispersao<Integer> mapaTags) {
        PilhaVetor<String> pilha = new PilhaVetor<>(9999);
        StringBuilder texto = new StringBuilder();
        boolean insideTag = false;

        for (int i = 0; i < conteudo.length(); i++) { //percorre cada caracter do arquivo
            char ch = conteudo.charAt(i); // extrai o caracter
            if (ch == '<') { // verifica se a tag abriu
                if (texto.length() > 0) {
                    texto.setLength(0); //limpa o conteudo do stringBuilder
                }
                insideTag = true; //indica q está dentro da tag
                texto.append(ch); //adiciona ao texto o <
            } else if (insideTag && ch == '/') { //se estiver dentro da tag e tiver /
                texto.append(ch); //adiciona ao stringbuilder
            } else if (ch == '>') { //identifica o final da tag
                texto.append(ch); //adiciona ao stringbuilder
                String tag = texto.toString().trim(); //converte td o conteudo do stringbuilder pra string
                String tagName = getTagName(tag); //extrai o nome da tag

                if (isSingletonTag(tagName)) { //verifica se é uma singleton tag
                    System.out.println("Singleton tag encontrada: " + tag);
                    Integer count = mapaTags.buscar(tagName); //busca o contador da tag no mapa
                    mapaTags.inserir(tagName, (count == null) ? 1 : count + 1); //atualiza o contador
                } else {
                    if (tag.startsWith("</")) { //verifica se eh uma tag de fechamento
                        tagName = tag.substring(2, tag.length() - 1); //extrai o nome da tag
                        System.out.println("TagFim: " + tagName);
                        String result = acharTag(pilha, tagName); //procura a tag de abertura na pilha
                        if (!result.equals("erro")) { //verifica se a tag de abertura foi encontrada
                            System.out.println("Removendo da pilha: " + result);
                            while (!pilha.estaVazia()) { //remove da pilha
                                String top = pilha.pop();
                                System.out.println("Removeu da pilha: " + top);
                                if (top.equalsIgnoreCase(result)) {
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Erro: Tag de fechamento inesperada."); // se a tag n for encontrada imprime erro
                        }
                    } else {
                        if (!isSingletonTag(tagName)) { 
                            pilha.push(tag); //empilha a tag se n for singleton
                            System.out.println("Empilhado: " + tag);
                            System.out.println("=====================\n" + pilha.toString());
                        } else {
                            System.out.println("Erro: Tentativa de empilhar uma tag singleton: " + tag); //erro se tentar empilhar singleton
                        }
                        Integer count = mapaTags.buscar(tagName); //busca o contador da tag atual no mapa
                        mapaTags.inserir(tagName, (count == null) ? 1 : count + 1); //atualiza a contagem
                    }
                }
                texto.setLength(0); //limpa o sb para a proxima tag
                insideTag = false; //sai de dentro da tag
            } else {
                texto.append(ch); //add o caracter atual ao stringbuilder
            }
        }
        return pilha; //retorna a pilha
    }

    @SuppressWarnings("unchecked")
    private boolean isSingletonTag(String tagName) {
        NoLista<String> p = sTags.getPrimeiro(); //percorre a lista encadeada procurando a tag com info igual a procurada
        while (p != null) {
            if (p.getInfo().equals(tagName)) {
                return true;
            }
            p = p.getProximo();
        }
        return false;
    }

    private String acharTag(PilhaVetor<String> pilha, String tag) {
        PilhaVetor<String> pilhaLocal = new PilhaVetor<>(9999);
        pilhaLocal.concatenar(pilha); //cria uma nova pilha e copia os dados da pilha principal pra ela

        String top;
        while (!pilhaLocal.estaVazia()) {
            top = pilhaLocal.pop(); //remove o elemento do topo da pilha local
            System.out.println("Removendo da pilha local: " + top);

            if (getTagName(top).equals(tag)) { // se a tag do topo for igual a tag procurada
                System.out.println("Achou: " + top + " -> deve retirar até aqui");
                return top; //retornamos essa tag indicando que achamos
            } else if (top.startsWith("<") && !top.startsWith("</")) { //caso uma tag não correspondente seja encontrada 
                System.out.println("Tag de abertura inesperadamente encontrada: " + top);
                return "erro"; //retornamos erro
            }
        }
        return "erro";
    }

    private String getTagName(String tag) {
        int startTag = tag.indexOf("<") + 1; //pega o inicio da tag
        int endTag = tag.indexOf(" "); //verifica espaço em branco dentro da tag
        if (endTag == -1) { //verifica se n eh uma tag vazia
            endTag = tag.indexOf(">");
        }
        if (startTag == -1 || endTag == -1 || startTag >= endTag) { //verifica se n começa com "<", não termina com " " e se faz sentido
            return ""; //string vazia pois encontrou um erro
        }
        return tag.substring(startTag, endTag).replace("/", "").trim(); //retorna apenas o nome da tag ignorando " " e "/" 
    }
}

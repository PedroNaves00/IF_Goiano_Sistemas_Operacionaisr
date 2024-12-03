import java.util.*;

public class GerenciamentoMemoria {
    static final int TAMANHO_MEMORIA = 32;
    static int[] memoria = new int[TAMANHO_MEMORIA]; // Mapa de bits para a memória
    static Map<String, Integer> processos = new HashMap<>(); // Mapa para guardar tamanho dos processos
    static int ponteiroNextFit = 0; // Posição inicial para o algoritmo Next Fit

    public static void main(String[] args) {
        // Definição dos processos
        processos.put("P1", 5);
        processos.put("P2", 4);
        processos.put("P3", 2);
        processos.put("P4", 5);
        processos.put("P5", 8);
        processos.put("P6", 3);
        processos.put("P7", 5);
        processos.put("P8", 8);
        processos.put("P9", 2);
        processos.put("P10", 6);

        List<String> operacoes = gerarOperacoesAleatorias(30);

        for (String operacao : operacoes) {
            String idProcesso = operacao.split(":")[1];
            if (operacao.startsWith("alocar")) {
                alocar(idProcesso, "First Fit");
            } else if (operacao.startsWith("desalocar")) {
                desalocar(idProcesso);
            }
            exibirMemoria();
        }
    }

    // Função de alocação genérica para chamar o algoritmo adequado
    public static void alocar(String idProcesso, String algoritmo) {
        int tamanhoProcesso = processos.getOrDefault(idProcesso, 0);
        int indiceInicio = -1;

        switch (algoritmo) {
            case "First Fit":
                indiceInicio = firstFit(tamanhoProcesso);
                break;
            case "Next Fit":
                indiceInicio = nextFit(tamanhoProcesso);
                break;
            case "Best Fit":
                indiceInicio = bestFit(tamanhoProcesso);
                break;
            case "Worst Fit":
                indiceInicio = worstFit(tamanhoProcesso);
                break;
            case "Quick Fit":
                indiceInicio = quickFit(tamanhoProcesso);
                break;
        }

        if (indiceInicio >= 0) {
            for (int i = indiceInicio; i < indiceInicio + tamanhoProcesso; i++) {
                memoria[i] = 1;
            }
            System.out.println("Processo " + idProcesso + " alocado no bloco " + indiceInicio);
        } else {
            System.out.println("Erro: Processo " + idProcesso + " não pode ser alocado.");
        }
    }

    // Função para desalocar um processo
    public static void desalocar(String idProcesso) {
        int tamanhoProcesso = processos.getOrDefault(idProcesso, 0);
        boolean desalocado = false;

        for (int i = 0; i < TAMANHO_MEMORIA; i++) {
            int contadorBlocos = 0;

            // Verificar blocos ocupados consecutivos
            while (i < TAMANHO_MEMORIA && memoria[i] == 1) {
                contadorBlocos++;
                i++;
            }

            // Verificar se corresponde ao processo atual
            if (contadorBlocos == tamanhoProcesso) {
                for (int j = i - tamanhoProcesso; j < i; j++) {
                    memoria[j] = 0;
                }
                System.out.println("Processo " + idProcesso + " desalocado.");
                desalocado = true;
                break;
            }
        }

        if (!desalocado) {
            System.out.println("Erro: Processo " + idProcesso + " não encontrado na memória.");
        }
    }

    // Algoritmo First Fit
    public static int firstFit(int tamanho) {
        for (int i = 0; i <= TAMANHO_MEMORIA - tamanho; i++) {
            boolean podeAlocar = true;

            for (int j = i; j < i + tamanho; j++) {
                if (memoria[j] == 1) {
                    podeAlocar = false;
                    break;
                }
            }

            if (podeAlocar) {
                return i;
            }
        }
        return -1; // Falha na alocação
    }

    // Algoritmo Next Fit
    public static int nextFit(int tamanho) {
        int indiceInicio = ponteiroNextFit;
        int contador = 0;

        while (contador < TAMANHO_MEMORIA) {
            boolean podeAlocar = true;

            for (int i = 0; i < tamanho; i++) {
                int indice = (indiceInicio + i) % TAMANHO_MEMORIA;
                if (memoria[indice] == 1) {
                    podeAlocar = false;
                    break;
                }
            }

            if (podeAlocar) {
                ponteiroNextFit = (indiceInicio + tamanho) % TAMANHO_MEMORIA;
                return indiceInicio;
            }

            indiceInicio = (indiceInicio + 1) % TAMANHO_MEMORIA;
            contador++;
        }

        return -1; // Falha na alocação
    }

    // Algoritmo Best Fit
    public static int bestFit(int tamanho) {
        int melhorIndice = -1;
        int menorEspaco = Integer.MAX_VALUE;

        for (int i = 0; i < TAMANHO_MEMORIA; i++) {
            int tamanhoBloco = 0;

            while (i < TAMANHO_MEMORIA && memoria[i] == 0) {
                tamanhoBloco++;
                i++;
            }

            if (tamanhoBloco >= tamanho && tamanhoBloco < menorEspaco) {
                melhorIndice = i - tamanhoBloco;
                menorEspaco = tamanhoBloco;
            }
        }

        return melhorIndice;
    }

    // Algoritmo Worst Fit
    public static int worstFit(int tamanho) {
        int piorIndice = -1;
        int maiorEspaco = -1;

        for (int i = 0; i < TAMANHO_MEMORIA; i++) {
            int tamanhoBloco = 0;

            while (i < TAMANHO_MEMORIA && memoria[i] == 0) {
                tamanhoBloco++;
                i++;
            }

            if (tamanhoBloco >= tamanho && tamanhoBloco > maiorEspaco) {
                piorIndice = i - tamanhoBloco;
                maiorEspaco = tamanhoBloco;
            }
        }

        return piorIndice;
    }

    // Algoritmo Quick Fit (Simples, agrupado por tamanho fixo)
    public static int quickFit(int tamanho) {
        return firstFit(tamanho); // Aqui pode-se usar mapeamento de tamanhos fixos
    }

    // Geração de operações aleatórias
    public static List<String> gerarOperacoesAleatorias(int quantidade) {
        List<String> operacoes = new ArrayList<>();
        List<String> idsProcessos = new ArrayList<>(processos.keySet());
        Random random = new Random();

        for (int i = 0; i < quantidade; i++) {
            String operacao = random.nextBoolean() ? "alocar" : "desalocar";
            String idProcesso = idsProcessos.get(random.nextInt(idsProcessos.size()));
            operacoes.add(operacao + ":" + idProcesso);
        }

        return operacoes;
    }

    // Exibição do estado da memória
    public static void exibirMemoria() {
        System.out.println(Arrays.toString(memoria));
    }
}

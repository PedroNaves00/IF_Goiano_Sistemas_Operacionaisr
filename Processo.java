import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Processo {
    // Atributos do processo
    private int pid;
    private int tempoExecucao;
    private int contadorPrograma;
    private String estado;
    private int nES;  // Número de vezes que realizou E/S
    private int nCPU; // Número de vezes que usou a CPU

    // Tempo de execução máximo do processo, fornecido pela tabela
    private final int tempoTotalExecucao;



    // Construtor
    public Processo(int pid, int tempoTotalExecucao) {
        this.pid = pid;
        this.tempoExecucao = 0;  // Inicialmente, o processo não executou nenhum ciclo
        this.contadorPrograma = 1;  // CP começa com 1
        this.estado = "PRONTO";  // Estado inicial
        this.nES = 0;
        this.nCPU = 0;
        this.tempoTotalExecucao = tempoTotalExecucao;
    }

    // Método para simular um ciclo de execução
    public void executarCiclo() {
        tempoExecucao++;
        contadorPrograma = tempoExecucao + 1;  // Atualiza CP como TP + 1
    }

    // Métodos getter e setter
    public int getPid() { return pid; }
    public int getTempoExecucao() { return tempoExecucao; }
    public int getContadorPrograma() { return contadorPrograma; }
    public String getEstado() { return estado; }
    public int getnES() { return nES; }
    public int getnCPU() { return nCPU; }
    public void setEstado(String novoEstado) { estado = novoEstado; }
    public void incrementarES() { nES++; }
    public void incrementarCPU() { nCPU++; }

    // Verifica se o processo terminou
    public boolean terminou() {
        return tempoExecucao >= tempoTotalExecucao;
    }

    // Método para registrar dados no arquivo (simulando uma Tabela de Processos)
    public void registrarTabela(String acao) {
        try (FileWriter writer = new FileWriter("tabela_processos.txt", true)) {
            writer.write(String.format("PID: %d | TP: %d | CP: %d | EP: %s | NES: %d | N_CPU: %d | Ação: %s%n",
                    pid, tempoExecucao, contadorPrograma, estado, nES, nCPU, acao));
        } catch (IOException e) {
            System.err.println("Erro ao escrever na tabela de processos: " + e.getMessage());
        }
    }

    // Método para imprimir dados do processo
    public void imprimirDados() {
        System.out.printf("PID: %d | TP: %d | CP: %d | EP: %s | NES: %d | N_CPU: %d%n",
                pid, tempoExecucao, contadorPrograma, estado, nES, nCPU);
    }
}

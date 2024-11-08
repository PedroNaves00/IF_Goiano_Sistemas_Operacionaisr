import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimuladorSO {
    private List<Processo> processos;
    private static final int QUANTUM = 1000;
    private Random random;

    // Inicializa os processos com seus tempos de execução
    public SimuladorSO() {
        processos = new ArrayList<>();
        processos.add(new Processo(0, 10000));
        processos.add(new Processo(1, 5000));
        processos.add(new Processo(2, 7000));
        processos.add(new Processo(3, 3000));
        processos.add(new Processo(4, 3000));
        processos.add(new Processo(5, 8000));
        processos.add(new Processo(6, 2000));
        processos.add(new Processo(7, 5000));
        processos.add(new Processo(8, 4000));
        processos.add(new Processo(9, 10000));
        random = new Random();
    }

    private boolean DesbloquearProcesso() {
        return random.nextInt(100) < 30; // 30% de chance de retornar true
    }

    // Método principal para rodar a simulação
    public void rodar() {
        for (Processo processo : processos) {
            while (!processo.terminou()) {

                while (processo.getEstado().equals("BLOQUEADO")) {
                    if (DesbloquearProcesso()) {
                        processo.setEstado("PRONTO");
                        processo.registrarTabela("BLOQUEADO >>> PRONTO");
                    } else {
                        break;
                    }
                }

                processo.setEstado("EXECUTANDO");
                processo.incrementarCPU();
                processo.registrarTabela("PRONTO >>> EXECUTANDO");

                for (int i = 0; i < QUANTUM; i++) {
                    if (processo.terminou()) break;
                    processo.executarCiclo();

                    // Chance de 1% de realizar E/S
                    if (random.nextInt(100) < 1) {
                        processo.setEstado("BLOQUEADO");
                        processo.incrementarES();
                        processo.registrarTabela("EXECUTANDO >>> BLOQUEADO");
                        break;
                    }
                }

                if (!processo.terminou() && !processo.getEstado().equals("BLOQUEADO")) {
                    processo.setEstado("PRONTO");
                    processo.registrarTabela("EXECUTANDO >>> PRONTO");
                }
            }
            // Processo terminou
            processo.setEstado("TERMINADO");
            processo.imprimirDados();
        }
    }

    public static void main(String[] args) {
        SimuladorSO simulador = new SimuladorSO();
        simulador.rodar();
    }
}

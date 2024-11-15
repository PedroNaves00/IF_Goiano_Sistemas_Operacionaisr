import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrocaDeMensagens {
    private static final BlockingQueue<String> filaLeitura = new LinkedBlockingQueue<>();
    private static final BlockingQueue<String> filaEscrita = new LinkedBlockingQueue<>();
    private static boolean escrevendo = false;

    public static void main(String[] args) {
        Thread[] threads = new Thread[10];

        // Criando 5 threads de leitores
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Leitor(), "Leitor-" + (i + 1));
        }

        // Criando 5 threads de escritores
        for (int i = 5; i < 10; i++) {
            threads[i] = new Thread(new Escritor(), "Escritor-" + (i - 4));
        }

        // Thread que processa as mensagens
        Thread gerenciador = new Thread(() -> {
            while (true) {
                try {
                    if (!escrevendo) {
                        // Processa mensagens de leitura se não há escrita em andamento
                        while (!filaLeitura.isEmpty()) {
                            String mensagem = filaLeitura.take();
                            System.out.println(mensagem);
                        }
                    }

                    if (!filaEscrita.isEmpty()) {
                        escrevendo = true;
                        // Processa a mensagem de escrita
                        String mensagem = filaEscrita.take();
                        System.out.println(mensagem);
                        escrevendo = false;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Gerenciador interrompido.");
                    break;
                }
            }
        });

        // Inicia a thread gerenciadora
        gerenciador.setDaemon(true); // Torna o gerenciador um thread daemon
        gerenciador.start();

        // Iniciando as threads de leitores e escritores
        for (Thread thread : threads) {
            thread.start();
        }

        // Aguardando todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrompida: " + thread.getName());
            }
        }
        System.out.println("Simulação finalizada!");
    }

    static class Leitor implements Runnable {
        @Override
        public void run() {
            try {
                // Simulação de leitura
                Thread.sleep((int) (Math.random() * 1000));
                filaLeitura.put(Thread.currentThread().getName() + " está lendo...");
                Thread.sleep((int) (Math.random() * 1000));
                filaLeitura.put(Thread.currentThread().getName() + " terminou de ler.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }

    static class Escritor implements Runnable {
        @Override
        public void run() {
            try {
                // Simulação de escrita
                Thread.sleep((int) (Math.random() * 1000));
                filaEscrita.put(Thread.currentThread().getName() + " está escrevendo...");
                Thread.sleep((int) (Math.random() * 1000));
                filaEscrita.put(Thread.currentThread().getName() + " terminou de escrever.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }
}

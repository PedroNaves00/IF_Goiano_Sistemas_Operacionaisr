import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Barreiras {
    private static int leitoresAtivos = 0; // Número de leitores ativos
    private static boolean escrevendo = false; // Indica se há um escritor ativo
    private static final Object lock = new Object(); // Lock para sincronização
    private static final CyclicBarrier barreiraLeitura = new CyclicBarrier(5); // Barreiras para leitores
    private static final CyclicBarrier barreiraEscrita = new CyclicBarrier(1); // Barreiras para escritores

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

        // Iniciando as threads
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
                // Espera na barreira de leitura
                barreiraLeitura.await();

                synchronized (lock) {
                    while (escrevendo) { // Leitores aguardam enquanto há escritores
                        lock.wait();
                    }
                    leitoresAtivos++;
                }

                // Simula leitura
                System.out.println(Thread.currentThread().getName() + " está lendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de leitura

                synchronized (lock) {
                    leitoresAtivos--;
                    if (leitoresAtivos == 0) {
                        lock.notifyAll(); // Notifica escritores quando não há mais leitores
                    }
                }

                System.out.println(Thread.currentThread().getName() + " terminou de ler.");
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }

    static class Escritor implements Runnable {
        @Override
        public void run() {
            try {
                // Espera na barreira de escrita
                barreiraEscrita.await();

                synchronized (lock) {
                    while (escrevendo || leitoresAtivos > 0) { // Escritores aguardam leitores ou outros escritores
                        lock.wait();
                    }
                    escrevendo = true;
                }

                // Simula escrita
                System.out.println(Thread.currentThread().getName() + " está escrevendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de escrita

                synchronized (lock) {
                    escrevendo = false;
                    lock.notifyAll(); // Notifica leitores e escritores
                }

                System.out.println(Thread.currentThread().getName() + " terminou de escrever.");
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }
}

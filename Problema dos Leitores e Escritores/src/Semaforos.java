import java.util.concurrent.Semaphore;

public class Semaforos {
    private static final Semaphore mutex = new Semaphore(1);
    private static final Semaphore writeLock = new Semaphore(1);
    private static int leitores = 0;

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
                mutex.acquire();
                leitores++;
                if (leitores == 1) writeLock.acquire();
                mutex.release();

                // Seção crítica (leitura)
                System.out.println(Thread.currentThread().getName() + " está lendo...");
                Thread.sleep(100);

                mutex.acquire();
                leitores--;
                if (leitores == 0) writeLock.release();
                mutex.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Escritor implements Runnable {
        @Override
        public void run() {
            try {
                writeLock.acquire();

                // Seção crítica (escrita)
                System.out.println(Thread.currentThread().getName() + " está escrevendo...");
                Thread.sleep(100);

                writeLock.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


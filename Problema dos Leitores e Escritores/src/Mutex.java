public class Mutex {
    private int leitores = 0;
    private boolean escrevendo = false;

    public static void main(String[] args) {
        Mutex mutex = new Mutex();
        Thread[] threads = new Thread[10];

        // Criando 5 threads de leitores
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Leitor(mutex), "Leitor-" + (i + 1));
        }

        // Criando 5 threads de escritores
        for (int i = 5; i < 10; i++) {
            threads[i] = new Thread(new Escritor(mutex), "Escritor-" + (i - 4));
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

    // Métodos para gerenciar o acesso à seção crítica
    public synchronized void iniciarLeitura() throws InterruptedException {
        while (escrevendo) {
            wait();
        }
        leitores++;
    }

    public synchronized void finalizarLeitura() {
        leitores--;
        if (leitores == 0) {
            notifyAll();
        }
    }

    public synchronized void iniciarEscrita() throws InterruptedException {
        while (escrevendo || leitores > 0) {
            wait();
        }
        escrevendo = true;
    }

    public synchronized void finalizarEscrita() {
        escrevendo = false;
        notifyAll();
    }

    // Classes de Leitor e Escritor
    static class Leitor implements Runnable {
        private final Mutex mutex;

        public Leitor(Mutex mutex) {
            this.mutex = mutex;
        }

        @Override
        public void run() {
            try {
                mutex.iniciarLeitura();
                // Simulação da leitura
                System.out.println(Thread.currentThread().getName() + " está lendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de leitura
                System.out.println(Thread.currentThread().getName() + " terminou de ler.");
                mutex.finalizarLeitura();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }

    static class Escritor implements Runnable {
        private final Mutex mutex;

        public Escritor(Mutex mutex) {
            this.mutex = mutex;
        }

        @Override
        public void run() {
            try {
                mutex.iniciarEscrita();
                // Simulação da escrita
                System.out.println(Thread.currentThread().getName() + " está escrevendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de escrita
                System.out.println(Thread.currentThread().getName() + " terminou de escrever.");
                mutex.finalizarEscrita();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }
}

public class Monitores {
    private int leitores = 0;        // Contador de leitores ativos
    private boolean escrevendo = false; // Indica se há um escritor ativo

    public synchronized void iniciarLeitura() throws InterruptedException {
        while (escrevendo) { // Leitores aguardam enquanto há escritores
            wait();
        }
        leitores++;
    }

    public synchronized void finalizarLeitura() {
        leitores--;
        if (leitores == 0) { // Notifica escritores quando o último leitor sair
            notifyAll();
        }
    }

    public synchronized void iniciarEscrita() throws InterruptedException {
        while (escrevendo || leitores > 0) { // Escritores aguardam enquanto há leitores ou outros escritores
            wait();
        }
        escrevendo = true;
    }

    public synchronized void finalizarEscrita() {
        escrevendo = false;
        notifyAll(); // Notifica leitores e escritores
    }

    public static void main(String[] args) {
        Monitores monitor = new Monitores();
        Thread[] threads = new Thread[10];

        // Criando 5 threads de leitores
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Leitor(monitor), "Leitor-" + (i + 1));
        }

        // Criando 5 threads de escritores
        for (int i = 5; i < 10; i++) {
            threads[i] = new Thread(new Escritor(monitor), "Escritor-" + (i - 4));
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

    // Classes de Leitor e Escritor
    static class Leitor implements Runnable {
        private final Monitores monitor;

        public Leitor(Monitores monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            try {
                monitor.iniciarLeitura();
                // Simulação da leitura
                System.out.println(Thread.currentThread().getName() + " está lendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de leitura
                System.out.println(Thread.currentThread().getName() + " terminou de ler.");
                monitor.finalizarLeitura();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }

    static class Escritor implements Runnable {
        private final Monitores monitor;

        public Escritor(Monitores monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            try {
                monitor.iniciarEscrita();
                // Simulação da escrita
                System.out.println(Thread.currentThread().getName() + " está escrevendo...");
                Thread.sleep((int) (Math.random() * 1000)); // Simula tempo de escrita
                System.out.println(Thread.currentThread().getName() + " terminou de escrever.");
                monitor.finalizarEscrita();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " foi interrompido.");
            }
        }
    }
}

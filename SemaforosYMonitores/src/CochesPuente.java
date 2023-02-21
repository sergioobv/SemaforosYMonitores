import java.util.Random;
import java.util.concurrent.locks.*;

public class CochesPuente {

    private static final int NUM_COCHES = 10;
    private static final int NUM_COCHES_SUR = 5;
    private static final int NUM_COCHES_NORTE = NUM_COCHES - NUM_COCHES_SUR;

    public static void main(String[] args) {
        Puente puente = new Puente();
        Thread[] coches = new Thread[NUM_COCHES];

        for (int i = 0; i < NUM_COCHES_SUR; i++) {
            coches[i] = new Thread(new CocheSur(puente));
        }

        for (int i = NUM_COCHES_SUR; i < NUM_COCHES; i++) {
            coches[i] = new Thread(new CocheNorte(puente));
        }

        for (int i = 0; i < NUM_COCHES; i++) {
            coches[i].start();
        }

        try {
            for (int i = 0; i < NUM_COCHES; i++) {
                coches[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupción en la espera de los coches.");
        }

        System.out.println("Todos los coches han cruzado el puente.");
    }

    private static class CocheSur implements Runnable {

        private static final Random rand = new Random();
        private Puente puente;

        public CocheSur(Puente puente) {
            this.puente = puente;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(rand.nextInt(1000));
                System.out.println("Coche sur quiere cruzar el puente.");
                puente.entrarCocheSur();
                System.out.println("Coche sur cruzando el puente.");
                Thread.sleep(rand.nextInt(1000));
                puente.salirCocheSur();
                System.out.println("Coche sur ha salido del puente.");
            } catch (InterruptedException e) {
                System.out.println("Interrupción del coche sur.");
            }
        }
    }

    private static class CocheNorte implements Runnable {

        private static final Random rand = new Random();
        private Puente puente;

        public CocheNorte(Puente puente) {
            this.puente = puente;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(rand.nextInt(1000));
                System.out.println("Coche norte quiere cruzar el puente.");
                puente.entrarCocheNorte();
                System.out.println("Coche norte cruzando el puente.");
                Thread.sleep(rand.nextInt(1000));
                puente.salirCocheNorte();
                System.out.println("Coche norte ha salido del puente.");
            } catch (InterruptedException e) {
                System.out.println("Interrupción del coche norte.");
            }
        }
    }

    private static class Puente {

        private int numCochesSur;
        private int numCochesNorte;

        private Lock lock;
        private Condition condSur;
        private Condition condNorte;

        public Puente() {
            numCochesSur = 0;
            numCochesNorte = 0;

            lock = new ReentrantLock();
            condSur = lock.newCondition();
            condNorte = lock.newCondition();
        }

        public void entrarCocheSur() throws InterruptedException {
            lock.lock();
            try {
                while (numCochesNorte > 0) {
                    condSur.await();
                }
                numCochesSur++;
            } finally {
                lock.unlock();
            }
        }

        public void salirCocheSur() {
            lock.lock();
            try {
                numCochesSur--;
                if (numCochesSur == 0) {
                    condNorte.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        public void entrarCocheNorte() throws InterruptedException {
            lock.lock();
            try {
                while (numCochesSur > 0) {
                    condNorte.await();
                }
                numCochesNorte++;
            } finally {
                lock.unlock();
            }
        }

        public void salirCocheNorte() {
            lock.lock();
            try {
                numCochesNorte--;
                if (numCochesNorte == 0) {
                    condSur.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}




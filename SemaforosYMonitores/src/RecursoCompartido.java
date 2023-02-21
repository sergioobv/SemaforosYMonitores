import java.util.concurrent.Semaphore;

public class RecursoCompartido {
    private Semaphore semaforoDisponible;
    private Semaphore semaforoAsignacion;
    private int unidadesDisponibles;
    private int[] asignacion;

    public RecursoCompartido(int k, int n) {
        semaforoDisponible = new Semaphore(k, true);
        semaforoAsignacion = new Semaphore(1, true);
        unidadesDisponibles = k;
        asignacion = new int[n];
    }

    public void reserva(int proceso, int unidades) throws InterruptedException {
        semaforoDisponible.acquire(unidades);
        semaforoAsignacion.acquire();
        unidadesDisponibles -= unidades;
        asignacion[proceso] += unidades;
        semaforoAsignacion.release();
    }

    public void liberacion(int proceso, int unidades) throws InterruptedException {
        semaforoAsignacion.acquire();
        unidadesDisponibles += unidades;
        asignacion[proceso] -= unidades;
        semaforoAsignacion.release();
        semaforoDisponible.release(unidades);
    }

    public static void main(String[] args) {
        final int NUM_PROCESOS = 3;
        final int UNIDADES_MAXIMAS = 5;
        RecursoCompartido recurso = new RecursoCompartido(UNIDADES_MAXIMAS, NUM_PROCESOS);

     
        Thread[] procesos = new Thread[NUM_PROCESOS];
        for (int i = 0; i < NUM_PROCESOS; i++) {
            final int proceso = i;
            procesos[i] = new Thread(() -> {
                try {
                   
                    int unidades = (int) (Math.random() * UNIDADES_MAXIMAS) + 1;
                    System.out.println("Proceso " + proceso + " intenta reservar " + unidades + " unidades.");
                    recurso.reserva(proceso, unidades);
                    System.out.println("Proceso " + proceso + " ha reservado " + unidades + " unidades.");
                    Thread.sleep((int) (Math.random() * 1000));
                    recurso.liberacion(proceso, unidades);
                    System.out.println("Proceso " + proceso + " ha liberado " + unidades + " unidades.");
                } catch (InterruptedException e) {
                    System.out.println("Interrupción del proceso " + proceso + ".");
                }
            });
        }

      
        for (int i = 0; i < NUM_PROCESOS; i++) {
            procesos[i].start();
        }

        
        for (int i = 0; i < NUM_PROCESOS; i++) {
            try {
                procesos[i].join();
            } catch (InterruptedException e) {
                System.out.println("Interrupción en la espera del proceso " + i + ".");
            }
        }

        System.out.println("Todos los procesos han terminado.");
    }
}
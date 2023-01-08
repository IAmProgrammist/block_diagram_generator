package rchat.info.blockdiagramgenerator;

public class DebounceDecorator {
    private final Runnable runnable;
    private Timeout previousRun;
    private final long delay;

    private static class Timeout {
        private boolean shouldRun = true;

        public static Timeout setTimeout(Runnable runnable, long delay) {
            Timeout t = new Timeout();
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    if (t.shouldRun) {
                        runnable.run();
                    }
                }
                catch (Exception e){
                    System.err.println(e);
                }
            }).start();
            return t;
        }

        public void clearTimeout() {
            this.shouldRun = false;
        }
    }
    public DebounceDecorator(Runnable runnable, long delay) {
        this.runnable = runnable;
        this.delay = delay;
    }

    public void execute() {
        previousRun.clearTimeout();
        previousRun = Timeout.setTimeout(runnable, delay);
    }

    public void discardChanges() {
        previousRun.clearTimeout();
    }
}

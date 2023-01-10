package rchat.info.blockdiagramgenerator;

import java.util.Stack;

public class History<T> extends Stack<History.Cloneable<T>>{
    public interface Cloneable<E> {
        public E clone();
    }
    public History(History.Cloneable<T> initValue) {
        this.add(initValue);
    }
    int currentIndex = 0;
    public synchronized T prev() {
        currentIndex = Math.max(0, currentIndex - 1);
        return this.get(currentIndex).clone();
    }

    public synchronized T next() {
        currentIndex = Math.min(this.size() - 1, currentIndex + 1);
        return this.get(currentIndex).clone();
    }

    public synchronized T pushElement(History.Cloneable<T> element) {
        if (++currentIndex >= this.size()) {
            this.add(element);
        } else {
            this.set(currentIndex, element);
            for (int i = currentIndex + 1; i < this.size(); i++) {
                this.remove(currentIndex + 1);
            }
        }
        return element.clone();
    }
}

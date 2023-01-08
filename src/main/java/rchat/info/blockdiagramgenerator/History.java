package rchat.info.blockdiagramgenerator;

import java.util.List;
import java.util.Stack;

public class History<T> extends Stack<T>{
    int currentIndex = 0;
    public synchronized T prev() {
        currentIndex = Math.max(0, currentIndex - 1);
        return this.get(currentIndex);
    }

    public synchronized T next() {
        currentIndex = Math.min(this.size() - 1, currentIndex + 1);
        return this.get(currentIndex);
    }

    @Override
    public synchronized T push(T element) {
        this.removeRange(currentIndex + 1, this.size() - 1);
        this.add(element);
        currentIndex++;
        return element;
    }
}

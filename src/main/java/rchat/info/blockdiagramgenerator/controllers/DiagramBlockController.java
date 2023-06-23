package rchat.info.blockdiagramgenerator.controllers;

import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

public abstract class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    AbstractPainter gc;

    public DiagramBlockController(DiagramBlockModel model, DiagramBlockView view, AbstractPainter gc) {
        this.model = model;
        this.view = view;
        this.gc = gc;
    }

    public abstract Style getCurrentStyle();

    public abstract double canvasMousePosX();

    public abstract double canvasMousePosY();
    public abstract boolean isViewportMode();
    public abstract boolean isDragMode();

    public void setCanvasScale(double newScale) {
        model.canvasScale = newScale;

        model.basicFont = Font.font(getCurrentStyle().getFontBasicName(),
                model.canvasScale * getCurrentStyle().getFontBasicSize());
    }

    public void repaint(Dimension2D size) {
        view.repaint(gc, size, isViewportMode(), getCurrentStyle());
    }
}

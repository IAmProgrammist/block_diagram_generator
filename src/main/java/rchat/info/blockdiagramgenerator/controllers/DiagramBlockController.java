package rchat.info.blockdiagramgenerator.controllers;

import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.Style;
import rchat.info.blockdiagramgenerator.painter.AbstractPainter;
import rchat.info.blockdiagramgenerator.views.DiagramBlockView;

public abstract class DiagramBlockController {
    public DiagramBlockModel model;
    public DiagramBlockView view;
    AbstractPainter gc;

    public abstract Style getCurrentStyle();
}

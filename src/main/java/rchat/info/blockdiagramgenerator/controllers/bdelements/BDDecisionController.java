package rchat.info.blockdiagramgenerator.controllers.bdelements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import javafx.util.StringConverter;
import rchat.info.blockdiagramgenerator.Main;
import rchat.info.blockdiagramgenerator.Utils;
import rchat.info.blockdiagramgenerator.controllers.DiagramBlockController;
import rchat.info.blockdiagramgenerator.models.DiagramBlockModel;
import rchat.info.blockdiagramgenerator.models.bdelements.*;
import rchat.info.blockdiagramgenerator.views.bdelements.BDDecisionView;

import java.util.*;
import java.util.stream.Collectors;

import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.basicFont;
import static rchat.info.blockdiagramgenerator.models.DiagramBlockModel.onDataUpdate;

public class BDDecisionController extends BDElementController implements TextEditable, Container {
    public BDDecisionModel model;
    public BDDecisionView view;

    public BDDecisionController(BDContainerController positive, BDDecisionModel.Branch positiveBranch,
                                BDContainerController negative, BDDecisionModel.Branch negativeBranch,
                                String content) {
        this.model = new BDDecisionModel(positive, positiveBranch, negative, negativeBranch, content);
        this.model.positive.setParentContainer(this);
        this.model.negative.setParentContainer(this);
        this.view = new BDDecisionView(this.model);
        this.setControls();
        recalculateSizes();
    }

    public BDDecisionController(BDContainerController positive, BDDecisionModel.Branch positiveBranch,
                                BDContainerController negative, BDDecisionModel.Branch negativeBranch,
                                String content, boolean selected) {
        this.model = new BDDecisionModel(positive, positiveBranch, negative, negativeBranch, content);
        this.view = new BDDecisionView(this.model);
        this.model.positive.setParentContainer(this);
        this.model.negative.setParentContainer(this);
        this.selected = selected;
        this.setControls();
        recalculateSizes();
    }

    @Override
    public void setControls() {
        this.controllings = new ArrayList<>();
        Label header = new Label(Main.rb.getString("text"));
        controllings.add(header);
        TextArea area = new TextArea(getText());
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });
        controllings.add(area);
        controllings.add(new Separator());
        Label headerDecPos = new Label(Main.rb.getString("dec_positive_pos"));
        controllings.add(headerDecPos);
        StringConverter<BDDecisionModel.Branch> converter = new StringConverter<>() {
            @Override
            public String toString(BDDecisionModel.Branch object) {
                if (object == null) return "";
                return Main.rb.getString(object.propName);
            }

            @Override
            public BDDecisionModel.Branch fromString(String string) {
                if (string.equals(Main.rb.getString(BDDecisionModel.Branch.LEFT.propName)))
                    return BDDecisionModel.Branch.LEFT;
                if (string.equals(Main.rb.getString(BDDecisionModel.Branch.CENTER.propName)))
                    return BDDecisionModel.Branch.CENTER;
                if (string.equals(Main.rb.getString(BDDecisionModel.Branch.RIGHT.propName)))
                    return BDDecisionModel.Branch.RIGHT;
                return null;
            }
        };
        ComboBox<BDDecisionModel.Branch> posComboBox = new ComboBox<>();
        posComboBox.setItems(FXCollections.observableArrayList(Arrays.stream(BDDecisionModel.Branch.values())
                .filter((el) -> el != model.negativeBranch).collect(Collectors.toList())));
        posComboBox.setConverter(converter);
        posComboBox.valueProperty().setValue(model.positiveBranch);
        controllings.add(posComboBox);

        controllings.add(new Separator());
        Label headerDecNeg = new Label(Main.rb.getString("dec_negative_pos"));
        controllings.add(headerDecNeg);

        ComboBox<BDDecisionModel.Branch> negComboBox = new ComboBox<>();
        negComboBox.setItems(FXCollections.observableArrayList(Arrays.stream(BDDecisionModel.Branch.values())
                .filter((el) -> el != model.positiveBranch).collect(Collectors.toList())));
        negComboBox.setConverter(converter);
        negComboBox.valueProperty().setValue(model.negativeBranch);
        negComboBox.setEditable(false);

        posComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            model.positiveBranch = newValue;
            negComboBox.setItems(FXCollections.observableArrayList(Arrays.stream(BDDecisionModel.Branch.values())
                    .filter((el) -> el != model.positiveBranch).collect(Collectors.toList())));
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });

        negComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            model.negativeBranch = newValue;
            posComboBox.setItems(FXCollections.observableArrayList(Arrays.stream(BDDecisionModel.Branch.values())
                    .filter((el) -> el != model.negativeBranch).collect(Collectors.toList())));
            if (DiagramBlockModel.onDataUpdate != null) onDataUpdate.run();
        });
        controllings.add(negComboBox);
        controllings.add(new Separator());
    }

    @Override
    public void update(GraphicsContext gc, Pair<Double, Double> position, double scale) {
        view.repaint(gc, position, isMouseInElement(position), selected, scale);
    }

    @Override
    public BDElementController select(Pair<Double, Double> drawPoint) {
        BDElementController selected = null;
        Dimension2D rhombusSize = model.getRhombusSize();
        double rhombusWidth = rhombusSize.getWidth();

        drawPoint = new Pair<>(drawPoint.getKey() + (model.getDistanceToLeftBound() - rhombusWidth / 2), drawPoint.getValue());


        if (model.positiveBranch == BDDecisionModel.Branch.CENTER || model.negativeBranch == BDDecisionModel.Branch.CENTER) {
            BDContainerController centerBranch;
            BDContainerController restContainer;
            BDDecisionModel.Branch restBranch;
            if (model.positiveBranch == BDDecisionModel.Branch.CENTER) {
                centerBranch = model.positive;
                restContainer = model.negative;
                restBranch = model.negativeBranch;
            } else {
                centerBranch = model.negative;
                restContainer = model.positive;
                restBranch = model.positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            selected = centerBranch.select(trueCenterDrawPoint);
            if (selected != null) {
                this.selected = false;
                return selected;
            }

            if (restBranch == BDDecisionModel.Branch.LEFT) {
                double restShoulderLen = centerBranch.getModel().getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getModel().getSize().getWidth() - rhombusWidth / 2;

                Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                }
                selected = restContainer.select(drawRestPoint);
            } else {
                double restShoulderLen = centerBranch.getModel().getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        - rhombusWidth / 2;

                Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                }
                selected = restContainer.select(drawRestPoint);
            }
        } else {
            Dimension2D negativeSize = model.negative.getModel().getSize();
            Dimension2D positiveSize = model.positive.getModel().getSize();
            Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

            if (model.negativeBranch == BDDecisionModel.Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                }

                selected = model.negative.select(drawNegativePoint);
                if (selected != null) {
                    this.selected = false;
                    return selected;
                }


                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                }


                selected = model.positive.select(drawPositivePoint);

            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                }
                selected = model.positive.select(drawPositivePoint);
                if (selected != null) {
                    this.selected = false;
                    return selected;
                }
                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                }
                selected = model.negative.select(drawNegativePoint);

            }
        }

        if (selected != null) {
            this.selected = false;
            return selected;
        }

        return super.select(new Pair<>(drawPoint.getKey() - (model.getDistanceToLeftBound() - rhombusWidth / 2), drawPoint.getValue()));
    }

    @Override
    public void recalculateSizes() {
        this.model.positive.recalculateSizes();
        this.model.negative.recalculateSizes();
        double maxLineLen = 0;
        Font basicFont = new Font(DiagramBlockModel.FONT_BASIC_NAME, DiagramBlockModel.FONT_BASIC_SIZE);
        List<String> dataLines = getModel().getDataLines();
        double textHeight = dataLines.size() == 0 ? Utils.computeTextWidth(basicFont, "").getHeight() : 0;
        for (String line : dataLines) {
            Dimension2D d = Utils.computeTextWidth(basicFont, line);
            if (d.getWidth() > maxLineLen) {
                maxLineLen = d.getWidth();
            }
            textHeight += d.getHeight() + DiagramBlockModel.LINE_SPACING;
        }
        textHeight -= DiagramBlockModel.LINE_SPACING;
        double textWidth = maxLineLen + 2 * DiagramBlockModel.TEXT_PADDING;
        double diag = textHeight + textWidth / 2;

        Dimension2D rhombusSize = new Dimension2D(diag * 2, diag);
        Dimension2D rhombusTextSize = new Dimension2D(textWidth, textHeight);
        this.model.rhombusSize = rhombusSize;
        this.model.rhombusTextSize = rhombusTextSize;
        double rhombusWidth = rhombusSize.getWidth();
        Pair<Double, Double> drawPoint = new Pair<>(0.0, 0.0);
        double leftPoint;
        double leftBound;
        double rightPoint;
        double rightBound;
        Pair<Double, Double> topConnector = model.getTopRhombusConnector(drawPoint);

        if (model.positiveBranch == BDDecisionModel.Branch.CENTER || model.negativeBranch == BDDecisionModel.Branch.CENTER) {
            BDContainerController centerBranch;
            BDContainerController restContainer;
            BDDecisionModel.Branch restBranch;
            if (model.positiveBranch == BDDecisionModel.Branch.CENTER) {
                centerBranch = model.positive;
                restContainer = model.negative;
                restBranch = model.negativeBranch;
            } else {
                centerBranch = model.negative;
                restContainer = model.positive;
                restBranch = model.positiveBranch;
            }
            Pair<Double, Double> bottomRhombusConnector = model.getBottomRhombusConnector(drawPoint);
            Pair<Double, Double> fakeDrawPoint = centerBranch.getModel().getTopConnector(new Pair<>(bottomRhombusConnector.getKey(), bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING));
            Pair<Double, Double> trueCenterDrawPoint = new Pair<>(bottomRhombusConnector.getKey() - (fakeDrawPoint.getKey() - bottomRhombusConnector.getKey()),
                    bottomRhombusConnector.getValue() + DiagramBlockModel.ELEMENTS_SPACING);
            leftPoint = Math.min(trueCenterDrawPoint.getKey(), model.getLeftRhombusConnector(drawPoint).getKey());
            rightPoint = Math.max(trueCenterDrawPoint.getKey() + centerBranch.getModel().getSize().getWidth(),
                    model.getRightRhombusConnector(drawPoint).getKey());

            if (restBranch == BDDecisionModel.Branch.LEFT) {
                double restShoulderLen = centerBranch.getModel().getDistanceToLeftBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        + restContainer.getModel().getSize().getWidth() - rhombusWidth / 2;

                Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusLeftConnector.getKey() - restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (rhombusLeftConnector.getKey() - restConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + restConnector.getKey()),
                            drawRestPoint.getValue());
                }

                leftPoint = drawRestPoint.getKey();
            } else {
                double restShoulderLen = centerBranch.getModel().getDistanceToRightBound() + DiagramBlockModel.DECISION_BLOCKS_PADDING
                        - rhombusWidth / 2;

                Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

                Pair<Double, Double> drawRestPoint = new Pair<>(rhombusRightConnector.getKey() + restShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> restConnector = restContainer.getModel().getTopConnector(drawRestPoint);
                if (restConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawRestPoint = new Pair<>(drawRestPoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - restConnector.getKey() + rhombusRightConnector.getKey()),
                            drawRestPoint.getValue());
                }

                rightPoint = drawRestPoint.getKey() + restContainer.getModel().getSize().getWidth();
            }

            // Left bound
            leftBound = Math.abs(topConnector.getKey() - leftPoint);
            // Right bound
            rightBound = Math.abs(topConnector.getKey() - rightPoint);
        } else {
            Dimension2D negativeSize = model.negative.getModel().getSize();
            Dimension2D positiveSize = model.positive.getModel().getSize();
            Pair<Double, Double> rhombusLeftConnector = model.getLeftRhombusConnector(drawPoint);
            Pair<Double, Double> rhombusRightConnector = model.getRightRhombusConnector(drawPoint);

            if (model.negativeBranch == BDDecisionModel.Branch.LEFT) {
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + negativeSize.getWidth() - rhombusWidth / 2;
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusLeftConnector.getKey() - negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (rhombusLeftConnector.getKey() - negConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + negConnector.getKey()),
                            drawNegativePoint.getValue());
                }
                leftPoint = drawNegativePoint.getKey();

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusRightConnector.getKey() + positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (posConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (posConnector.getKey() - rhombusRightConnector.getKey())),
                            drawPositivePoint.getValue());
                }
                rightPoint = drawPositivePoint.getKey() + model.positive.getModel().getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawNegativePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawPositivePoint.getKey() + model.positive.getModel().getSize().getWidth()));
            } else {
                double positiveShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING + positiveSize.getWidth() - rhombusWidth / 2;
                double negativeShoulderLen = DiagramBlockModel.DECISION_BLOCKS_PADDING - rhombusWidth / 2;

                Pair<Double, Double> drawPositivePoint = new Pair<>(rhombusLeftConnector.getKey() - positiveShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> posConnector = model.positive.getModel().getTopConnector(drawPositivePoint);
                if (rhombusLeftConnector.getKey() - posConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawPositivePoint = new Pair<>(drawPositivePoint.getKey() - (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - rhombusLeftConnector.getKey() + posConnector.getKey()),
                            drawPositivePoint.getValue());
                }
                leftPoint = drawPositivePoint.getKey();

                Pair<Double, Double> drawNegativePoint = new Pair<>(rhombusRightConnector.getKey() + negativeShoulderLen,
                        model.getBottomRhombusConnector(drawPoint).getValue() + DiagramBlockModel.ELEMENTS_SPACING);
                Pair<Double, Double> negConnector = model.negative.getModel().getTopConnector(drawNegativePoint);
                if (negConnector.getKey() - rhombusRightConnector.getKey() < DiagramBlockModel.MIN_DECISION_SHOULDER_LEN) {
                    drawNegativePoint = new Pair<>(drawNegativePoint.getKey() + (DiagramBlockModel.MIN_DECISION_SHOULDER_LEN - (negConnector.getKey() - rhombusRightConnector.getKey())),
                            drawNegativePoint.getValue());
                }
                rightPoint = drawNegativePoint.getKey() + model.negative.getModel().getSize().getWidth();
                // Left bound
                leftBound = Math.abs(topConnector.getKey() - drawPositivePoint.getKey());
                // Right bound
                rightBound = Math.abs(topConnector.getKey() - (drawNegativePoint.getKey() + model.negative.getModel().getSize().getWidth()));
            }
        }
        Dimension2D size = new Dimension2D(Math.abs(rightPoint - leftPoint),
                rhombusSize.getHeight() + 2 * DiagramBlockModel.ELEMENTS_SPACING + Math.max(model.negative.getModel().getSize().getHeight(), model.positive.getModel().getSize().getHeight()));
        model.setMeasurements(size, leftBound, rightBound);
    }

    @Override
    public BDElementModel getModel() {
        return model;
    }

    @Override
    public BDElementController getSelected() {
        if (model.positive.getSelected() != null) return model.positive;
        if (model.negative.getSelected() != null) return model.negative;
        return super.getSelected();
    }

    @Override
    public BDElementController clone() {
        return new BDDecisionController(model.positive.clone(), model.positiveBranch,
                model.negative.clone(), model.negativeBranch,
                getModel().data, this.selected);
    }

    @Override
    public String getText() {
        return getModel().data;
    }

    @Override
    public void setText(String text) {
        this.model.data = text;
        recalculateSizes();
    }

    @Override
    public void removeFromContainer(BDElementController bdElementController) {
        if (model.positive == bdElementController) {
            model.positive = null;
            model.positive = new BDContainerController();
        } else if (model.negative == bdElementController) {
            model.negative = null;
            model.negative = new BDContainerController();
        }
    }

    @Override
    public void replaceInContainer(BDElementController replacing, BDElementController replacer) {
        if (replacing == this.model.positive && replacer instanceof BDContainerController) {
            this.model.positive = (BDContainerController) replacer;
            onDataUpdate.run();
        } else if (replacing == this.model.negative && replacer instanceof BDContainerController) {
            this.model.negative = (BDContainerController) replacer;
            onDataUpdate.run();
        }
    }

    @Override
    public void replace(BDElementController replacer) {
        if (parentContainer != null) {
            if (replacer instanceof BDDecisionController) return;
            replacer.setParentContainer(parentContainer);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Main.rb.getString("alert_data_narrowing_content"),
                    ButtonType.APPLY, ButtonType.CANCEL);
            alert.setTitle(Main.rb.getString("alert_data_narrowing_title"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.APPLY) {
                replacer.getModel().data = model.data;
                if (replacer instanceof BDCycleFixedController)
                    ((BDCycleFixedModel)replacer.getModel()).body = this.model.positive;
                if (replacer instanceof BDCycleNotFixedController)
                    ((BDCycleNotFixedModel)replacer.getModel()).body = this.model.positive;
                replacer.setControls();
                parentContainer.replaceInContainer(this, replacer);
            }
        }
    }
}

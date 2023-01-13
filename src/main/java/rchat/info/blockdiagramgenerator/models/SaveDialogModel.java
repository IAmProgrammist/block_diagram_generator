package rchat.info.blockdiagramgenerator.models;

import javafx.geometry.Dimension2D;

public class SaveDialogModel {
    public final double originalWidth;
    public final double originalHeight;
    public final double initialRatio;
    private static final double CM_IN_ONE_INCH = 2.54;
    public double widthPixels;
    public double heightPixels;
    public double widthInches;
    public double heightInches;
    public double scale;
    public Short fileExtension;
    public static final short PIXELS = 0;
    public static final short INCHES = 1;
    public static final short CENTIMETERS = 2;
    public static final short FILE_EXTENSION_SVG = 0;
    public static final short FILE_EXTENSION_PNG = 1;
    public static final short FILE_EXTENSION_JPG = 2;
    //px/inch
    public double density;
    public String file = null;

    public SaveDialogModel(double originalWidth, double originalHeight, double density) {
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.widthPixels = originalWidth;
        this.heightPixels = originalHeight;
        this.density = density;
        this.scale = 1.0;
        this.widthInches = originalWidth / density;
        this.heightInches = originalHeight / density;
        this.initialRatio = originalWidth / originalHeight;
    }

    // densityType = false, px/inch
    // densityType = true,  cm/inch
    public void setDensity(double newDensity, boolean densityType) {
        if (densityType)
            this.density = newDensity / CM_IN_ONE_INCH;
        else
            this.density = newDensity;

        this.widthInches = originalWidth / density;
        this.heightInches = originalHeight / density;
        this.scale = this.widthInches / this.originalWidth;
    }

    public void setWidth(double width, short measurments) {
        switch (measurments) {
            case PIXELS:
                this.widthPixels = width;
                break;
            case INCHES:
                this.widthPixels = width * density;
                break;
            case CENTIMETERS:
                this.widthPixels = (width / CM_IN_ONE_INCH) * density;
                break;
            default:
                throw new IllegalArgumentException();
        }

        this.heightPixels = this.widthPixels / initialRatio;
        this.heightInches = this.heightPixels / this.density;
        this.widthInches = this.widthPixels / this.density;
        this.scale = this.widthPixels / this.originalWidth;
    }

    public void setHeight(double height, short measurments) {
        switch (measurments) {
            case PIXELS:
                this.heightPixels = height;
                break;
            case INCHES:
                this.heightPixels = height * density;
                break;
            case CENTIMETERS:
                this.heightPixels = (height / CM_IN_ONE_INCH) * density;
                break;
            default:
                throw new IllegalArgumentException();
        }

        this.widthPixels = this.heightPixels * initialRatio;
        this.heightInches = this.heightPixels / this.density;
        this.widthInches = this.widthPixels / this.density;
        this.scale = this.heightPixels / this.originalHeight;
    }

    public Dimension2D getSize(short measurment) {
        switch (measurment) {
            case PIXELS:
                return new Dimension2D(widthPixels, heightPixels);
            case INCHES:
                return new Dimension2D(widthInches, heightInches);
            case CENTIMETERS:
                return new Dimension2D(widthInches * CM_IN_ONE_INCH, heightInches * CM_IN_ONE_INCH);
            default:
                throw new IllegalArgumentException();
        }
    }

    public double getDensity(boolean densityType) {
        if (densityType)
            return density / CM_IN_ONE_INCH;
        return density;
    }

    public void setPath(String absolutePath) {
        if (absolutePath.endsWith(".svg")) {
            fileExtension = FILE_EXTENSION_SVG;
        } else if (absolutePath.endsWith(".jpg")) {
            fileExtension = FILE_EXTENSION_JPG;
        } else if (absolutePath.endsWith(".png")) {
            fileExtension = FILE_EXTENSION_PNG;
        } else throw new IllegalArgumentException();

        this.file = absolutePath;
    }
}

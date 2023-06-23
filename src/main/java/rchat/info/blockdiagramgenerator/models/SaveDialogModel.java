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
    public double widthCantimeters;
    public double heightCantimeters;
    public double scale;
    public Short fileExtension;
    public static final short PIXELS = 0;
    public static final double MAX_DIMENSION = 300000;
    public static final double MIN_DIMENSION = 1;
    public static final short INCHES = 1;
    public static final short CENTIMETERS = 2;
    public static final short FILE_EXTENSION_SVG = 0;
    public static final short FILE_EXTENSION_PNG = 1;
    //public static final short FILE_EXTENSION_JPG = 2;
    public static final short FILE_EXTENSION_TEX = 3;
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
        this.widthCantimeters = widthInches * CM_IN_ONE_INCH;
        this.heightCantimeters = heightInches * CM_IN_ONE_INCH;
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
        double tempWidthPix;

        switch (measurments) {
            case PIXELS:
                tempWidthPix = width;
                break;
            case INCHES:
                tempWidthPix = width * density;
                break;
            case CENTIMETERS:
                tempWidthPix = (width / CM_IN_ONE_INCH) * density;
                break;
            default:
                throw new IllegalArgumentException();
        }

        tempWidthPix = Math.min(MAX_DIMENSION, Math.max(MIN_DIMENSION, tempWidthPix));
        double tempHeight = tempWidthPix / initialRatio;

        if (tempHeight < MIN_DIMENSION || tempHeight > MAX_DIMENSION) {
            setHeight(tempHeight, PIXELS);
            return;
        }

        this.widthPixels = tempWidthPix;
        this.heightPixels = tempHeight;

        this.heightInches = this.heightPixels / this.density;
        this.widthInches = this.widthPixels / this.density;

        this.heightCantimeters = this.heightInches * CM_IN_ONE_INCH;
        this.widthCantimeters = this.widthInches * CM_IN_ONE_INCH;

        this.scale = this.widthPixels / this.originalWidth;
    }

    public void setHeight(double height, short measurments) {
        double tempHeight;

        switch (measurments) {
            case PIXELS:
                tempHeight = height;
                break;
            case INCHES:
                tempHeight = height * density;
                break;
            case CENTIMETERS:
                tempHeight = (height / CM_IN_ONE_INCH) * density;
                break;
            default:
                throw new IllegalArgumentException();
        }


        tempHeight = Math.min(MAX_DIMENSION, Math.max(MIN_DIMENSION, tempHeight));
        double tempWidth = tempHeight * initialRatio;

        if (tempWidth < MIN_DIMENSION || tempWidth > MAX_DIMENSION) {
            setWidth(tempWidth, PIXELS);
            return;
        }

        this.widthPixels = tempWidth;
        this.heightPixels = tempHeight;

        this.heightInches = this.heightPixels / this.density;
        this.widthInches = this.widthPixels / this.density;

        this.heightCantimeters = this.heightInches * CM_IN_ONE_INCH;
        this.widthCantimeters = this.widthInches * CM_IN_ONE_INCH;

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
        //} else if (absolutePath.endsWith(".jpg")) {
        //    fileExtension = FILE_EXTENSION_JPG;
        } else if (absolutePath.endsWith(".png")) {
            fileExtension = FILE_EXTENSION_PNG;
        }else if (absolutePath.endsWith(".tex")) {
            fileExtension = FILE_EXTENSION_TEX;
        } else throw new IllegalArgumentException();

        this.file = absolutePath;
    }
}

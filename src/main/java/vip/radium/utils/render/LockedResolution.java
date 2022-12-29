package vip.radium.utils.render;

public final class LockedResolution {

    public static final int SCALE_FACTOR = 2;

    private final int width;
    private final int height;

    public LockedResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}

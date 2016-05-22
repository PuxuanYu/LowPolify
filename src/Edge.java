/**
 * Created by Lucius on 16/5/22.
 */

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Edge {
    private static final int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    public static void edge(BufferedImage image, EdgeCallback callback) {
        int width  = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelX = ( /*横向处理*/
                        (kernelX[0][0] * getAvg(image, x - 1, y - 1)) +
                                (kernelX[0][1] * getAvg(image, x, y - 1)) +
                                (kernelX[0][2] * getAvg(image, x + 1, y - 1)) +
                                (kernelX[1][0] * getAvg(image, x - 1, y)) +
                                (kernelX[1][1] * getAvg(image, x, y)) +
                                (kernelX[1][2] * getAvg(image, x + 1, y)) +
                                (kernelX[2][0] * getAvg(image, x - 1, y + 1)) +
                                (kernelX[2][1] * getAvg(image, x, y + 1)) +
                                (kernelX[2][2] * getAvg(image, x + 1, y + 1))
                );
                int pixelY = ( /*纵向处理,是横向处理的转置矩阵*/
                        (kernelY[0][0] * getAvg(image, x - 1, y - 1)) +
                                (kernelY[0][1] * getAvg(image, x, y - 1)) +
                                (kernelY[0][2] * getAvg(image, x + 1, y - 1)) +
                                (kernelY[1][0] * getAvg(image, x - 1, y)) +
                                (kernelY[1][1] * getAvg(image, x, y)) +
                                (kernelY[1][2] * getAvg(image, x + 1, y)) +
                                (kernelY[2][0] * getAvg(image, x - 1, y + 1)) +
                                (kernelY[2][1] * getAvg(image, x, y + 1)) +
                                (kernelY[2][2] * getAvg(image, x + 1, y + 1))
                );

                int magnitude = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                callback.call(magnitude, x, y);
            }
        }
    }

    private static int getAvg(BufferedImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return 0;
        }
        Color color = new Color(image.getRGB(x, y));
        return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
    }

    protected interface EdgeCallback {
        void call(int magnitude, int x, int y);
    }
}


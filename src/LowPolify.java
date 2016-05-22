/**
 * Created by Lucius on 16/5/21.
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;


public final class LowPolify {
    public static void generate(InputStream inputStream, OutputStream outputStream) throws IOException {
        generate(inputStream, outputStream, 200, 1, true, "png", true, 3000);
    }

    public static void generate(InputStream inputStream, OutputStream outputStream, int accuracy,
                                float scale, boolean fill, String format,
                                boolean antiAliasing, int pointCount) throws IOException {
        if (inputStream == null || outputStream == null) {
            return;
        }
        BufferedImage image = ImageIO.read(inputStream);

        int width = image.getWidth();
        int height = image.getHeight();

        ArrayList<int[]> collectors = new ArrayList<>();
        ArrayList<int[]> particles = new ArrayList<>();

        Edge.edge(image, (magnitude, x, y) -> {
            if (magnitude > 40) {
                collectors.add(new int[]{x, y});
            }
        });

        for (int i = 0; i < pointCount; i++) {
            particles.add(new int[]{(int) (Math.random() * width), (int) (Math.random() * height)});
        }

        int len = collectors.size() / accuracy;
        for (int i = 0; i < len; i++) {
            int rd = (int) (Math.random() * collectors.size());
            particles.add(collectors.get(rd));
            collectors.remove(rd);
        }

        particles.add(new int[]{0, 0});
        particles.add(new int[]{0, height});
        particles.add(new int[]{width, 0});
        particles.add(new int[]{width, height});

        List<Integer> triangles = Delaunay.triangulate(particles);

        float x1, x2, x3, y1, y2, y3, cx, cy;

        BufferedImage outImage = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = outImage.getGraphics();
        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int i = 0; i < triangles.size(); i += 3) {
            x1 = particles.get(triangles.get(i))[0];
            x2 = particles.get(triangles.get(i + 1))[0];
            x3 = particles.get(triangles.get(i + 2))[0];
            y1 = particles.get(triangles.get(i))[1];
            y2 = particles.get(triangles.get(i + 1))[1];
            y3 = particles.get(triangles.get(i + 2))[1];

            cx = (x1 + x2 + x3) / 3;
            cy = (y1 + y2 + y3) / 3;

            Color color = new Color(image.getRGB((int) cx, (int) cy));

            graphics.setColor(color);
            if (fill) {
                graphics.fillPolygon(new int[]{(int) (x1 * scale), (int) (x2 * scale), (int) (x3 * scale)}, new int[]{(int) (y1 * scale), (int) (y2 * scale), (int) (y3 * scale)}, 3);
            } else {
                graphics.drawPolygon(new int[]{(int) (x1 * scale), (int) (x2 * scale), (int) (x3 * scale)}, new int[]{(int) (y1 * scale), (int) (y2 * scale), (int) (y3 * scale)}, 3);
            }
        }

        ImageIO.write(outImage, format, outputStream);
    }
}

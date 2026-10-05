package com.stylemirror.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OverlayGenerator {

    public static void main(String[] args) throws IOException {
        String baseDir = "src/main/resources/static/assets/overlays";
        new File(baseDir).mkdirs();

        generateOverlay(baseDir + "/bomber_jacket.png", new Color(20, 24, 34, 230), new Color(0, 229, 255), "Jacket");
        generateOverlay(baseDir + "/oxford_shirt.png", new Color(245, 247, 250, 235), new Color(212, 175, 55), "Shirt");
        generateOverlay(baseDir + "/velvet_dress.png", new Color(128, 19, 54, 230), new Color(212, 175, 55), "Dress");
        generateOverlay(baseDir + "/graphic_hoodie.png", new Color(40, 50, 65, 230), new Color(0, 229, 255), "Hoodie");
        generateOverlay(baseDir + "/slim_blazer.png", new Color(25, 32, 48, 230), new Color(212, 175, 55), "Blazer");
        generateOverlay(baseDir + "/linen_tee.png", new Color(215, 204, 190, 230), new Color(180, 160, 140), "Tee");
        generateOverlay(baseDir + "/silk_dress.png", new Color(16, 120, 90, 230), new Color(212, 175, 55), "SilkDress");
        generateOverlay(baseDir + "/denim_jacket.png", new Color(40, 70, 115, 230), new Color(200, 150, 80), "Denim");
        generateOverlay(baseDir + "/executive_shirt.png", new Color(210, 230, 250, 235), new Color(60, 90, 140), "Executive");
        generateOverlay(baseDir + "/acid_tee.png", new Color(30, 30, 30, 230), new Color(150, 80, 220), "AcidTee");

        System.out.println("Generated all transparent try-on overlay PNGs.");
    }

    private static void generateOverlay(String path, Color bodyColor, Color accentColor, String type) throws IOException {
        int width = 400;
        int height = 500;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent background
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, width, height);
        g2.setComposite(AlphaComposite.SrcOver);

        // Draw garment silhouette
        GeneralPath garment = new GeneralPath();
        if (type.contains("Dress")) {
            garment.moveTo(150, 40);
            garment.lineTo(250, 40);
            garment.lineTo(290, 120);
            garment.lineTo(260, 200);
            garment.lineTo(340, 460);
            garment.lineTo(60, 460);
            garment.lineTo(140, 200);
            garment.lineTo(110, 120);
            garment.closePath();
        } else {
            // Jacket / Shirt / Blazer / Hoodie
            garment.moveTo(130, 40);
            garment.lineTo(270, 40);
            garment.lineTo(350, 120);
            garment.lineTo(390, 260);
            garment.lineTo(340, 280);
            garment.lineTo(295, 190);
            garment.lineTo(295, 460);
            garment.lineTo(105, 460);
            garment.lineTo(105, 190);
            garment.lineTo(60, 280);
            garment.lineTo(10, 260);
            garment.lineTo(50, 120);
            garment.closePath();
        }

        g2.setColor(bodyColor);
        g2.fill(garment);

        // Accent outline
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(garment);

        // Center line & details
        g2.drawLine(200, 60, 200, 460);

        // Collar notch
        GeneralPath collar = new GeneralPath();
        collar.moveTo(160, 40);
        collar.lineTo(200, 110);
        collar.lineTo(240, 40);
        g2.setStroke(new BasicStroke(3f));
        g2.draw(collar);

        g2.dispose();
        ImageIO.write(img, "PNG", new File(path));
    }
}

package com.stylemirror.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Generates photorealistic, high-resolution transparent apparel overlays
 * with realistic neckline cutouts, tailored sleeves, fabric folds, gradients,
 * seams, and luxury details for both Men and Women collections.
 */
public class OverlayGenerator {

    public static void main(String[] args) throws IOException {
        String staticDir = "src/main/resources/static/assets/overlays";
        String uploadDir = "uploads/tryon";
        new File(staticDir).mkdirs();
        new File(uploadDir).mkdirs();

        // 1. Men's Cyber Techwear Bomber Jacket
        renderAndSave(staticDir, uploadDir, "bomber_jacket.png", () -> createBomberJacket());

        // 2. Men's Classic Oxford Shirt
        renderAndSave(staticDir, uploadDir, "oxford_shirt.png", () -> createOxfordShirt(new Color(245, 248, 252), new Color(195, 215, 240), false));

        // 3. Women's Midnight Velvet Cocktail Dress
        renderAndSave(staticDir, uploadDir, "velvet_dress.png", () -> createVelvetCocktailDress());

        // 4. Streetwear Graphic Hoodie
        renderAndSave(staticDir, uploadDir, "graphic_hoodie.png", () -> createStreetwearHoodie());

        // 5. Men's Italian Wool Slim Blazer
        renderAndSave(staticDir, uploadDir, "slim_blazer.png", () -> createTailoredBlazer(new Color(22, 28, 42), new Color(14, 18, 28)));

        // 6. Men's Minimalist Linen Summer Tee
        renderAndSave(staticDir, uploadDir, "linen_tee.png", () -> createLinenTee(new Color(230, 222, 208), new Color(205, 195, 180)));

        // 7. Women's Floral Silk Wrap Midi Dress
        renderAndSave(staticDir, uploadDir, "silk_dress.png", () -> createFloralSilkDress());

        // 8. Men's Vintage Denim Jacket
        renderAndSave(staticDir, uploadDir, "denim_jacket.png", () -> createDenimJacket());

        // 9. Men's Monochrome Executive Shirt
        renderAndSave(staticDir, uploadDir, "executive_shirt.png", () -> createOxfordShirt(new Color(25, 28, 36), new Color(45, 52, 68), true));

        // 10. Streetwear Acid Wash Boxy Tee
        renderAndSave(staticDir, uploadDir, "acid_tee.png", () -> createLinenTee(new Color(38, 40, 48), new Color(25, 26, 32)));

        // 11. Women's Emerald Royal Satin Evening Gown (New luxury dress)
        renderAndSave(staticDir, uploadDir, "emerald_gown.png", () -> createEmeraldSatinGown());

        // 12. Men's Black-Tie Royal Tuxedo Suit (New luxury formal)
        renderAndSave(staticDir, uploadDir, "tuxedo_suit.png", () -> createTuxedoSuit());

        System.out.println("Generated 12 photorealistic garment overlays for Men and Women.");
    }

    private static void renderAndSave(String dir1, String dir2, String filename, ImageSupplier supplier) throws IOException {
        BufferedImage img = supplier.get();
        ImageIO.write(img, "PNG", new File(dir1, filename));
        ImageIO.write(img, "PNG", new File(dir2, filename));
    }

    @FunctionalInterface
    interface ImageSupplier {
        BufferedImage get();
    }

    // =========================================================================
    // 1. WOMEN'S MIDNIGHT VELVET COCKTAIL DRESS
    // =========================================================================
    private static BufferedImage createVelvetCocktailDress() {
        int W = 600, H = 850;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        // Rich Royal Burgundy / Ruby Velvet with subtle light reflections
        Color velvetDark = new Color(75, 8, 28);
        Color velvetMid = new Color(125, 16, 48);
        Color velvetHighlight = new Color(175, 35, 75);

        // Skirt and Bodice Path
        Path2D dress = new Path2D.Float();
        // Left shoulder strap
        dress.moveTo(215, 65);
        dress.curveTo(225, 55, 240, 60, 245, 75); // Left shoulder curve
        // Neckline (Sweetheart / Plunging V)
        dress.curveTo(270, 150, 330, 150, 355, 75); // Elegant sweetheart neckline
        // Right shoulder strap
        dress.curveTo(360, 60, 375, 55, 385, 65);
        // Right armhole / side bodice
        dress.curveTo(420, 120, 395, 240, 385, 310); // Cinched waist right
        // Right hips and flared mermaid/A-line skirt
        dress.curveTo(430, 420, 460, 600, 485, 820);
        // Hemline curve
        dress.curveTo(390, 840, 210, 840, 115, 820);
        // Left hips and flared skirt
        dress.curveTo(140, 600, 170, 420, 215, 310); // Cinched waist left
        // Left armhole
        dress.curveTo(205, 240, 180, 120, 215, 65);
        dress.closePath();

        // Velvet Gradient Fill (Vertical & Radial Depth)
        GradientPaint grad = new GradientPaint(W / 2f, 50, velvetMid, W / 2f, H - 50, velvetDark);
        g.setPaint(grad);
        g.fill(dress);

        // Velvet Sheen / Highlighting (simulating lush velvet drape folds)
        g.setPaint(new GradientPaint(180, 200, new Color(220, 60, 100, 70), 300, 500, new Color(255, 255, 255, 0)));
        Path2D fold1 = new Path2D.Float();
        fold1.moveTo(250, 170);
        fold1.curveTo(270, 340, 240, 550, 220, 825);
        fold1.lineTo(260, 830);
        fold1.curveTo(285, 550, 305, 340, 280, 170);
        fold1.closePath();
        g.fill(fold1);

        // Right fold
        g.setPaint(new GradientPaint(420, 200, new Color(220, 60, 100, 70), 300, 500, new Color(255, 255, 255, 0)));
        Path2D fold2 = new Path2D.Float();
        fold2.moveTo(350, 170);
        fold2.curveTo(330, 340, 360, 550, 380, 825);
        fold2.lineTo(340, 830);
        fold2.curveTo(315, 550, 295, 340, 320, 170);
        fold2.closePath();
        g.fill(fold2);

        // Shading on waist and side contours
        g.setColor(new Color(30, 3, 10, 120));
        g.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D sideShadeLeft = new Path2D.Float();
        sideShadeLeft.moveTo(195, 120);
        sideShadeLeft.curveTo(185, 240, 175, 420, 130, 810);
        g.draw(sideShadeLeft);

        Path2D sideShadeRight = new Path2D.Float();
        sideShadeRight.moveTo(405, 120);
        sideShadeRight.curveTo(415, 240, 425, 420, 470, 810);
        g.draw(sideShadeRight);

        // Gold Waist Belt / Couture Accent
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(6f));
        g.drawLine(215, 310, 385, 310);
        // Belt Buckle
        g.setColor(new Color(255, 225, 130));
        g.fillRoundRect(285, 301, 30, 18, 6, 6);
        g.setColor(new Color(150, 115, 25));
        g.drawRoundRect(285, 301, 30, 18, 6, 6);

        // Sweetheart Neckline Trim & Hem Trim
        g.setColor(new Color(212, 175, 55, 180));
        g.setStroke(new BasicStroke(2.5f));
        Path2D neckTrim = new Path2D.Float();
        neckTrim.moveTo(245, 75);
        neckTrim.curveTo(270, 150, 330, 150, 355, 75);
        g.draw(neckTrim);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 2. WOMEN'S EMERALD SATIN GALA GOWN
    // =========================================================================
    private static BufferedImage createEmeraldSatinGown() {
        int W = 600, H = 880;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Color satinDeep = new Color(6, 68, 52);
        Color satinMid = new Color(16, 120, 92);
        Color satinLight = new Color(42, 185, 145);

        Path2D gown = new Path2D.Float();
        gown.moveTo(225, 70);
        gown.curveTo(240, 60, 250, 75, 255, 90);
        gown.curveTo(285, 135, 315, 135, 345, 90); // Delicate cowl neckline
        gown.curveTo(350, 75, 360, 60, 375, 70);
        gown.curveTo(400, 130, 385, 260, 375, 330); // Waistline
        gown.curveTo(420, 480, 455, 680, 480, 855); // Flared skirt
        gown.curveTo(380, 875, 220, 875, 120, 855);
        gown.curveTo(145, 680, 180, 480, 225, 330);
        gown.curveTo(215, 260, 200, 130, 225, 70);
        gown.closePath();

        // Satin Shimmer Fill
        g.setPaint(new GradientPaint(W / 2f, 70, satinMid, W / 2f, H - 50, satinDeep));
        g.fill(gown);

        // Vertical Satin Highlights
        g.setPaint(new GradientPaint(260, 150, new Color(80, 230, 185, 90), 340, 700, new Color(255, 255, 255, 0)));
        Path2D shine = new Path2D.Float();
        shine.moveTo(280, 130);
        shine.curveTo(295, 350, 265, 580, 240, 860);
        shine.lineTo(285, 865);
        shine.curveTo(320, 580, 335, 350, 320, 130);
        shine.closePath();
        g.fill(shine);

        // Cinched Waist Draping folds
        g.setColor(new Color(3, 40, 30, 140));
        g.setStroke(new BasicStroke(3f));
        g.drawArc(225, 315, 150, 30, 0, -180);
        g.drawArc(220, 330, 160, 35, 0, -180);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 3. WOMEN'S FLORAL SILK WRAP MIDI DRESS
    // =========================================================================
    private static BufferedImage createFloralSilkDress() {
        int W = 600, H = 840;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Color silkBase = new Color(248, 242, 235);
        Color silkShadow = new Color(225, 215, 202);

        Path2D dress = new Path2D.Float();
        dress.moveTo(200, 70);
        dress.curveTo(220, 50, 240, 60, 250, 85);
        // Wrap V-neck
        dress.lineTo(310, 210); // crossover point
        dress.lineTo(350, 85);
        dress.curveTo(360, 60, 380, 50, 400, 70);
        // Sleeves (flutter cap sleeves)
        dress.curveTo(450, 110, 440, 180, 410, 210);
        dress.lineTo(380, 210);
        dress.curveTo(375, 280, 370, 340, 365, 360);
        dress.curveTo(430, 500, 470, 670, 490, 810);
        dress.curveTo(380, 835, 220, 835, 110, 810);
        dress.curveTo(130, 670, 170, 500, 235, 360);
        dress.curveTo(230, 340, 225, 280, 220, 210);
        dress.lineTo(190, 210);
        dress.curveTo(160, 180, 150, 110, 200, 70);
        dress.closePath();

        g.setPaint(new GradientPaint(W / 2f, 60, silkBase, W / 2f, H - 40, silkShadow));
        g.fill(dress);

        // Floral motifs (Chic delicate blush and sage petals across the silk)
        drawFloralPattern(g, 150, 350, 300, 450);

        // Wrap Sash & Bow
        g.setColor(new Color(210, 140, 150));
        g.setStroke(new BasicStroke(8f));
        g.drawLine(230, 350, 370, 350);
        // Bow
        g.fillOval(355, 338, 25, 25);
        g.fillOval(375, 335, 22, 18);
        g.drawLine(370, 350, 385, 410);

        g.dispose();
        return img;
    }

    private static void drawFloralPattern(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(225, 130, 145, 140)); // Rose blush
        for (int i = 0; i < 18; i++) {
            int px = x + (int) ((Math.sin(i * 1.8) * 0.4 + 0.5) * w);
            int py = y + (int) ((i / 18.0) * h);
            g.fillOval(px, py, 14, 14);
            g.fillOval(px + 10, py - 4, 12, 12);
            g.fillOval(px + 6, py + 8, 12, 12);
            // Sage leaf
            g.setColor(new Color(110, 150, 120, 120));
            g.fillOval(px - 6, py + 4, 8, 14);
            g.setColor(new Color(225, 130, 145, 140));
        }
    }

    // =========================================================================
    // 4. MEN'S TAILORED ITALIAN WOOL SLIM BLAZER
    // =========================================================================
    private static BufferedImage createTailoredBlazer(Color baseDark, Color shadowDark) {
        int W = 600, H = 820;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        // Outer Jacket Silhouette
        Path2D jacket = new Path2D.Float();
        jacket.moveTo(210, 65);
        jacket.lineTo(390, 65); // Collar base
        jacket.lineTo(515, 140); // Right shoulder
        jacket.lineTo(560, 480); // Right sleeve
        jacket.lineTo(470, 495); // Sleeve hem
        jacket.lineTo(435, 360); // Underarm
        jacket.lineTo(440, 770); // Right hem
        jacket.lineTo(160, 770); // Hemline
        jacket.lineTo(165, 360); // Underarm
        jacket.lineTo(130, 495); // Sleeve hem
        jacket.lineTo(40, 480);  // Left sleeve
        jacket.lineTo(85, 140);  // Left shoulder
        jacket.closePath();

        g.setPaint(new GradientPaint(W / 2f, 60, baseDark, W / 2f, H - 50, shadowDark));
        g.fill(jacket);

        // Inner White Shirt & Silk Tie V-Opening
        Path2D shirtOpening = new Path2D.Float();
        shirtOpening.moveTo(250, 65);
        shirtOpening.lineTo(350, 65);
        shirtOpening.lineTo(300, 360); // Lapel crossover
        shirtOpening.closePath();
        g.setColor(new Color(250, 252, 255));
        g.fill(shirtOpening);

        // Silk Burgundy Tie
        Path2D tie = new Path2D.Float();
        tie.moveTo(288, 110);
        tie.lineTo(312, 110);
        tie.lineTo(318, 320);
        tie.lineTo(300, 355);
        tie.lineTo(282, 320);
        tie.closePath();
        g.setColor(new Color(140, 20, 38));
        g.fill(tie);

        // Lapels (Satin / Tailored Notch)
        Color lapelColor = new Color(baseDark.getRed() + 15, baseDark.getGreen() + 15, baseDark.getBlue() + 20);
        Path2D leftLapel = new Path2D.Float();
        leftLapel.moveTo(220, 65);
        leftLapel.lineTo(275, 180);
        leftLapel.lineTo(255, 195);
        leftLapel.lineTo(305, 385);
        leftLapel.lineTo(220, 385);
        leftLapel.lineTo(195, 180);
        leftLapel.closePath();
        g.setColor(lapelColor);
        g.fill(leftLapel);

        Path2D rightLapel = new Path2D.Float();
        rightLapel.moveTo(380, 65);
        rightLapel.lineTo(325, 180);
        rightLapel.lineTo(345, 195);
        rightLapel.lineTo(295, 385);
        rightLapel.lineTo(380, 385);
        rightLapel.lineTo(405, 180);
        rightLapel.closePath();
        g.fill(rightLapel);

        // Pocket Square (Gold Silk)
        g.setColor(new Color(212, 175, 55));
        Path2D pocketSquare = new Path2D.Float();
        pocketSquare.moveTo(190, 320);
        pocketSquare.lineTo(210, 295);
        pocketSquare.lineTo(225, 310);
        pocketSquare.lineTo(235, 320);
        pocketSquare.closePath();
        g.fill(pocketSquare);

        // Chest Pocket Welt
        g.setColor(new Color(15, 20, 30));
        g.fillRect(185, 320, 60, 8);

        // Front Horn Buttons
        g.setColor(new Color(212, 175, 55));
        g.fillOval(293, 420, 14, 14);
        g.fillOval(293, 490, 14, 14);
        g.setColor(new Color(30, 20, 10));
        g.fillOval(296, 423, 8, 8);
        g.fillOval(296, 493, 8, 8);

        // Side Flap Pockets
        g.setColor(new Color(15, 20, 30));
        g.fillRoundRect(170, 540, 85, 22, 6, 6);
        g.fillRoundRect(345, 540, 85, 22, 6, 6);

        // Tailored Seam Details
        g.setColor(new Color(255, 255, 255, 30));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(200, 140, 180, 770);
        g.drawLine(400, 140, 420, 770);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 5. MEN'S BLACK-TIE ROYAL TUXEDO SUIT
    // =========================================================================
    private static BufferedImage createTuxedoSuit() {
        int W = 600, H = 840;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        // Black Tuxedo Base
        Color tuxBlack = new Color(12, 14, 18);
        Color satinLapel = new Color(28, 32, 40);

        Path2D tux = new Path2D.Float();
        tux.moveTo(215, 65);
        tux.lineTo(385, 65);
        tux.lineTo(510, 135);
        tux.lineTo(555, 475);
        tux.lineTo(465, 490);
        tux.lineTo(435, 355);
        tux.lineTo(435, 780);
        tux.lineTo(165, 780);
        tux.lineTo(165, 355);
        tux.lineTo(135, 490);
        tux.lineTo(45, 475);
        tux.lineTo(90, 135);
        tux.closePath();

        g.setColor(tuxBlack);
        g.fill(tux);

        // Crisp White Tuxedo Shirt (Pleated)
        Path2D shirt = new Path2D.Float();
        shirt.moveTo(250, 65);
        shirt.lineTo(350, 65);
        shirt.lineTo(300, 390);
        shirt.closePath();
        g.setColor(new Color(252, 252, 255));
        g.fill(shirt);

        // Black Satin Bowtie
        g.setColor(new Color(15, 15, 20));
        Path2D bowtie = new Path2D.Float();
        bowtie.moveTo(270, 95);
        bowtie.lineTo(330, 95);
        bowtie.lineTo(300, 120);
        bowtie.lineTo(330, 145);
        bowtie.lineTo(270, 145);
        bowtie.lineTo(300, 120);
        bowtie.closePath();
        g.fill(bowtie);
        g.fillOval(293, 112, 14, 16);

        // Shawl Satin Lapel
        Path2D shawlLapel = new Path2D.Float();
        shawlLapel.moveTo(225, 65);
        shawlLapel.curveTo(190, 200, 240, 390, 300, 420);
        shawlLapel.curveTo(360, 390, 410, 200, 375, 65);
        shawlLapel.lineTo(350, 65);
        shawlLapel.curveTo(370, 190, 335, 350, 300, 390);
        shawlLapel.curveTo(265, 350, 230, 190, 250, 65);
        shawlLapel.closePath();
        g.setColor(satinLapel);
        g.fill(shawlLapel);

        // Satin Lapel Sheen Edge
        g.setColor(new Color(255, 255, 255, 45));
        g.setStroke(new BasicStroke(2f));
        g.draw(shawlLapel);

        // Crisp Silk Pocket Square
        g.setColor(Color.WHITE);
        g.fillRect(190, 310, 45, 6);

        // Single Onyx Button
        g.setColor(new Color(5, 5, 8));
        g.fillOval(293, 445, 15, 15);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(293, 445, 15, 15);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 6. MEN'S CLASSIC OXFORD SHIRT
    // =========================================================================
    private static BufferedImage createOxfordShirt(Color baseColor, Color seamColor, boolean isExecutive) {
        int W = 600, H = 820;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Path2D shirt = new Path2D.Float();
        shirt.moveTo(220, 65);
        shirt.lineTo(380, 65);
        shirt.lineTo(495, 130);
        shirt.lineTo(540, 460);
        shirt.lineTo(460, 475);
        shirt.lineTo(425, 335);
        shirt.lineTo(430, 755);
        shirt.lineTo(170, 755);
        shirt.lineTo(175, 335);
        shirt.lineTo(140, 475);
        shirt.lineTo(60, 460);
        shirt.lineTo(105, 130);
        shirt.closePath();

        g.setColor(baseColor);
        g.fill(shirt);

        // Crisp Folded Collar
        Path2D leftCollar = new Path2D.Float();
        leftCollar.moveTo(220, 65);
        leftCollar.lineTo(290, 155);
        leftCollar.lineTo(260, 165);
        leftCollar.lineTo(200, 75);
        leftCollar.closePath();
        g.setColor(isExecutive ? new Color(35, 40, 52) : new Color(230, 238, 248));
        g.fill(leftCollar);

        Path2D rightCollar = new Path2D.Float();
        rightCollar.moveTo(380, 65);
        rightCollar.lineTo(310, 155);
        rightCollar.lineTo(340, 165);
        rightCollar.lineTo(400, 75);
        rightCollar.closePath();
        g.fill(rightCollar);

        // Center Button Placket
        g.setColor(isExecutive ? new Color(20, 24, 32) : new Color(235, 242, 250));
        g.fillRect(285, 155, 30, 600);
        g.setColor(seamColor);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(285, 155, 30, 600);

        // Mother-of-pearl buttons
        Color buttonColor = isExecutive ? new Color(200, 205, 215) : Color.WHITE;
        for (int y = 200; y <= 700; y += 75) {
            g.setColor(buttonColor);
            g.fillOval(295, y, 10, 10);
            g.setColor(new Color(150, 160, 175));
            g.drawOval(295, y, 10, 10);
        }

        // Chest Pocket
        g.setColor(isExecutive ? new Color(28, 32, 42) : new Color(238, 244, 252));
        Path2D pocket = new Path2D.Float();
        pocket.moveTo(195, 270);
        pocket.lineTo(260, 270);
        pocket.lineTo(260, 350);
        pocket.lineTo(228, 370);
        pocket.lineTo(195, 350);
        pocket.closePath();
        g.fill(pocket);
        g.setColor(seamColor);
        g.draw(pocket);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 7. MEN'S CYBER TECHWEAR BOMBER JACKET
    // =========================================================================
    private static BufferedImage createBomberJacket() {
        int W = 600, H = 800;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Color bomberBlack = new Color(20, 24, 32);
        Color cyanAccent = new Color(0, 229, 255);

        Path2D jacket = new Path2D.Float();
        jacket.moveTo(215, 80);
        jacket.lineTo(385, 80);
        jacket.lineTo(515, 140);
        jacket.lineTo(560, 480);
        jacket.lineTo(470, 495);
        jacket.lineTo(435, 360);
        jacket.lineTo(440, 730);
        jacket.lineTo(160, 730);
        jacket.lineTo(165, 360);
        jacket.lineTo(130, 495);
        jacket.lineTo(40, 480);
        jacket.lineTo(85, 140);
        jacket.closePath();

        g.setColor(bomberBlack);
        g.fill(jacket);

        // Ribbed Collar Band
        g.setColor(new Color(10, 12, 16));
        g.fillRoundRect(210, 65, 180, 30, 12, 12);
        // Ribbed Bottom Band
        g.fillRoundRect(155, 725, 290, 45, 10, 10);

        // Metallic Center Zipper
        g.setColor(new Color(80, 85, 95));
        g.fillRect(296, 95, 8, 630);
        g.setColor(cyanAccent);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(295, 95, 295, 725);
        g.drawLine(305, 95, 305, 725);

        // Tactical Arm Pocket & Utility Red Ribbon
        g.setColor(new Color(32, 38, 48));
        g.fillRect(80, 250, 45, 70);
        g.setColor(new Color(220, 38, 38)); // "Remove Before Flight" Red Ribbon
        g.fillRect(72, 290, 12, 65);

        // Neon Cyber Panel Stitching
        g.setColor(new Color(0, 229, 255, 120));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{6, 6}, 0));
        g.drawArc(180, 140, 100, 160, 0, 90);
        g.drawArc(320, 140, 100, 160, 90, 90);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 8. MEN'S VINTAGE WASHED DENIM JACKET
    // =========================================================================
    private static BufferedImage createDenimJacket() {
        int W = 600, H = 800;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Color denimBlue = new Color(52, 85, 130);
        Color denimFade = new Color(85, 125, 175);
        Color stitchGold = new Color(212, 165, 75);

        Path2D denim = new Path2D.Float();
        denim.moveTo(215, 70);
        denim.lineTo(385, 70);
        denim.lineTo(510, 135);
        denim.lineTo(555, 470);
        denim.lineTo(465, 485);
        denim.lineTo(430, 350);
        denim.lineTo(435, 740);
        denim.lineTo(165, 740);
        denim.lineTo(170, 350);
        denim.lineTo(135, 485);
        denim.lineTo(45, 470);
        denim.lineTo(90, 135);
        denim.closePath();

        // Washed Gradient
        g.setPaint(new GradientPaint(W / 2f, 150, denimFade, W / 2f, H - 100, denimBlue));
        g.fill(denim);

        // Pointed Collar
        Path2D collar = new Path2D.Float();
        collar.moveTo(215, 70);
        collar.lineTo(270, 145);
        collar.lineTo(330, 145);
        collar.lineTo(385, 70);
        collar.lineTo(300, 95);
        collar.closePath();
        g.setColor(new Color(42, 68, 105));
        g.fill(collar);

        // Flap Chest Pockets
        g.setColor(new Color(45, 72, 110));
        Path2D leftFlap = new Path2D.Float();
        leftFlap.moveTo(185, 230);
        leftFlap.lineTo(265, 230);
        leftFlap.lineTo(265, 280);
        leftFlap.lineTo(225, 305);
        leftFlap.lineTo(185, 280);
        leftFlap.closePath();
        g.fill(leftFlap);

        Path2D rightFlap = new Path2D.Float();
        rightFlap.moveTo(335, 230);
        rightFlap.lineTo(415, 230);
        rightFlap.lineTo(415, 280);
        rightFlap.lineTo(375, 305);
        rightFlap.lineTo(335, 280);
        rightFlap.closePath();
        g.fill(rightFlap);

        // Bronze Metal Rivet Buttons
        Color bronze = new Color(195, 135, 60);
        g.setColor(bronze);
        g.fillOval(220, 245, 12, 12);
        g.fillOval(370, 245, 12, 12);
        for (int y = 200; y <= 680; y += 95) {
            g.fillOval(294, y, 14, 14);
        }

        // Contrast Gold Stitching Lines
        g.setColor(stitchGold);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{4, 4}, 0));
        g.drawLine(225, 305, 225, 740);
        g.drawLine(375, 305, 375, 740);
        g.draw(leftFlap);
        g.draw(rightFlap);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 9. STREETWEAR GRAPHIC HOODIE
    // =========================================================================
    private static BufferedImage createStreetwearHoodie() {
        int W = 600, H = 820;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Color hoodieGrey = new Color(38, 44, 56);
        Path2D hoodie = new Path2D.Float();
        hoodie.moveTo(210, 80);
        hoodie.lineTo(390, 80);
        hoodie.lineTo(525, 145);
        hoodie.lineTo(570, 500);
        hoodie.lineTo(475, 515);
        hoodie.lineTo(440, 380);
        hoodie.lineTo(450, 770);
        hoodie.lineTo(150, 770);
        hoodie.lineTo(160, 380);
        hoodie.lineTo(125, 515);
        hoodie.lineTo(30, 500);
        hoodie.lineTo(75, 145);
        hoodie.closePath();

        g.setColor(hoodieGrey);
        g.fill(hoodie);

        // Hood Neckline & Folds
        g.setColor(new Color(24, 28, 36));
        g.fillArc(210, 60, 180, 80, 0, -180);

        // Drawstrings
        g.setColor(new Color(220, 225, 235));
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D stringL = new Path2D.Float();
        stringL.moveTo(260, 120);
        stringL.curveTo(255, 190, 245, 240, 250, 290);
        g.draw(stringL);

        Path2D stringR = new Path2D.Float();
        stringR.moveTo(340, 120);
        stringR.curveTo(345, 190, 355, 240, 350, 290);
        g.draw(stringR);

        // Kangaroo Front Pocket
        g.setColor(new Color(28, 34, 44));
        Path2D pocket = new Path2D.Float();
        pocket.moveTo(210, 520);
        pocket.lineTo(390, 520);
        pocket.lineTo(425, 680);
        pocket.lineTo(175, 680);
        pocket.closePath();
        g.fill(pocket);

        // Cyber Aesthetic Graphic Print
        g.setColor(new Color(0, 229, 255));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("STYLEMIRROR", 220, 320);
        g.setColor(new Color(212, 175, 55));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("// ATELIER EDITION 2026 //", 195, 345);

        g.dispose();
        return img;
    }

    // =========================================================================
    // 10. MEN'S MINIMALIST LINEN TEE
    // =========================================================================
    private static BufferedImage createLinenTee(Color baseColor, Color shadeColor) {
        int W = 600, H = 780;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics(img);

        Path2D tee = new Path2D.Float();
        tee.moveTo(215, 65);
        tee.curveTo(260, 110, 340, 110, 385, 65); // Crew neckline
        tee.lineTo(505, 125);
        tee.lineTo(540, 320);
        tee.lineTo(460, 340);
        tee.lineTo(435, 260);
        tee.lineTo(435, 730);
        tee.lineTo(165, 730);
        tee.lineTo(165, 260);
        tee.lineTo(140, 340);
        tee.lineTo(60, 320);
        tee.lineTo(95, 125);
        tee.closePath();

        g.setPaint(new GradientPaint(W / 2f, 60, baseColor, W / 2f, H - 60, shadeColor));
        g.fill(tee);

        // Ribbed Neckline Binding
        g.setColor(shadeColor);
        g.setStroke(new BasicStroke(5f));
        Path2D neckRib = new Path2D.Float();
        neckRib.moveTo(215, 65);
        neckRib.curveTo(260, 110, 340, 110, 385, 65);
        g.draw(neckRib);

        g.dispose();
        return img;
    }

    private static Graphics2D setupGraphics(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        return g;
    }
}

/*
Class ParticleEngine
By Art1kDev
*/

package com.art1kdev.Zombietown;

import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.Iterator;

public class ParticleManager {
    private static ArrayList<Particle> particles = new ArrayList<>();
    private static ArrayList<BloodParticle> bloodParticles = new ArrayList<>();

    public static void addParticle(float x, float y, float z, float velX, float velY, float velZ, int tex, float sizeMod) {
        particles.add(new Particle(x, y, z, velX, velY, velZ, tex, sizeMod));
    }

    public static void addBloodParticle(float x, float y, float z, float velX, float velY, float velZ) {
        bloodParticles.add(new BloodParticle(x, y, z, velX, velY, velZ));
    }

    public static void update() {
        updateParticleList(particles);
        updateParticleList(bloodParticles);
    }

    private static void updateParticleList(ArrayList<? extends Particle> list) {
        Iterator<? extends Particle> it = list.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.isDead()) {
                it.remove();
            }
        }
    }

    public static void render() {
        renderParticleList(particles, "/dust.png");
        renderParticleList(bloodParticles, "/blood.png");
    }

    private static void renderParticleList(ArrayList<? extends Particle> list, String texture) {
        if (list.isEmpty()) return;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture(texture, GL11.GL_NEAREST));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBegin(GL11.GL_QUADS);
        for (Particle p : list) {
            p.render();
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void clear() {
        particles.clear();
        bloodParticles.clear();
    }
}
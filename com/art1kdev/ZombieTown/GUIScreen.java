/*
Class GUIScreen
By Art1kDev
*/

package com.art1kdev.Zombietown;

import org.lwjgl.opengl.GL11;
import java.util.ArrayList;

public class GUIScreen {
    private static final int CHAR_WIDTH = 16;
    private static final int CHAR_HEIGHT = 16;
    private static final int TEXTURE_SIZE = 128;

    private static ArrayList<PlayerParticle> playerParticles = new ArrayList<>();
    private static float lastPlayerX = 0.0f;
    private static float lastPlayerZ = 0.0f;
    private static long lastParticleSpawn = 0;
    private static final long PARTICLE_SPAWN_INTERVAL = 100;

    public static void updatePlayerParticles(float playerX, float playerZ) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastParticleSpawn > PARTICLE_SPAWN_INTERVAL) {
            float dx = playerX - lastPlayerX;
            float dz = playerZ - lastPlayerZ;
            float distance = (float) Math.sqrt(dx * dx + dz * dz);
            if (distance > 0.01f) {
                lastParticleSpawn = currentTime;
                double angle = Math.atan2(dz, dx);
                double spread = 0.5;
                for (int i = 0; i < 2; i++) {
                    double offsetAngle = angle + (Math.random() - 0.5) * spread;
                    float offsetX = (float) (Math.cos(offsetAngle) * 0.3);
                    float offsetZ = (float) (Math.sin(offsetAngle) * 0.3);
                    playerParticles.add(new PlayerParticle(playerX + offsetX, 0.1f, playerZ + offsetZ));
                }
            }
        }
        lastPlayerX = playerX;
        lastPlayerZ = playerZ;

        for (int i = playerParticles.size() - 1; i >= 0; i--) {
            PlayerParticle p = playerParticles.get(i);
            p.update();
            if (p.isDead()) {
                playerParticles.remove(i);
            }
        }
    }

    public static void render(int score, int wave, int zombieCount) {
        renderPlayerParticles();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 600, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_FOG);

        renderText("Score: " + score, 20, 30);
        renderText("Wave: " + wave, 20, 60);
        renderText("Zombies: " + zombieCount, 20, 90);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_FOG);

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private static void renderPlayerParticles() {
        if (playerParticles.isEmpty()) return;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture("/terrain.png", GL11.GL_NEAREST));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glBegin(GL11.GL_QUADS);
        for (PlayerParticle p : playerParticles) {
            p.render();
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void renderText(String text, int x, int y) {
        renderText(text, x, y, "/default.png");
    }

    public static void renderText(String text, int x, int y, String texture) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture(texture, GL11.GL_NEAREST));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        float texelSize = 8.0f / TEXTURE_SIZE;

        GL11.glBegin(GL11.GL_QUADS);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charId = getCharId(c);

            if (charId >= 0) {
                int cx = charId % 16;
                int cy = charId / 16;

                float u1 = cx * texelSize;
                float v1 = cy * texelSize;
                float u2 = (cx + 1) * texelSize;
                float v2 = (cy + 1) * texelSize;

                GL11.glTexCoord2f(u1, v1);
                GL11.glVertex2f(x + i * CHAR_WIDTH, y);

                GL11.glTexCoord2f(u1, v2);
                GL11.glVertex2f(x + i * CHAR_WIDTH, y + CHAR_HEIGHT);

                GL11.glTexCoord2f(u2, v2);
                GL11.glVertex2f(x + i * CHAR_WIDTH + CHAR_WIDTH, y + CHAR_HEIGHT);

                GL11.glTexCoord2f(u2, v1);
                GL11.glVertex2f(x + i * CHAR_WIDTH + CHAR_WIDTH, y);
            }
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    private static int getCharId(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 10;
        } else if (c == ':') {
            return 36;
        } else if (c == ' ') {
            return 37;
        } else if (c == '/') {
            return 38;
        } else if (c == '!') {
            return 39;
        } else {
            return 37;
        }
    }

    private static class PlayerParticle {
        public float x, y, z;
        public float xo, yo, zo;
        private float xd, yd, zd;
        private int age = 0;
        private int lifetime = 20;
        private float size = 0.1f;

        public PlayerParticle(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.xo = x;
            this.yo = y;
            this.zo = z;
            this.xd = (float) (Math.random() - 0.5) * 0.05f;
            this.yd = (float) (Math.random() * 0.05f);
            this.zd = (float) (Math.random() - 0.5) * 0.05f;
        }

        public void update() {
            xo = x;
            yo = y;
            zo = z;
            age++;
            x += xd;
            y += yd;
            z += zd;
            xd *= 0.98f;
            yd *= 0.98f;
            zd *= 0.98f;
            yd -= 0.005f;
        }

        public boolean isDead() {
            return age >= lifetime;
        }

        public void render() {
            int tex = 7;
            float u0 = (tex % 16) / 16.0f;
            float u1 = u0 + 0.062438f;
            float v0 = (tex / 16) / 16.0f;
            float v1 = v0 + 0.062438f;

            GL11.glTexCoord2f(u0, v1);
            GL11.glVertex3f(x - size, y - size, z);
            GL11.glTexCoord2f(u1, v1);
            GL11.glVertex3f(x + size, y - size, z);
            GL11.glTexCoord2f(u1, v0);
            GL11.glVertex3f(x + size, y + size, z);
            GL11.glTexCoord2f(u0, v0);
            GL11.glVertex3f(x - size, y + size, z);
        }
    }
}
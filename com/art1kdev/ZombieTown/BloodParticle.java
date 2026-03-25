/*
Class BloodParticle
By Art1kDev
*/

package com.art1kdev.Zombietown;

import org.lwjgl.opengl.GL11;

public class BloodParticle extends Particle {
    private float red;
    private float green;
    private float blue;
    private float rotation;
    private float rotationSpeed;

    public BloodParticle(float x, float y, float z, float xd, float yd, float zd) {
        super(x, y, z, xd, yd, zd, 0, 1.0f);

        this.size = 0.1f + (float)Math.random() * 0.05f;
        this.fadeSpeed = 0.02f + (float)Math.random() * 0.01f;
        this.lifetime = 30 + (int)(Math.random() * 20);

        this.red = 0.8f + (float)Math.random() * 0.2f;
        this.green = 0.1f;
        this.blue = 0.1f + (float)Math.random() * 0.1f;

        this.rotation = (float)(Math.random() * Math.PI * 2);
        this.rotationSpeed = (float)(Math.random() - 0.5) * 0.2f;
    }

    @Override
    public void update() {
        super.update();
        rotation += rotationSpeed;
        yd -= 0.005f;
    }

    @Override
    public void render() {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotation * 57.29578f, 0, 0, 1);

        GL11.glColor4f(red, green, blue, alpha);

        float u0 = (tex % 16) / 16.0f;
        float u1 = u0 + 0.0625f;
        float v0 = (tex / 16) / 16.0f;
        float v1 = v0 + 0.0625f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex2f(-size, -size);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(size, -size);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex2f(size, size);
        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex2f(-size, size);
        GL11.glEnd();

        GL11.glPopMatrix();
    }
}
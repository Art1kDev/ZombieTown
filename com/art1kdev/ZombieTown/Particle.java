/*
Class Particle
By Art1kDev
*/

package com.art1kdev.Zombietown;

import org.lwjgl.opengl.GL11;

public class Particle {
    public float x, y, z;
    public float xo, yo, zo;
    protected float xd, yd, zd;
    protected int tex;
    protected int age = 0;
    protected int lifetime = 20;
    protected float size;
    protected float alpha = 1.0f;
    protected float fadeSpeed = 0.05f;

    public Particle(float x, float y, float z, float xd, float yd, float zd, int tex, float sizeMod) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.tex = tex;
        this.size = 0.05f * sizeMod;
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
        yd -= 0.01f;
        alpha -= fadeSpeed;
        if (alpha < 0) alpha = 0;
    }

    public boolean isDead() {
        return age >= lifetime || alpha <= 0;
    }

    public void render() {
        float u0 = (tex % 16) / 16.0f;
        float u1 = u0 + 0.0625f;
        float v0 = (tex / 16) / 16.0f;
        float v1 = v0 + 0.0625f;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
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
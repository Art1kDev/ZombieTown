/*
Class Zombie
By Art1kDev
*/


package com.art1kdev.Zombietown;

import org.lwjgl.opengl.GL11;

public class Zombie {
	public enum ZombieType {
		NORMAL, FAST, TOUGH
	}
	public Cube head;
	public Cube body;
	public Cube arm0;
	public Cube arm1;
	public Cube leg0;
	public Cube leg1;
	public float rot;
	public float timeOffs;
	public float speed;
	public float rotA = (float)(Math.random() + 1.0D) * 0.01F;
	public float x, y, z;
	public float xo, yo, zo;
	public boolean isDying = false;
	public float deathTimer = 0.0f;
	public float fallSpeed = 0.0f;
	public float deathRotX = 0.0f;
	public float deathRotZ = 0.0f;
	public float deathRotSpeedX = 0.0f;
	public float deathRotSpeedZ = 0.0f;
	public float deathKickX = 0.0f;
	public float deathKickZ = 0.0f;
	public float deathShake = 0.0f;
	public float headOffX = 0.0f;
	public float headOffY = 0.0f;
	public float headOffZ = 0.0f;
	public float armOffX0 = 0.0f;
	public float armOffY0 = 0.0f;
	public float armOffZ0 = 0.0f;
	public float armOffX1 = 0.0f;
	public float armOffY1 = 0.0f;
	public float armOffZ1 = 0.0f;
	public float legOffX0 = 0.0f;
	public float legOffY0 = 0.0f;
	public float legOffZ0 = 0.0f;
	public float legOffX1 = 0.0f;
	public float legOffY1 = 0.0f;
	public float legOffZ1 = 0.0f;
	public float headRotX = 0.0f;
	public float headRotZ = 0.0f;
	public float armRotZ0 = 0.0f;
	public float armRotZ1 = 0.0f;
	public float legRotX0 = 0.0f;
	public float legRotX1 = 0.0f;
	public float deathScale = 1.0f;
	public int deathPattern = 0;
	public ZombieType type;
	private long lastParticleSpawn = 0;
	private static final long PARTICLE_SPAWN_INTERVAL = 200;

	public Zombie(float x, float y, float z, ZombieType type) {
		this.type = type;
		this.timeOffs = (float)Math.random() * 1239813.0F;
		this.rot = (float)(Math.random() * Math.PI * 2.0D);
		this.speed = 1.0F;
		this.head = new Cube(0, 0);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
		this.body = new Cube(16, 16);
		this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
		this.arm0 = new Cube(40, 16);
		this.arm0.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
		this.arm0.setPos(-5.0F, 2.0F, 0.0F);
		this.arm1 = new Cube(40, 16);
		this.arm1.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
		this.arm1.setPos(5.0F, 2.0F, 0.0F);
		this.leg0 = new Cube(0, 16);
		this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		this.leg0.setPos(-2.0F, 12.0F, 0.0F);
		this.leg1 = new Cube(0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
		this.leg1.setPos(2.0F, 12.0F, 0.0F);
	}

	public Zombie(float x, float y, float z) {
		this(x, y, z, ZombieType.NORMAL);
	}

	public void tick() {
		this.rot += this.rotA;
		this.rotA = (float)((double)this.rotA * 0.99D);
		this.rotA = (float)((double)this.rotA + (Math.random() - Math.random()) * Math.random() * Math.random() * (double)0.01F);
		if (isDying) {
			deathTimer += 0.1f;
			deathRotX += deathRotSpeedX;
			deathRotZ += deathRotSpeedZ;
			deathRotSpeedX *= 0.97f;
			deathRotSpeedZ *= 0.97f;
			deathKickX *= 0.95f;
			deathKickZ *= 0.95f;
			x += deathKickX;
			z += deathKickZ;
			deathShake *= 0.9f;
			if (deathShake > 0.01f) {
				x += (float)(Math.random() - 0.5) * deathShake;
				z += (float)(Math.random() - 0.5) * deathShake;
			}
			fallSpeed += 0.08f;
			y -= fallSpeed;
			if (y < -3.0f) {
				y = -3.0f;
				fallSpeed = 0.0f;
			}
			if (deathPattern == 0) {
				if (deathTimer > 0.5f) headOffY -= 0.08f;
				if (deathTimer > 0.8f) armOffY0 -= 0.05f;
				if (deathTimer > 1.0f) armOffY1 -= 0.05f;
				if (deathTimer > 1.2f) legOffY0 -= 0.04f;
				if (deathTimer > 1.4f) legOffY1 -= 0.04f;
			} else if (deathPattern == 1) {
				if (deathTimer > 0.3f) armOffX0 -= 0.1f;
				if (deathTimer > 0.5f) armOffX1 += 0.1f;
				if (deathTimer > 0.8f) {
					headRotX += 2.0f;
					headOffY -= 0.05f;
				}
				if (deathTimer > 1.2f) legRotX0 += 1.5f;
				if (deathTimer > 1.4f) legRotX1 -= 1.5f;
			} else {
				if (deathTimer > 0.2f) armRotZ0 -= 2.0f;
				if (deathTimer > 0.4f) armRotZ1 += 2.0f;
				if (deathTimer > 0.6f) {
					headOffX = (float)Math.sin(deathTimer * 10) * 0.3f;
					headOffZ = (float)Math.cos(deathTimer * 10) * 0.3f;
				}
				if (deathTimer > 1.0f) {
					legOffX0 = (float)Math.sin(deathTimer * 5) * 0.5f;
					legOffX1 = (float)Math.cos(deathTimer * 5) * 0.5f;
				}
			}
			if (deathTimer < 2.5f && Math.random() < 0.3f) {
				for (int i = 0; i < 2; i++) {
					float velX = (float)(Math.random() - 0.5) * 0.15f;
					float velY = (float)Math.random() * 0.2f + 0.1f;
					float velZ = (float)(Math.random() - 0.5) * 0.15f;
					float px = x + (float)(Math.random() - 0.5) * 0.5f;
					float pz = z + (float)(Math.random() - 0.5) * 0.5f;
					ParticleManager.addBloodParticle(px, Math.max(y, 0.01f) + (float)Math.random() * 0.5f, pz, velX, velY, velZ);
				}
			}
			if (deathTimer < 1.0f && Math.random() < 0.2f) {
				for (int i = 0; i < 3; i++) {
					float velX = (float)(Math.random() - 0.5) * 0.3f;
					float velY = (float)Math.random() * 0.15f;
					float velZ = (float)(Math.random() - 0.5) * 0.3f;
					float px = x + (float)(Math.random() - 0.5) * 1.0f;
					float py = (float)Math.random() * 0.05f;
					float pz = z + (float)(Math.random() - 0.5) * 1.0f;
					ParticleManager.addBloodParticle(px, py, pz, velX, velY, velZ);
				}
			}
		} else {
			spawnWalkingParticles();
		}
	}

	private void spawnWalkingParticles() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastParticleSpawn > PARTICLE_SPAWN_INTERVAL) {
			if (Math.abs(this.rotA) > 0.001f) {
				lastParticleSpawn = currentTime;
				int particleType = 7;
				float sizeMod = 1.0f;
				if (type == ZombieType.FAST) {
					particleType = 8;
					sizeMod = 0.8f;
				} else if (type == ZombieType.TOUGH) {
					particleType = 9;
					sizeMod = 1.2f;
				}
				for (int i = 0; i < 3; i++) {
					double offsetX = (Math.random() - 0.5) * 0.7;
					double offsetZ = (Math.random() - 0.5) * 0.7;
					float particleX = x + (float)offsetX;
					float particleY = 0.01f;
					float particleZ = z + (float)offsetZ;
					float velX = (float)(Math.random() - 0.5) * 0.03f;
					float velY = (float)(Math.random() * 0.04f) * sizeMod;
					float velZ = (float)(Math.random() - 0.5) * 0.03f;
					ParticleManager.addParticle(particleX, particleY, particleZ,
							velX, velY, velZ, particleType, sizeMod);
				}
			}
		}
	}

	public void hit() {
		if (!isDying) {
			isDying = true;
			deathPattern = (int)(Math.random() * 3);
			deathRotSpeedX = (float)(Math.random() - 0.5) * 15.0f;
			deathRotSpeedZ = (float)(Math.random() - 0.5) * 15.0f;
			deathKickX = (float)(Math.random() - 0.5) * 0.2f;
			deathKickZ = (float)(Math.random() - 0.5) * 0.2f;
			deathShake = 0.3f;
			fallSpeed = -(float)Math.random() * 0.3f;
			for (int i = 0; i < 40; i++) {
				float velX = (float)(Math.random() - 0.5) * 0.35f;
				float velY = (float)Math.random() * 0.5f + 0.1f;
				float velZ = (float)(Math.random() - 0.5) * 0.35f;
				float py = y + 0.2f + (float)Math.random() * 0.8f;
				ParticleManager.addBloodParticle(x, py, z, velX, velY, velZ);
			}
			for (int i = 0; i < 20; i++) {
				float velX = (float)(Math.random() - 0.5) * 0.5f;
				float velY = (float)Math.random() * 0.15f;
				float velZ = (float)(Math.random() - 0.5) * 0.5f;
				float py = (float)Math.random() * 0.1f;
				ParticleManager.addBloodParticle(x, py, z, velX, velY, velZ);
			}
			for (int i = 0; i < 15; i++) {
				float velX = (float)(Math.random() - 0.5) * 0.2f;
				float velY = (float)Math.random() * 0.6f + 0.2f;
				float velZ = (float)(Math.random() - 0.5) * 0.2f;
				float px = x + (float)(Math.random() - 0.5) * 0.3f;
				float pz = z + (float)(Math.random() - 0.5) * 0.3f;
				ParticleManager.addBloodParticle(px, y + 1.0f, pz, velX, velY, velZ);
			}
		}
	}

	public boolean isDead() {
		return isDying && deathTimer >= 3.0f;
	}

	public void render(float a) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture("/char.png", GL11.GL_NEAREST));
		GL11.glPushMatrix();
		double time = (double)System.nanoTime() / 1.0E9D * 10.0D * (double)this.speed + (double)this.timeOffs;
		float size = 0.058333334F;
		float yy = (float)(-Math.abs(Math.sin(time * 0.6662D)) * 5.0D - 23.0D);
		if (isDying) {
			GL11.glColor3f(1.0F, 0.5F, 0.5F);
		} else {
			if (type == ZombieType.FAST) {
				GL11.glColor3f(0.7F, 0.7F, 1.0F);
			} else if (type == ZombieType.TOUGH) {
				GL11.glColor3f(0.5F, 0.3F, 0.3F);
			} else {
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
			}
		}
		GL11.glTranslatef(this.xo + (this.x - this.xo) * a, this.yo + (this.y - this.yo) * a, this.zo + (this.z - this.zo) * a);
		GL11.glScalef(1.0F, -1.0F, 1.0F);
		GL11.glScalef(size, size, size);
		GL11.glTranslatef(0.0F, yy, 0.0F);
		float c = 57.29578F;
		GL11.glRotatef(this.rot * c + 180.0F, 0.0F, 1.0F, 0.0F);
		if (isDying) {
			GL11.glRotatef(deathRotX, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(deathRotZ, 0.0F, 0.0F, 1.0F);
			this.head.yRot = 0.0F;
			this.head.xRot = headRotX;
			this.head.zRot = headRotZ;
			this.arm0.xRot = 0.0F;
			this.arm0.zRot = armRotZ0;
			this.arm1.xRot = 0.0F;
			this.arm1.zRot = armRotZ1;
			this.leg0.xRot = legRotX0;
			this.leg1.xRot = legRotX1;
			if (deathPattern == 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef(headOffX, headOffY, headOffZ);
				this.head.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(armOffX0, armOffY0, armOffZ0);
				this.arm0.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(armOffX1, armOffY1, armOffZ1);
				this.arm1.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(legOffX0, legOffY0, legOffZ0);
				this.leg0.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(legOffX1, legOffY1, legOffZ1);
				this.leg1.render();
				GL11.glPopMatrix();
				this.body.render();
			} else {
				GL11.glPushMatrix();
				GL11.glTranslatef(headOffX, headOffY, headOffZ);
				GL11.glRotatef(headRotX, 1, 0, 0);
				this.head.render();
				GL11.glPopMatrix();
				this.body.render();
				GL11.glPushMatrix();
				GL11.glTranslatef(armOffX0, armOffY0, armOffZ0);
				this.arm0.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(armOffX1, armOffY1, armOffZ1);
				this.arm1.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(legOffX0, legOffY0, legOffZ0);
				this.leg0.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glTranslatef(legOffX1, legOffY1, legOffZ1);
				this.leg1.render();
				GL11.glPopMatrix();
			}
		} else {
			this.head.yRot = (float)Math.sin(time * 0.83D) * 1.0F;
			this.head.xRot = (float)Math.sin(time) * 0.8F;
			this.arm0.xRot = (float)Math.sin(time * 0.6662D + Math.PI) * 2.0F;
			this.arm0.zRot = (float)(Math.sin(time * 0.2312D) + 1.0D) * 1.0F;
			this.arm1.xRot = (float)Math.sin(time * 0.6662D) * 2.0F;
			this.arm1.zRot = (float)(Math.sin(time * 0.2812D) - 1.0D) * 1.0F;
			this.leg0.xRot = (float)Math.sin(time * 0.6662D) * 1.4F;
			this.leg1.xRot = (float)Math.sin(time * 0.6662D + Math.PI) * 1.4F;
			this.head.render();
			this.body.render();
			this.arm0.render();
			this.arm1.render();
			this.leg0.render();
			this.leg1.render();
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}

	class Cube {
		private Vertex[] vertices;
		private Polygon[] polygons;
		private int xTexOffs;
		private int yTexOffs;
		public float x;
		public float y;
		public float z;
		public float xRot;
		public float yRot;
		public float zRot;
		public Cube(int xTexOffs, int yTexOffs) {
			this.xTexOffs = xTexOffs;
			this.yTexOffs = yTexOffs;
		}
		public void addBox(float x0, float y0, float z0, int w, int h, int d) {
			this.vertices = new Vertex[8];
			this.polygons = new Polygon[6];
			float x1 = x0 + (float)w;
			float y1 = y0 + (float)h;
			float z1 = z0 + (float)d;
			Vertex u0 = new Vertex(x0, y0, z0, 0.0F, 0.0F);
			Vertex u1 = new Vertex(x1, y0, z0, 0.0F, 8.0F);
			Vertex u2 = new Vertex(x1, y1, z0, 8.0F, 8.0F);
			Vertex u3 = new Vertex(x0, y1, z0, 8.0F, 0.0F);
			Vertex l0 = new Vertex(x0, y0, z1, 0.0F, 0.0F);
			Vertex l1 = new Vertex(x1, y0, z1, 0.0F, 8.0F);
			Vertex l2 = new Vertex(x1, y1, z1, 8.0F, 8.0F);
			Vertex l3 = new Vertex(x0, y1, z1, 8.0F, 0.0F);
			this.vertices[0] = u0;
			this.vertices[1] = u1;
			this.vertices[2] = u2;
			this.vertices[3] = u3;
			this.vertices[4] = l0;
			this.vertices[5] = l1;
			this.vertices[6] = l2;
			this.vertices[7] = l3;
			this.polygons[0] = new Polygon(new Vertex[]{l1, u1, u2, l2}, this.xTexOffs + d + w, this.yTexOffs + d, this.xTexOffs + d + w + d, this.yTexOffs + d + h);
			this.polygons[1] = new Polygon(new Vertex[]{u0, l0, l3, u3}, this.xTexOffs + 0, this.yTexOffs + d, this.xTexOffs + d, this.yTexOffs + d + h);
			this.polygons[2] = new Polygon(new Vertex[]{l1, l0, u0, u1}, this.xTexOffs + d, this.yTexOffs + 0, this.xTexOffs + d + w, this.yTexOffs + d);
			this.polygons[3] = new Polygon(new Vertex[]{u2, u3, l3, l2}, this.xTexOffs + d + w, this.yTexOffs + 0, this.xTexOffs + d + w + w, this.yTexOffs + d);
			this.polygons[4] = new Polygon(new Vertex[]{u1, u0, u3, u2}, this.xTexOffs + d, this.yTexOffs + d, this.xTexOffs + d + w, this.yTexOffs + d + h);
			this.polygons[5] = new Polygon(new Vertex[]{l0, l1, l2, l3}, this.xTexOffs + d + w + d, this.yTexOffs + d, this.xTexOffs + d + w + d + w, this.yTexOffs + d + h);
		}
		public void setPos(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public void render() {
			float c = 57.29578F;
			GL11.glPushMatrix();
			GL11.glTranslatef(this.x, this.y, this.z);
			GL11.glRotatef(this.zRot * c, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(this.yRot * c, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.xRot * c, 1.0F, 0.0F, 0.0F);
			GL11.glBegin(GL11.GL_QUADS);
			for(int i = 0; i < this.polygons.length; ++i) {
				this.polygons[i].render();
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}

	class Polygon {
		public Vertex[] vertices;
		public int vertexCount;
		public Polygon(Vertex[] vertices) {
			this.vertexCount = 0;
			this.vertices = vertices;
			this.vertexCount = vertices.length;
		}
		public Polygon(Vertex[] vertices, int u0, int v0, int u1, int v1) {
			this(vertices);
			vertices[0] = vertices[0].remap((float)u1, (float)v0);
			vertices[1] = vertices[1].remap((float)u0, (float)v0);
			vertices[2] = vertices[2].remap((float)u0, (float)v1);
			vertices[3] = vertices[3].remap((float)u1, (float)v1);
		}
		public void render() {
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			for(int i = 3; i >= 0; --i) {
				Vertex v = this.vertices[i];
				GL11.glTexCoord2f(v.u / 64.0F, v.v / 32.0F);
				GL11.glVertex3f(v.pos.x, v.pos.y, v.pos.z);
			}
		}
	}

	class Vertex {
		public Vec3 pos;
		public float u;
		public float v;
		public Vertex(float x, float y, float z, float u, float v) {
			this(new Vec3(x, y, z), u, v);
		}
		public Vertex remap(float u, float v) {
			return new Vertex(this, u, v);
		}
		public Vertex(Vertex vertex, float u, float v) {
			this.pos = vertex.pos;
			this.u = u;
			this.v = v;
		}
		public Vertex(Vec3 pos, float u, float v) {
			this.pos = pos;
			this.u = u;
			this.v = v;
		}
	}

	class Vec3 {
		public float x;
		public float y;
		public float z;
		public Vec3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public Vec3 interpolateTo(Vec3 t, float p) {
			float xt = this.x + (t.x - this.x) * p;
			float yt = this.y + (t.y - this.y) * p;
			float zt = this.z + (t.z - this.z) * p;
			return new Vec3(xt, yt, zt);
		}
		public void set(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
/*
Main Class ZombieTown
By Art1kDev
*/
package com.art1kdev.Zombietown;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.BufferUtils;
public class Zombietown {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Zombie Town";
    private static final float WORLD_SIZE = 64.0f;
    private static final float WORLD_MIN = -WORLD_SIZE;
    private static final float WORLD_MAX = WORLD_SIZE;
    private static final float ZOMBIE_SPAWN_RADIUS = 6.0f;
    private static final float ZOMBIE_BOUNDARY = 12.0f;
    private static final float ZOMBIE_MIN = -ZOMBIE_BOUNDARY;
    private static final float ZOMBIE_MAX = ZOMBIE_BOUNDARY;
    private static final float FOG_BOUNDARY = 40.0f;
    private static final float FOG_MIN = -FOG_BOUNDARY;
    private static final float FOG_MAX = FOG_BOUNDARY;
    private static final int WIN_CONDITION_KILLS = 2000;
    private ArrayList<Zombie> zombies = new ArrayList<>();
    private Random random = new Random();
    private float cameraAngleX = 30.0f;
    private float cameraAngleY = 0.0f;
    private long lastSpawnTime = 0;
    private int score = 0;
    private int wave = 1;
    private long lastWaveTime = 0;
    private int zombiesKilledInCurrentWave = 0;
    private int zombiesNeededForNextWave = 10;
    private boolean gameStarted = false;
    private Random noiseRandom = new Random();
    private boolean isMouseOverButton = false;
    private int totalZombiesKilled = 0;
    private boolean gameEnded = false;
    private String endGameMessage = "";
    public static void main(String[] args) {
        Zombietown game = new Zombietown();
        game.start();
    }
    public void start() {
        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Display.destroy();
        }
    }
    private void init() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
        Display.setTitle(TITLE);
        Display.create();
        Keyboard.create();
        Mouse.create();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
        GL11.glFogf(GL11.GL_FOG_DENSITY, 0.05f);
        FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
        fogColor.put(0.0f).put(0.0f).put(0.0f).put(1.0f);
        fogColor.flip();
        GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(60.0f, (float)WIDTH/(float)HEIGHT, 0.1f, 200.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    private void spawnInitialZombies() {
        int initialZombies = 64 + random.nextInt(37);
        for (int i = 0; i < initialZombies; i++) {
            spawnZombie();
        }
    }
    private void loop() {
        while (!Display.isCloseRequested()) {
            if (!gameStarted) {
                renderMenu();
                handleMenuInput();
            } else {
                if (!gameEnded) {
                    update();
                }
                render();
                if (!gameEnded) {
                    handleInput();
                }
            }
            Display.update();
            Display.sync(60);
        }
    }
    private void handleMenuInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            gameStarted = true;
            spawnInitialZombies();
            lastWaveTime = System.currentTimeMillis();
        }
        int mouseX = Mouse.getX();
        int mouseY = HEIGHT - Mouse.getY();
        if (mouseX >= 350 && mouseX <= 450 && mouseY >= 300 && mouseY <= 340) {
            isMouseOverButton = true;
            if (Mouse.isButtonDown(0)) {
                gameStarted = true;
                spawnInitialZombies();
                lastWaveTime = System.currentTimeMillis();
            }
        } else {
            isMouseOverButton = false;
        }
    }
    private void renderMenu() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_FOG);
        renderNoiseBackground();
        renderLogo();
        if (isMouseOverButton) {
            GUIScreen.renderText("PLAY", 350, 310, "/default.png");
        } else {
            GUIScreen.renderText("PLAY", 350, 310, "/default.png");
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private void renderLogo() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture("/logo.png", GL11.GL_NEAREST));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        int logoWidth = 232;
        int logoHeight = 36;
        int logoX = (WIDTH - logoWidth) / 2;
        int logoY = 150;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(logoX, logoY);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(logoX + logoWidth, logoY);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(logoX + logoWidth, logoY + logoHeight);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(logoX, logoY + logoHeight);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    private void renderNoiseBackground() {
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(WIDTH, 0);
        GL11.glVertex2f(WIDTH, HEIGHT);
        GL11.glVertex2f(0, HEIGHT);
        GL11.glEnd();
        GL11.glPointSize(2.0f);
        GL11.glBegin(GL11.GL_POINTS);
        for (int i = 0; i < 200; i++) {
            float x = noiseRandom.nextFloat() * WIDTH;
            float y = noiseRandom.nextFloat() * HEIGHT;
            float intensity = 0.2f + noiseRandom.nextFloat() * 0.3f;
            GL11.glColor3f(intensity, intensity, intensity);
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();
    }
    private void update() {
        long currentTime = System.currentTimeMillis();
        ParticleManager.update();
        if (currentTime - lastWaveTime > 30000) {
            wave++;
            zombiesNeededForNextWave = 10 + (wave * 5);
            lastWaveTime = currentTime;
            int additionalZombies = 5 + (wave * 3);
            for (int i = 0; i < additionalZombies; i++) {
                spawnZombie();
            }
        }
        cameraAngleY += 1.0f;
        if (cameraAngleY >= 360.0f) {
            cameraAngleY = 0.0f;
        }
        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);
            zombie.tick();
            float baseSpeed = 0.03f;
            float speed = baseSpeed + (wave * 0.005f);
            if (zombie.type == Zombie.ZombieType.FAST) {
                speed *= 1.5f;
            } else if (zombie.type == Zombie.ZombieType.TOUGH) {
                speed *= 0.8f;
            }
            float moveX = (float)Math.sin(zombie.rot) * speed;
            float moveZ = (float)Math.cos(zombie.rot) * speed;
            zombie.x += moveX;
            zombie.z += moveZ;
            if (zombie.x < ZOMBIE_MIN) {
                zombie.x = ZOMBIE_MIN;
                zombie.rot = (float)(Math.random() * Math.PI * 2);
            }
            if (zombie.x > ZOMBIE_MAX) {
                zombie.x = ZOMBIE_MAX;
                zombie.rot = (float)(Math.random() * Math.PI * 2);
            }
            if (zombie.z < ZOMBIE_MIN) {
                zombie.z = ZOMBIE_MIN;
                zombie.rot = (float)(Math.random() * Math.PI * 2);
            }
            if (zombie.z > ZOMBIE_MAX) {
                zombie.z = ZOMBIE_MAX;
                zombie.rot = (float)(Math.random() * Math.PI * 2);
            }
            if (zombie.isDead()) {
                zombies.remove(i);
                i--;
                score += 10 * wave;
                totalZombiesKilled++;
                zombiesKilledInCurrentWave++;
                if (totalZombiesKilled >= WIN_CONDITION_KILLS) {
                    gameEnded = true;
                    endGameMessage = "YOU WIN! All 2000 zombies destroyed!";
                } else if (zombiesKilledInCurrentWave >= zombiesNeededForNextWave) {
                    wave++;
                    zombiesKilledInCurrentWave = 0;
                    zombiesNeededForNextWave = 10 + (wave * 5);
                    lastWaveTime = currentTime;
                    int additionalZombies = 5 + (wave * 3);
                    for (int j = 0; j < additionalZombies; j++) {
                        spawnZombie();
                    }
                }
            }
        }
    }
    private void spawnZombie() {
        float x, z;
        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = random.nextDouble() * ZOMBIE_SPAWN_RADIUS;
        x = (float) (Math.cos(angle) * radius);
        z = (float) (Math.sin(angle) * radius);
        Zombie.ZombieType type = Zombie.ZombieType.NORMAL;
        double typeChance = random.nextDouble();
        if (wave >= 3 && typeChance < 0.1) {
            type = Zombie.ZombieType.FAST;
        } else if (wave >= 5 && typeChance < 0.15) {
            type = Zombie.ZombieType.TOUGH;
        }
        Zombie zombie = new Zombie(x, 0, z, type);
        zombie.rot = random.nextFloat() * (float)Math.PI * 2;
        zombies.add(zombie);
    }
    private void handleInput() {
        if (Mouse.isButtonDown(0)) {
            checkZombieClick();
        }
    }
    private void checkZombieClick() {
        int mouseX = Mouse.getX();
        int mouseY = HEIGHT - Mouse.getY();
        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);
            float[] screenPos = worldToScreen(zombie.x, zombie.y, zombie.z);
            if (screenPos == null) continue;
            float dist = (float)Math.sqrt(
                    Math.pow(screenPos[0] - mouseX, 2) +
                            Math.pow(screenPos[1] - mouseY, 2));
            if (dist < 50) {
                zombie.hit();
                break;
            }
        }
    }
    private float[] worldToScreen(float x, float y, float z) {
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        if (GLU.gluProject(x, y, z, modelView, projection, viewport, screenCoords)) {
            return new float[]{screenCoords.get(0), screenCoords.get(1)};
        }
        return null;
    }
    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -15);
        GL11.glRotatef(cameraAngleX, 1, 0, 0);
        GL11.glRotatef(cameraAngleY, 0, 1, 0);
        renderPlatform();
        for (Zombie zombie : zombies) {
            zombie.render(1.0f);
        }
        ParticleManager.render();
        if (gameEnded) {
            renderEndGameScreen();
        } else {
            GUIScreen.render(score, wave, zombies.size());
        }
    }
    private void renderEndGameScreen() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.7f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(WIDTH, 0);
        GL11.glVertex2f(WIDTH, HEIGHT);
        GL11.glVertex2f(0, HEIGHT);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GUIScreen.renderText(endGameMessage, WIDTH / 2 - 150, HEIGHT / 2, "/default.png");
        GUIScreen.renderText("Final Score: " + score, WIDTH / 2 - 100, HEIGHT / 2 + 30, "/default.png");
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    private void renderPlatform() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Textures.loadTexture("/grass.png", GL11.GL_NEAREST));

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        float repeat = WORLD_SIZE / 8.0f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(WORLD_MIN, 0, WORLD_MIN);
        GL11.glTexCoord2f(repeat, 0.0f);
        GL11.glVertex3f(WORLD_MIN, 0, WORLD_MAX);
        GL11.glTexCoord2f(repeat, repeat);
        GL11.glVertex3f(WORLD_MAX, 0, WORLD_MAX);
        GL11.glTexCoord2f(0.0f, repeat);
        GL11.glVertex3f(WORLD_MAX, 0, WORLD_MIN);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor3f(0.7f, 0.7f, 0.7f);
        for (float x = WORLD_MIN; x <= WORLD_MAX; x += 5.0f) {
            GL11.glVertex3f(x, 0.01f, WORLD_MIN);
            GL11.glVertex3f(x, 0.01f, WORLD_MAX);
        }
        for (float z = WORLD_MIN; z <= WORLD_MAX; z += 5.0f) {
            GL11.glVertex3f(WORLD_MIN, 0.01f, z);
            GL11.glVertex3f(WORLD_MAX, 0.01f, z);
        }
        GL11.glEnd();
    }
}
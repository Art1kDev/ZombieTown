package com.art1kdev.Zombietown.online;

import java.util.*;

public class OnlineManager {
    private static OnlineManager instance;
    private PlayerList playerList;
    private BestScore bestScore;
    private boolean isConnected = false;
    private String serverIP = "";
    private long connectionTime = 0;
}
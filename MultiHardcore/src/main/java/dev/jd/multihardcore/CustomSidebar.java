package dev.jd.multihardcore;

import java.util.HashMap;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class CustomSidebar {

    private Scoreboard board;
    private Objective objective;

    private HashMap<Integer, Score> lines;

    public CustomSidebar(Scoreboard board, String name, String title) {
        this.board = board;
        objective = board.registerNewObjective(name, "dummy", name);

        lines = new HashMap<>();

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);
    }

    public void setLine(int lineNumber, String newText) {

        Score oldScore = lines.get(lineNumber);
        if (oldScore != null)
            board.resetScores(oldScore.getEntry());

        Score newScore = objective.getScore(newText);
        newScore.setScore(lineNumber);
        lines.put(lineNumber, newScore);

        oldScore = null;

    }

    public Scoreboard getBoard() {
        return board;
    }

}

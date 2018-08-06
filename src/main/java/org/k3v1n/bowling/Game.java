package org.k3v1n.bowling;

import java.util.*;

public class Game {

    private final Queue<Integer> bonusFrameQueue = new ArrayDeque<>();

    private int currentFrameIndex = 0;
    private int currentFrameRoll = 0;
    private boolean over = false;

    private final Frame[] frames = {
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame(),
            new Frame()
    };

    public Game() {
    }

    public void AddRoll(final int pins) {

        if (over) {
            throw new GameEndException();
        }

        final var currentFrame = frames[currentFrameIndex];

        if (isStrike(pins)) {
            scoreStrike(pins, currentFrame);
            return;
        }

        if (isLastFrame() && currentFrameIsSpareOrStrike()) {
            scoreExtraRollInLastFrame(pins);
        }

        if (rollIsWithinNormalFrame()) {
            var isLastRollInFrame = scoreRoll(pins);

            if (!isLastRollInFrame) {
                return;
            }

            prepareNextFrame();
        }

        if (isLastFrame() && !currentFrameIsSpareOrStrike()) {
            over = true;
        }
    }

    private boolean scoreRoll(int pins) {
        addScoreToFrame(pins, currentFrameIndex);
        setPinsHitForCurrentRollInFrame(pins);

        if (isFirstRollInFrame()) {
            nextRollInFrame();
            return false;
        }

        if (currentFrameIsSpareOrStrike()) {
            bonusFrameQueue.add(currentFrameIndex);
        }

        return true;
    }

    public Frame[] Frames() {
        return frames;
    }

    public int TotalScore() {
        return Arrays.stream(frames).map(f -> f.Score).reduce(Integer::sum).orElse(0);
    }

    public boolean Over() {
        return over;
    }

    private void scoreExtraRollInLastFrame(final int pins) {
        nextRollInFrame();
        frames[currentFrameIndex].Score += pins;
        setPinsHitForCurrentRollInFrame(pins);
    }

    private void scoreStrike(final int pins, final Frame currentFrame) {
        setPinsHitForCurrentRollInFrame(pins);
        currentFrame.Score = pins;

        if (bonusFrameQueue.size() > 0) {
            var bonusFrameIndex = bonusFrameQueue.remove();
            addScoreToFrame(pins, bonusFrameIndex);
        }

        bonusFrameQueue.add(currentFrameIndex);
        bonusFrameQueue.add(currentFrameIndex);

        if (isLastFrame()) {
            nextRollInFrame();
        }

        prepareNextFrame();
    }

    private boolean isLastFrame() {
        return currentFrameIndex == frames.length - 1;
    }

    private boolean rollIsWithinNormalFrame() {
        return currentFrameRoll < 2;
    }

    private boolean isFirstRollInFrame() {
        return currentFrameRoll < 1;
    }

    private void addScoreToFrame(final int pins, final int frameIndex) {
        frames[frameIndex].Score += pins;
        scoreOutstandingBonusPoints(pins);
    }

    private void scoreOutstandingBonusPoints(final int pins) {
        if (bonusFrameQueue.size() > 0) {
            var bonusFrameIndex = bonusFrameQueue.remove();
            frames[bonusFrameIndex].Score += pins;
        }
    }

    private void nextRollInFrame() {
        ++currentFrameRoll;
    }

    private void setPinsHitForCurrentRollInFrame(final int pins) {
        frames[currentFrameIndex].PinsRolled[currentFrameRoll] = pins;
    }

    private void prepareNextFrame() {

        if (!isLastFrame()) {
            currentFrameRoll = 0;
            ++currentFrameIndex;
        }
    }

    private boolean currentFrameIsSpareOrStrike() {
        return frames[currentFrameIndex].Score == 10;
    }

    private static boolean isStrike(final int pins) {
        return pins == 10;
    }
}
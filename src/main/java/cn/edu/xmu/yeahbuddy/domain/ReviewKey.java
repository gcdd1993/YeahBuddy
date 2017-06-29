package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class ReviewKey implements Serializable {

    private static final long serialVersionUID = -6903775283127675781L;

    private int teamId;
    private int stage;
    private int viewer;
    private boolean viewerIsAdmin;

    public ReviewKey() {
    }

    public ReviewKey(int teamId, int stage, int viewer, boolean viewerIsAdmin) {
        this.teamId = teamId;
        this.stage = stage;
        this.viewer = viewer;
        this.viewerIsAdmin = viewerIsAdmin;
    }

    @Contract(pure = true)
    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Contract(pure = true)
    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    @Contract(pure = true)
    public int getViewer() {
        return viewer;
    }

    public void setViewer(int viewer) {
        this.viewer = viewer;
    }

    @Contract(pure = true)
    public boolean isViewerIsAdmin() {
        return viewerIsAdmin;
    }

    public void setViewerIsAdmin(boolean viewerIsAdmin) {
        this.viewerIsAdmin = viewerIsAdmin;
    }

    @Override
    public boolean equals(Object t) {
        return t instanceof ReviewKey && ((ReviewKey) t).getTeamId() == this.getTeamId() && ((ReviewKey) t).getStage() == this.getStage() && ((ReviewKey) t).getViewer() == this.getViewer() && ((ReviewKey) t).isViewerIsAdmin() == this.isViewerIsAdmin();
    }

    @Override
    public int hashCode(){
        return (isViewerIsAdmin() ? Integer.MIN_VALUE : 0) + (teamId << 20) + (stage << 10) + viewer;
    }

}

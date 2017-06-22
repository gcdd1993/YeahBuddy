package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;


@Entity
@IdClass(ReviewKey.class)
public class Review {

    @Id
    @Column(name = "ReviewTeamId", nullable = false)
    private int teamId;

    @Id
    @Column(name = "ReviewStage", nullable = false)
    private int stage;

    @Id
    @Column(name = "ReviewViewer", nullable = false)
    private int viewer;

    @Id
    @Column(name = "ReviewViewerIsAdmin", nullable = false)
    private boolean viewerIsAdmin;

    @Column(name = "ReviewRank", nullable = false)
    private int rank;

    @Column(name = "ReviewText")
    private String text;

    @Column(name = "ReviewSubmitted", nullable = false)
    private boolean submitted;

    public Review() {
    }

    public Review(int teamId, int stage, int viewer, boolean viewerIsAdmin) {
        this.teamId = teamId;
        this.stage = stage;
        this.viewer = viewer;
        this.viewerIsAdmin = viewerIsAdmin;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getStage() {
        return stage;
    }

    public int getViewer() {
        return viewer;
    }

    public boolean isViewerIsAdmin() {
        return viewerIsAdmin;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}

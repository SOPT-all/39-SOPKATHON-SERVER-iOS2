package org.sopt.sopkathon.domain.record.domain;

public enum RecordCategory {
    FEEDING("수유"),
    MEAL("식사"),
    SLEEP("수면"),
    DIAPER("배변"),
    PLAY("놀이"),
    HEALTH("건강");

    private final String description;

    RecordCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

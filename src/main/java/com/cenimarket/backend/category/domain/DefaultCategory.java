package com.cenimarket.backend.category.domain;

public enum DefaultCategory {

    IT_DIGITAL("IT · 디지털", 1),
    HOME_APPLIANCE("가전제품", 2),
    FURNITURE_INTERIOR("가구 · 인테리어", 3),
    LIVING_KITCHEN("생활 · 주방", 4),
    SPORTS_HOBBY("스포츠 · 취미", 5),
    BOOK_MUSIC("도서 · 음반", 6),
    BABY_ETC("육아 · 기타", 7);

    private final String displayName;
    private final int sortOrder;

    DefaultCategory(String displayName, int sortOrder) {
        this.displayName = displayName;
        this.sortOrder = sortOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}

package com.accountbook.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 개인 가계부의 핵심 데이터 엔티티(entity)인 가계부 항목을 나타냅니다.
 * 유효성 검사 로직과 미리 정의된 카테고리를 포함합니다.
 */
public class LedgerItem {
    
    // 상위 카테고리 목록 (6개)
    public static final List<String> VALID_CATEGORIES = Arrays.asList(
        "Food", "Transport", "Living", "Shopping", "Transfer", "Hobby"
    );
    // 계층형 카테고리 정의 (각 상위별 5개 하위)
    public static final String[] TOP_CATEGORIES = { "Food", "Transport", "Living", "Shopping", "Transfer", "Hobby" };
    public static final String[][] SUB_CATEGORIES = {
        { "식재료", "외식", "음료", "간식", "기타" },
        { "지하철", "버스", "택시", "주유", "기타" },
        { "월세", "관리비", "통신비", "공과금", "기타" },
        { "의류", "전자제품", "생활용품", "선물", "기타" },
        { "이체", "송금", "환전", "수수료", "기타" },
        { "운동", "여행", "문화", "게임", "기타" }
    };
    
    // 고유 식별자 (자동 증가)
    private int id;
    // 항목 유형: "수입" 또는 "지출"
    private String type;
    
    // 거래 날짜: 유효해야 하며 2025-10-01 이후여야 함
    private LocalDate date;
    
    // 금액: 1억 이하의 양의 정수
    private int amount;
    
    // 카테고리: 미리 정의된 값 중 하나여야 함
    private String category;
    
    // 내용(설명): 최대 길이 50자의 문자열
    private String description;
    
    // 기본 생성자
    public LedgerItem() {}
    
    // 모든 필드를 포함하는 생성자
    public LedgerItem(int id, String type, LocalDate date, int amount, String category, String description) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }
    
    // ID가 없는 생성자 (새 항목용)
    public LedgerItem(String type, LocalDate date, int amount, String category, String description) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }
    
    // Getter와 Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s | %d | %s",
            id, type, date, category, amount, description != null ? description : "");
    }
    
    /**
     * 카테고리가 유효한지 검사합니다. "상위: 하위" 형식 또는 상위 카테고리만 허용합니다.
     */
    public static boolean isValidCategory(String category) {
        return isValidHierarchicalCategory(category) || Arrays.asList(TOP_CATEGORIES).contains(category);
    }

    /**
     * "상위: 하위" 형식의 카테고리 문자열 유효성 검사
     */
    public static boolean isValidHierarchicalCategory(String category) {
        if (category == null) return false;
        String[] parts = category.split(":");
        if (parts.length != 2) return false;
        String top = parts[0].trim();
        String sub = parts[1].trim();
        int topIndex = -1;
        for (int i = 0; i < TOP_CATEGORIES.length; i++) {
            if (TOP_CATEGORIES[i].equals(top)) {
                topIndex = i;
                break;
            }
        }
        if (topIndex == -1) return false;
        for (String s : SUB_CATEGORIES[topIndex]) {
            if (s.equals(sub)) return true;
        }
        return false;
    }

    /** 상위 카테고리 목록 반환 */
    public static List<String> getTopCategories() {
        return Arrays.asList(TOP_CATEGORIES);
    }

    /** 주어진 상위 카테고리(1-based index)의 하위 카테고리 목록 반환 */
    public static List<String> getSubCategories(int topIndexOneBased) {
        if (topIndexOneBased < 1 || topIndexOneBased > TOP_CATEGORIES.length) return Arrays.asList(new String[0]);
        return Arrays.asList(SUB_CATEGORIES[topIndexOneBased - 1]);
    }
}
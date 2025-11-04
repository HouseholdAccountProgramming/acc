package com.accountbook.util;

import com.accountbook.model.LedgerItem;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * 가계부 데이터의 영속성을 위한 CSV 파일 작업을 처리합니다.
 */
public class CsvFileHandler {
    
    private static final String DEFAULT_FILE_NAME = "ledger.csv";
    private static final String CSV_HEADER = "id,type,date,category,amount,description";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final String fileName;
    
    public CsvFileHandler() {
        this.fileName = DEFAULT_FILE_NAME;
    }
    
    public CsvFileHandler(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * CSV 파일에서 가계부 항목을 불러옵니다.
     * 파일이 존재하지 않거나 오류가 있으면 빈 목록을 반환합니다.
     */
    public List<LedgerItem> loadFromFile() {
        List<LedgerItem> items = new ArrayList<>();
        File file = new File(fileName);
        
        if (!file.exists()) {
            System.out.println("기존 데이터 파일이 없습니다. 빈 가계부로 시작합니다.");
            return items;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // 헤더 건너뛰기
            
            if (line == null || !line.equals(CSV_HEADER)) {
                System.out.println("경고: 유효하지 않거나 누락된 CSV 헤더입니다. 빈 가계부로 시작합니다.");
                return items;
            }
            
            int lineNumber = 2;
            while ((line = reader.readLine()) != null) {
                try {
                    LedgerItem item = parseCsvLine(line);
                    if (item != null) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    System.out.printf("경고: %d번 줄의 유효하지 않은 항목을 건너뜁니다: %s%n", lineNumber, e.getMessage());
                }
                lineNumber++;
            }
            
            System.out.printf("%s에서 %d개의 항목을 불러왔습니다.%n", fileName, items.size());
            
        } catch (IOException e) {
            System.err.printf("파일 %s 읽기 오류: %s%n", fileName, e.getMessage());
        }
        
        return items;
    }
    
    /**
     * 가계부 항목 목록을 CSV 파일에 저장합니다.
     */
    public boolean saveToFile(List<LedgerItem> items) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            // 헤더 작성
            writer.println(CSV_HEADER);
            
            // 데이터 작성
            for (LedgerItem item : items) {
                writer.println(formatCsvLine(item));
            }
            
            System.out.printf("%s에 %d개의 항목을 저장했습니다.%n", fileName, items.size());
            return true;
            
        } catch (IOException e) {
            System.err.printf("파일 %s 쓰기 오류: %s%n", fileName, e.getMessage());
            return false;
        }
    }
    
    /**
     * CSV 한 줄을 LedgerItem 객체로 파싱합니다.
     */
    private LedgerItem parseCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",", -1); // -1은 비어 있는 끝 필드도 포함하기 위함
        
        if (parts.length != 6) {
            throw new IllegalArgumentException("유효하지 않은 CSV 형식: 6개의 필드가 필요하지만, " + parts.length + "개가 발견되었습니다.");
        }
        
        try {
            int id = Integer.parseInt(parts[0].trim());
            String type = parts[1].trim();
            LocalDate date = LocalDate.parse(parts[2].trim(), DATE_FORMATTER);
            String category = parts[3].trim();
            int amount = Integer.parseInt(parts[4].trim());
            String descriptionRaw = parts[5].trim();
            String description = descriptionRaw.isEmpty() ? null : descriptionRaw;
            
            // 엄격 유효성 검사
            if (!("수입".equals(type) || "지출".equals(type))) {
                throw new IllegalArgumentException("유효하지 않은 유형: " + type);
            }
            if (!date.isAfter(LocalDate.of(2025, 10, 1))) {
                throw new IllegalArgumentException("날짜는 2025-10-01 이후여야 합니다.");
            }
            if (!LedgerItem.isValidCategory(category)) {
                throw new IllegalArgumentException("유효하지 않은 카테고리: " + category);
            }
            if (amount <= 0 || amount > 100_000_000) {
                throw new IllegalArgumentException("유효하지 않은 금액: " + amount);
            }
            if (description != null) {
                if (description.length() > 50) {
                    throw new IllegalArgumentException("설명이 50자를 초과합니다.");
                }
                if (!description.isEmpty() && description.matches("^[^a-zA-Z0-9\\s가-힣]+$")) {
                    throw new IllegalArgumentException("설명은 특수문자로만 구성될 수 없습니다.");
                }
            }
            
            return new LedgerItem(id, type, date, amount, category, description);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("CSV 줄 파싱 오류: " + e.getMessage());
        }
    }
    
    /**
     * LedgerItem 객체를 CSV 한 줄로 포맷합니다.
     */
    private String formatCsvLine(LedgerItem item) {
        return String.format("%d,%s,%s,%s,%d,%s",
            item.getId(),
            item.getType(),
            item.getDate().format(DATE_FORMATTER),
            item.getCategory(),
            item.getAmount(),
            item.getDescription() != null ? item.getDescription() : ""
        );
    }
    
    /**
     * 데이터 파일이 존재하는지 확인합니다.
     */
    public boolean fileExists() {
        return new File(fileName).exists();
    }
    
    /**
     * 사용 중인 파일명을 가져옵니다.
     */
    public String getFileName() {
        return fileName;
    }
}
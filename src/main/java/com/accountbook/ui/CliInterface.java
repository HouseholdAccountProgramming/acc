package com.accountbook.ui;

import com.accountbook.model.LedgerItem;
import com.accountbook.service.LedgerService;
import com.accountbook.util.FileFormat;
import com.accountbook.util.ValidationUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * 개인 가계부 애플리케이션을 위한 명령줄 인터페이스입니다.
 */
public class CliInterface {
    
    private final Scanner scanner;
    private final LedgerService ledgerService;
    private boolean running;
    
    public CliInterface() {
        this.scanner = new Scanner(System.in);
        this.ledgerService = new LedgerService();
        this.running = true;
    }
    
    public CliInterface(String fileName) {
        this.scanner = new Scanner(System.in);
        this.ledgerService = new LedgerService(fileName);
        this.running = true;
    }
    
    /**
     * CLI 애플리케이션을 시작합니다.
     */
    public void start() {
        System.out.println("개인 가계부에 오신 것을 환영합니다!");
        System.out.printf("데이터 파일: %s%n", ledgerService.getFileName());
        System.out.printf("기존 항목 %d개를 불러왔습니다.%n%n", ledgerService.getItemCount());
        
        while (running) {
            showMainMenu();
            handleMainMenuChoice();
        }
        
        System.out.println("개인 가계부를 이용해 주셔서 감사합니다!");
        scanner.close();
    }
    
    /**
     * 메인 메뉴를 표시합니다.
     */
    private void showMainMenu() {
        System.out.println("==== 개인 가계부 ====");
        System.out.printf("현재 파일 형식: %s%n", ledgerService.getCurrentFormat().getDescription());
        System.out.println("1. 내역 관리");
        System.out.println("   1.1 내역 추가");
        System.out.println("   1.2 내역 삭제");
        System.out.println("2. 내역 조회");
        System.out.println("   2.1 전체 보기");
        System.out.println("   2.2 날짜 범위별 보기");
        System.out.println("   2.3 카테고리별 보기");
        System.out.println("3. 파일에 저장");
        System.out.println("4. 파일 불러오기");
        System.out.println("5. 파일 형식 변경");
        System.out.println("6. 프로그램 종료");
        System.out.println();
        System.out.print("옵션 선택: ");
    }
    
    /**
     * 메인 메뉴 선택을 처리합니다.
     */
    private void handleMainMenuChoice() {
        String input = scanner.nextLine();
        ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 6);
        
        if (!result.isValid()) {
            System.out.println("오류: " + result.getErrorMessage());
            System.out.println();
            return;
        }
        
        int choice = result.getValue(Integer.class);
        System.out.println();
        
        switch (choice) {
            case 1:
                handleManageItemsMenu();
                break;
            case 2:
                handleViewItemsMenu();
                break;
            case 3:
                saveToFile();
                break;
            case 4:
                loadFromFile();
                break;
            case 5:
                changeFileFormat();
                break;
            case 6:
                running = false;
                break;
        }
    }
    
    /**
     * 내역 관리 서브메뉴를 처리합니다.
     */
    private void handleManageItemsMenu() {
        System.out.println("=== 내역 관리 ===");
        System.out.println("1. 내역 추가");
        System.out.println("2. 내역 삭제");
        System.out.print("옵션 선택: ");
        
        String input = scanner.nextLine();
        ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 2);
        
        if (!result.isValid()) {
            System.out.println("오류: " + result.getErrorMessage());
            System.out.println();
            return;
        }
        
        int choice = result.getValue(Integer.class);
        System.out.println();
        
        switch (choice) {
            case 1:
                addItem();
                break;
            case 2:
                deleteItem();
                break;
        }
    }
    
    /**
     * 내역 조회 서브메뉴를 처리합니다.
     */
    private void handleViewItemsMenu() {
        System.out.println("=== 내역 조회 ===");
        System.out.println("1. 전체 보기");
        System.out.println("2. 날짜 범위별 보기");
        System.out.println("3. 카테고리별 보기");
        System.out.print("옵션 선택: ");
        
        String input = scanner.nextLine();
        ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 3);
        
        if (!result.isValid()) {
            System.out.println("오류: " + result.getErrorMessage());
            System.out.println();
            return;
        }
        
        int choice = result.getValue(Integer.class);
        System.out.println();
        
        switch (choice) {
            case 1:
                viewAllItems();
                break;
            case 2:
                viewItemsByDateRange();
                break;
            case 3:
                viewItemsByCategory();
                break;
        }
    }
    
    /**
     * 가계부에 새 항목을 추가합니다.
     */
    private void addItem() {
        System.out.println("=== 새 내역 추가 ===");
        
        // 유형 가져오기 (수입/지출)
        String type;
        while (true) {
            System.out.print("유형 입력 (수입/지출): ");
            String input = scanner.nextLine();
            ValidationUtil.ValidationResult result = ValidationUtil.validateType(input);
            if (result.isValid()) {
                type = result.getValue(String.class);
                break;
            } else {
                System.out.println("오류: " + result.getErrorMessage());
            }
        }
        
        // 날짜 가져오기
        LocalDate date = getValidDate("날짜 입력 (YYYY-MM-DD): ");
        if (date == null) return;
        
        // 금액 가져오기
        Integer amount = getValidAmount("금액 입력: ");
        if (amount == null) return;
        
        // 카테고리 가져오기 (TODO: 2단계 선택 로직 도입 예정)
        String category = getValidCategory("카테고리 입력 (예: 상위: 하위 또는 상위만): ");
        if (category == null) return;
        
        // 설명 가져오기
        String description = getValidDescription("설명 입력 (선택 사항, 최대 50자): ");
        if (description == null) return;
        
        // 항목 추가
        boolean success = ledgerService.addItem(type, date, amount, category, description);
        if (!success) {
            System.out.println("파일에 항목을 저장하지 못했습니다.");
        }
        
        System.out.println();
    }
    
    /**
     * 가계부에서 항목을 삭제합니다.
     */
    private void deleteItem() {
        System.out.println("=== 내역 삭제 ===");
        
        if (ledgerService.getItemCount() == 0) {
            System.out.println("삭제할 항목이 없습니다.");
            System.out.println();
            return;
        }
        
        // 현재 항목 표시
        System.out.println("현재 항목:");
        ledgerService.displayItems(ledgerService.getAllItems());
        System.out.println();
        
        System.out.print("삭제할 항목의 ID 입력: ");
        String input = scanner.nextLine();
        
        try {
            int id = Integer.parseInt(input.trim());
            ledgerService.deleteItem(id);
        } catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력해주세요.");
        }
        
        System.out.println();
    }
    
    /**
     * 모든 항목을 조회합니다.
     */
    private void viewAllItems() {
        System.out.println("=== 전체 내역 ===");
        List<LedgerItem> items = ledgerService.getAllItems();
        ledgerService.displayItems(items);
        System.out.println();
    }
    
    /**
     * 날짜 범위별로 항목을 조회합니다.
     */
    private void viewItemsByDateRange() {
        System.out.println("=== 날짜 범위별 보기 ===");
        
        LocalDate startDate = getValidDate("시작 날짜 입력 (YYYY-MM-DD): ");
        if (startDate == null) return;
        
        LocalDate endDate = getValidDate("종료 날짜 입력 (YYYY-MM-DD): ");
        if (endDate == null) return;
        
        if (startDate.isAfter(endDate)) {
            System.out.println("오류: 시작 날짜가 종료 날짜보다 뒤일 수 없습니다.");
            System.out.println();
            return;
        }
        
        List<LedgerItem> items = ledgerService.getItemsByDateRange(startDate, endDate);
        System.out.printf("%s부터 %s까지의 항목:%n", startDate, endDate);
        ledgerService.displayItems(items);
        System.out.println();
    }
    
    /**
     * 카테고리별로 항목을 조회합니다.
     */
    private void viewItemsByCategory() {
        System.out.println("=== 카테고리별 보기 ===");
        
        String category = getValidCategory("카테고리 입력 (" + String.join(", ", LedgerItem.VALID_CATEGORIES) + "): ");
        if (category == null) return;
        
        List<LedgerItem> items = ledgerService.getItemsByCategory(category);
        System.out.printf("'%s' 카테고리의 항목:%n", category);
        ledgerService.displayItems(items);
        System.out.println();
    }
    
    /**
     * 데이터를 파일에 저장합니다.
     */
    private void saveToFile() {
        System.out.println("=== 파일에 저장 ===");
        boolean success = ledgerService.saveData();
        if (!success) {
            System.out.println("파일에 데이터를 저장하지 못했습니다.");
        }
        System.out.println();
    }
    
    /**
     * 파일에서 데이터를 불러옵니다.
     */
    private void loadFromFile() {
        System.out.println("=== 파일 불러오기 ===");
        System.out.print("현재 데이터가 덮어씌워집니다. 계속하시겠습니까? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            boolean success = ledgerService.loadData(true);
            if (!success) {
                System.out.println("파일에서 데이터를 불러오지 못했습니다.");
            }
        } else {
            System.out.println("불러오기가 취소되었습니다.");
        }
        System.out.println();
    }
    
    /**
     * 파일 형식을 변경합니다.
     */
    private void changeFileFormat() {
        System.out.println("=== 파일 형식 변경 ===");
        System.out.printf("현재 형식: %s%n", ledgerService.getCurrentFormat().getDescription());
        System.out.println();
        
        FileFormat[] formats = LedgerService.getSupportedFormats();
        System.out.println("지원되는 파일 형식:");
        for (int i = 0; i < formats.length; i++) {
            String current = formats[i] == ledgerService.getCurrentFormat() ? " (현재)" : "";
            System.out.printf("%d. %s%s%n", i + 1, formats[i].getDescription(), current);
        }
        System.out.println();
        
        System.out.print("새 파일 형식을 선택하세요 (1-" + formats.length + "): ");
        String input = scanner.nextLine();
        
        ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, formats.length);
        if (!result.isValid()) {
            System.out.println("오류: " + result.getErrorMessage());
            System.out.println();
            return;
        }
        
        int choice = result.getValue(Integer.class);
        FileFormat selectedFormat = formats[choice - 1];
        
        if (selectedFormat == ledgerService.getCurrentFormat()) {
            System.out.println("이미 선택된 형식입니다.");
        } else {
            System.out.printf("%s(으)로 형식을 변경하시겠습니까? (y/N): ", selectedFormat.getDescription());
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                boolean success = ledgerService.changeFormat(selectedFormat);
                if (!success) {
                    System.out.println("파일 형식 변경에 실패했습니다.");
                }
            } else {
                System.out.println("형식 변경이 취소되었습니다.");
            }
        }
        System.out.println();
    }
    
    // 입력 유효성 검사를 위한 헬퍼 메서드
    
    private LocalDate getValidDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            
            ValidationUtil.ValidationResult result = ValidationUtil.validateDate(input);
            if (result.isValid()) {
                return result.getValue(LocalDate.class);
            } else {
                System.out.println("오류: " + result.getErrorMessage());
            }
        }
    }
    
    private Integer getValidAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            
            ValidationUtil.ValidationResult result = ValidationUtil.validateAmount(input);
            if (result.isValid()) {
                return result.getValue(Integer.class);
            } else {
                System.out.println("오류: " + result.getErrorMessage());
            }
        }
    }
    
    private String getValidCategory(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            
            ValidationUtil.ValidationResult result = ValidationUtil.validateCategory(input);
            if (result.isValid()) {
                return result.getValue(String.class);
            } else {
                System.out.println("오류: " + result.getErrorMessage());
            }
        }
    }
    
    private String getValidDescription(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            
            ValidationUtil.ValidationResult result = ValidationUtil.validateDescription(input);
            if (result.isValid()) {
                return result.getValue(String.class);
            } else {
                System.out.println("오류: " + result.getErrorMessage());
            }
        }
    }
}
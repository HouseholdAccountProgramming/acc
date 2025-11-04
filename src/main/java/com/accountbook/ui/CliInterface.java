package com.accountbook.ui;

import com.accountbook.model.LedgerItem;
import com.accountbook.service.LedgerService;
import com.accountbook.util.FileFormat;
import com.accountbook.util.ValidationUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.Optional; 


// 개인 가계부 애플리케이션을 위한 명령줄 인터페이스입니다.
public class CliInterface {
	
	private final Scanner scanner;
	private final LedgerService ledgerService;
	private boolean running;
	
	public CliInterface() {
		// 💡 수정: 생성 시 인코딩을 StandardCharsets.UTF_8로 명시
		this.scanner = new Scanner(System.in, StandardCharsets.UTF_8.name()); 
		this.ledgerService = new LedgerService();
		this.running = true;
	}
	
	public CliInterface(String fileName) {
		// 💡 수정: 생성 시 인코딩을 StandardCharsets.UTF_8로 명시
		this.scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
		this.ledgerService = new LedgerService(fileName);
		this.running = true;
	}
	
	// CLI 애플리케이션을 시작합니다.

	public void start() {
		System.out.println("개인 가계부에 오신 것을 환영합니다!");
		// 💡 주의: 기획서에 파일명을 명시적으로 출력하라는 내용은 없으나, 편의상 유지.
		System.out.printf("데이터 파일: %s%n", ledgerService.getFileName());
		System.out.printf("기존 항목 %d개를 불러왔습니다.%n%n", ledgerService.getItemCount());
		
		while (running) {
			showMainMenu();
			handleMainMenuChoice();
		}
		
		System.out.println("개인 가계부를 이용해 주셔서 감사합니다!");
		scanner.close();
	}
	
	// 메인 메뉴를 표시합니다.
	private void showMainMenu() {
		System.out.println("==== 개인 가계부 ====");
		System.out.printf("현재 파일 형식: %s%n", ledgerService.getCurrentFormat().getDescription());
		System.out.println("1. 내역 관리");
		System.out.println(" 1.1 내역 추가");
		System.out.println(" 1.2 내역 삭제");
		System.out.println(" 1.3 내역 수정"); // Edit
		System.out.println("2. 내역 조회");
		System.out.println(" 2.1 전체 보기");
		System.out.println(" 2.2 날짜 범위별 보기");
		System.out.println(" 2.3 카테고리별 보기");
		System.out.println("3. 파일 불러오기"); // Load
		System.out.println("4. 파일 형식 변경"); // Change Format
		System.out.println("5. 프로그램 종료");
		System.out.println();
		System.out.print("옵션 선택: ");
	}
	
	// 메인 메뉴 선택을 처리합니다.
	private void handleMainMenuChoice() {
		String input = scanner.nextLine();
		// 💡 메인 메뉴 항목이 6개에서 5개로 변경되었으므로, 범위도 1~5로 변경
		ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 5); 
		
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
			// 💡 case 3: 기존의 파일 저장(saveToFile)이 삭제되고, 파일 불러오기(loadFromFile)가 3번으로 이동
			case 3:
				loadFromFile();
				break;
			// 💡 case 4: 파일 형식 변경(changeFileFormat)이 4번으로 이동
			case 4:
				changeFileFormat();
				break;
			// 💡 case 5: 프로그램 종료가 5번으로 이동
			case 5:
				running = false;
				break;
		}
	}
	
	// 내역 관리 서브메뉴를 처리합니다.
	private void handleManageItemsMenu() {
		System.out.println("=== 내역 관리 ===");
		System.out.println("1. 내역 추가");
		System.out.println("2. 내역 삭제");
		System.out.println("3. 내역 수정"); // 💡 내역 수정 메뉴 추가
		System.out.print("옵션 선택: ");
		
		String input = scanner.nextLine();
		// 💡 내역 관리 항목이 2개에서 3개로 변경되었으므로, 범위도 1~3으로 변경
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
				addItem();
				break;
			case 2:
				deleteItem();
				break;
			// 💡 case 3: 내역 수정 기능 추가
			case 3:
				editItem();
				break;
		}
	}
	
	// 내역 조회 서브메뉴를 처리합니다.
	private void handleViewItemsMenu() { // 💡 void 키워드 중복 제거 (수정 완료)
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
	
	// 가계부에 새 항목을 추가합니다.
	private void addItem() {
		System.out.println("=== 새 내역 추가 ===");

		String type; // "수입" 또는 "지출"이 저장될 변수

		while (true) {
			// 💡 수정: 유형 입력을 '수입/지출' 대신 '1/2' 숫자로 받도록 변경
			System.out.print("유형 입력 [1: 수입 (+), 2: 지출 (-)]: ");
			String input = scanner.nextLine(); 
			
			// 취소 기능 구현을 위한 'cancel' 체크 (기획서 3.1.2.4 및 3.1.2.5 참고)
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return;
				continue;
			}

			// ValidationUtil을 사용하여 입력값의 유효성(1 또는 2) 검증
			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 2);
			
			if (!result.isValid()) {
				System.out.println("오류: " + result.getErrorMessage());
				continue;
			}

			int choice = result.getValue(Integer.class);
			if (choice == 1) { // 1 선택 시 수입
				type = "수입";
				break;
			} else { // 2 선택 시 지출
				type = "지출";
				break;
			}
		}
		// 날짜 가져오기
		LocalDate date = getValidDate("날짜 입력 (YYYY-MM-DD): ");
		if (date == null) return;

		// 금액 가져오기
		Integer amount = getValidAmount("금액 입력: ");
		if (amount == null) return;
		
		// LedgerService에 전달할 최종 부호를 여기서 결정합니다.
		String typeSymbol;

		// 💡 금액 부호 적용 로직: type 변수 ("수입" 또는 "지출")을 사용하여 정확히 비교합니다.
		if (type.equals("지출")) { 
			amount = -Math.abs(amount); // 지출은 음수로 저장
			typeSymbol = " 지출 (-)"; 
		} else { // type.equals("수입")
			amount = Math.abs(amount); // 수입은 양수로 저장
			typeSymbol = "수입 (+)"; 
		}

		// 카테고리 가져오기
		String category = getValidCategory("카테고리 입력: ");
		if (category == null) return;

		// 설명 가져오기
		String description = getValidDescription("설명 입력 (선택 사항, 최대 50자 이내): ");
		if (description == null) return;

		// 💡 항목 추가 로직: typeSymbol (+ 또는 -)을 사용하여 LedgerService에 전달합니다.
		// 💡 기획서 3.2.1에 따라, 항목 추가 시 파일에 즉시 반영되어야 합니다. (Service에서 처리 가정)
		boolean success = ledgerService.addItem(typeSymbol, date, amount, category, description); 
		if (!success) {
			System.out.println("오류: 파일에 항목을 저장하지 못했습니다.");
		} else {
			System.out.println("항목이 성공적으로 추가되었습니다.");
		}
		System.out.println();
	}
	// 가계부에서 항목을 삭제합니다.
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
		
		// 취소 기능 구현을 위한 'cancel' 체크 (기획서 3.1.2.4 및 3.1.2.5 참고)
		if ("cancel".equalsIgnoreCase(input.trim())) {
			if (confirmCancel()) return;
			// 'cancel' 취소 후 재입력 로직은 복잡해지므로, 여기서는 취소 후 메인 메뉴 복귀만 처리했습니다.
		}

		try {
			int id = Integer.parseInt(input.trim());
			boolean deleted = ledgerService.deleteItem(id);// 삭제 후 파일 동기화는 Service에서 수행 가정
			if (deleted) {
				System.out.printf("ID %d 항목이 성공적으로 삭제되었습니다.%n", id);
			} else {
				// 기획서 3.1.2.2에 따라 ID 존재하지 않으면 오류 메시지 출력 후 삭제 취소
				System.out.println("오류: 해당 ID의 항목이 존재하지 않습니다.");
			}
		} catch (NumberFormatException e) {
			System.out.println("오류: 유효한 ID 번호를 입력해주세요.");
		}
		
		System.out.println();
	}
	
	//💡 내역 수정 기능을 처리합니다.
	private void editItem() {
		System.out.println("=== 내역 수정 ===");

		if (ledgerService.getItemCount() == 0) {
			System.out.println("수정할 항목이 없습니다.");
			System.out.println();
			return;
		}
		
		// 현재 항목 표시
		System.out.println("현재 항목:");
		ledgerService.displayItems(ledgerService.getAllItems());
		System.out.println();

		System.out.print("수정할 항목의 ID 입력: ");
		String idInput = scanner.nextLine();
		
		// 취소 기능 구현을 위한 'cancel' 체크
		if ("cancel".equalsIgnoreCase(idInput.trim())) {
			if (confirmCancel()) return;
		}
		
		try {
			int id = Integer.parseInt(idInput.trim());
			
			// 💡 수정: Optional을 사용하여 항목 존재 여부 안전하게 확인
			Optional<LedgerItem> itemOpt = ledgerService.getAllItems().stream()
				.filter(item -> item.getId() == id)
				.findFirst();

			if (itemOpt.isEmpty()) {
					// 기획서 1.9.7에 따라 ID가 존재하지 않으면 오류 메시지 출력 후 메인화면으로 이동
				System.out.println("오류: 해당 ID의 항목이 존재하지 않습니다.");
				System.out.println();
				return;
			}
			
			LedgerItem itemToEdit = itemOpt.get();

			// 수정 로직 시작 (기획서 3.1.2.4의 상세 동작을 간략화)
			System.out.println("--- 항목 수정 모드 ---");
			System.out.printf("ID: %d%n", itemToEdit.getId());
			// 💡 수정: 금액 부호로 '수입' 또는 '지출'을 결정합니다.
			String currentType = itemToEdit.getAmount() >= 0 ? "수입" : "지출";
			System.out.printf("1. 유형: %s%n", currentType); 
			System.out.printf("2. 날짜: %s%n", itemToEdit.getDate());
			System.out.printf("3. 금액: %d%n", Math.abs(itemToEdit.getAmount())); // 절대값으로 표시
			System.out.printf("4. 카테고리: %s%n", itemToEdit.getCategory());
			System.out.printf("5. 내용: %s%n", itemToEdit.getDescription()); 
			System.out.println("---------------------");
			
			// 💡 수정할 필드 선택 메뉴 출력 및 번호 범위 수정
			System.out.println("수정할 항목을 선택하세요:");
			System.out.println("1. 유형 (수입/지출) 2. 날짜 3. 금액 4. 카테고리 5. 내용");
			System.out.print("선택 > (취소: 'cancel' 입력): ");
			
			String fieldChoiceInput = scanner.nextLine().trim();
			
			// 취소 로직은 기획서 3.1.2.4 및 3.1.2.5에 따라 'cancel' 키워드로 처리
			if ("cancel".equalsIgnoreCase(fieldChoiceInput)) {
				if (confirmCancel()) return;
			}
			
			// 필드 선택 유효성 검증 (1~5 범위로 변경)
			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(fieldChoiceInput, 1, 5); // 💡 범위 1, 5로 변경
			if (!result.isValid()) {
				// 기획서 예외 처리 9번: "유효하지 않은 메뉴 번호입니다."
				System.out.println("오류: " + result.getErrorMessage());
				System.out.println();
				return;
			}

			// 선택된 필드에 따라 수정 로직 수행
			int fieldChoice = result.getValue(Integer.class);
			
			// 💡 수정 로직 구현: 선택된 필드에 따라 사용자 입력을 받고 유효성 검사 수행
			Object newValue = null;

			switch (fieldChoice) {
				case 1: // 유형 (Type: 수입/지출)
					String newType = getValidType("유형 입력 [1: 수입 (+), 2: 지출 (-)]: ");
					if (newType == null) return; 
					
					// 💡 유형 수정: 금액의 절대값을 유지한 채 새 유형에 따라 부호를 변경
					// newValue는 (양수 또는 음수) 금액이 됩니다.
					newValue = newType.equals("지출") ? -Math.abs(itemToEdit.getAmount()) : Math.abs(itemToEdit.getAmount());
					break;
				case 2: // 날짜 (Date)
					newValue = getValidDate("날짜 입력 (YYYY-MM-DD): ");
					break;
				case 3: // 금액 (Amount)
					Integer newAmount = getValidAmount("금액 입력: ");
					if (newAmount == null) return;

					// 💡 금액 수정: 기존 유형(currentType)에 따라 새 금액에 부호를 적용
					// newValue는 (양수 또는 음수) 금액이 됩니다.
					newValue = currentType.equals("지출") ? -Math.abs(newAmount) : Math.abs(newAmount);
					break;
				case 4: // 카테고리 (Category)
					newValue = getValidCategory("카테고리 입력: ");
					break;
				case 5: // 내용 (Description/Note)
					newValue = getValidDescription("내용 입력 (선택사항, 최대 50자): ");
					break;
			}
			
			if (newValue == null && fieldChoice != 1) { 
				// 유형 필드(1) 외의 필드에서 취소/유효성 검사 실패 시
				System.out.println("수정 작업이 취소되었습니다.");
				return;
			}
            
            // ----------------------------------------------------------------------------------
            // 💡 문제 해결 핵심 수정: editItemField() 호출 대신 LedgerItem 객체에 직접 반영 및 saveData() 호출
            // ----------------------------------------------------------------------------------
            updateItemToEdit(itemToEdit, fieldChoice, newValue); // LedgerItem 객체에 직접 변경 사항 반영
            
			// 💡 수정: LedgerService의 기존 저장 메서드 호출 (파일 동기화)
			boolean success = ledgerService.saveData();
			// ----------------------------------------------------------------------------------
			
			if (success) {
				// 💡 기획서 3.2.1에 따라, 수정 완료 후 파일에 즉시 반영되어야 합니다. (Service에서 처리 가정)
				System.out.println("항목이 성공적으로 수정되었습니다.");
			} else {
				System.out.println("수정 작업이 취소되었거나 파일 저장에 실패했습니다.");
			}
			
		} catch (NumberFormatException e) {
			System.out.println("오류: 유효한 ID 번호를 입력해주세요.");
		}
		System.out.println();
	}
    
    // 💡 추가된 헬퍼 메서드: LedgerItem 객체에 변경 사항을 직접 반영합니다.
    private void updateItemToEdit(LedgerItem itemToEdit, int fieldChoice, Object newValue) {
        if (newValue == null) return;

        // LedgerItem의 setter가 존재한다고 가정하고 값을 업데이트합니다.
        // 이는 UI 계층에서 데이터 모델을 직접 조작하는 것이므로 계층 분리 관점에서는 이상적이지 않으나, 
        // Service 메서드 구현 없이 컴파일 오류를 회피하기 위한 해결책입니다.
        try {
            switch (fieldChoice) {
                case 1: // 유형 (Type) - 금액 필드를 수정
                    // 금액 필드가 수입/지출을 나타내므로 amount를 직접 수정 (newValue는 부호가 적용된 금액)
                    itemToEdit.setAmount((Integer)newValue); 
                    // type 필드도 있다면 여기서 업데이트해야 합니다. (LedgerItem 클래스 정의에 따라 다름)
                    break; 
                case 2: // 날짜 (Date)
                    itemToEdit.setDate((LocalDate)newValue);
                    break; 
                case 3: // 금액 (Amount)
                    // newValue는 부호가 적용된 금액입니다.
                    itemToEdit.setAmount((Integer)newValue);
                    break;
                case 4: // 카테고리 (Category)
                    itemToEdit.setCategory((String)newValue);
                    break;
                case 5: // 내용 (Description/Note)
                    itemToEdit.setDescription((String)newValue);
                    // itemToEdit.setNote((String)newValue); // Note 필드도 있다면
                    break;
            }
        } catch (Exception e) {
             System.out.println("오류: 데이터 모델 업데이트 중 예외 발생: " + e.getMessage());
        }
    }
    
// 💡 새로운 헬퍼 메서드: 유형 입력 (수정 메뉴에서 사용)
	private String getValidType(String prompt) {
		while (true) {
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine(); 
			
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return null; 
				continue;
			}
			
			// 1 또는 2의 숫자 입력만 허용
			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 2);
			
			if (result.isValid()) {
				int choice = result.getValue(Integer.class);
				return choice == 1 ? "수입" : "지출";
			} else { 
				System.out.println("오류: " + result.getErrorMessage());
				continue;
			}
		}
	}

	// 모든 항목을 조회합니다.
	private void viewAllItems() {
		System.out.println("=== 전체 내역 ===");
		List<LedgerItem> items = ledgerService.getAllItems();
		ledgerService.displayItems(items);
		System.out.println();
	}
	
	// 날짜 범위별로 항목을 조회합니다.
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
	
	// 카테고리별로 항목을 조회합니다.
	private void viewItemsByCategory() {
		System.out.println("=== 카테고리별 보기 ===");
		
		// 💡 카테고리 목록 표시 로직이 실제 카테고리 목록에 의존하도록 수정해야 합니다.
		// 임시로 LedgerItem.VALID_CATEGORIES를 사용한다고 가정합니다.
		String category = getValidCategory("카테고리 입력 (" + String.join(", ", LedgerItem.VALID_CATEGORIES) + "): ");
		if (category == null) return;
		
		List<LedgerItem> items = ledgerService.getItemsByCategory(category);
		System.out.printf("'%s' 카테고리의 항목:%n", category);
		ledgerService.displayItems(items);
		System.out.println();
	}
	
	// 데이터를 파일에 저장합니다.
	private void saveToFile() {
		System.out.println("=== 파일에 저장 ===");
		boolean success = ledgerService.saveData();
		if (!success) {
			System.out.println("파일에 데이터를 저장하지 못했습니다.");
		}
		System.out.println();
	}
	
	// 파일에서 데이터를 불러옵니다.
	private void loadFromFile() {
		System.out.println("=== 파일 불러오기 ===");
		System.out.print("현재 데이터가 덮어씌워집니다. 계속하시겠습니까? (Y/N): ");
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
	
	// 파일 형식을 변경합니다.
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
	
	// 💡 입력 취소 확인 프롬프트 
	// @return 취소 확정 시 true, 아니면 false
	private boolean confirmCancel() {
		System.out.print("# 확인: 현재 작업을 취소하고 메인 화면으로 이동하시겠습니까? (Y/N) > ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		
		if ("y".equals(confirm)) {
			System.out.println("작업이 취소되었습니다. 메인 메뉴로 돌아갑니다.");
			return true;
		} else {
			System.out.println("작업을 재개합니다.");
			return false;
		}
	}

	// 입력 유효성 검사를 위한 헬퍼 메서드
	
	private LocalDate getValidDate(String prompt) {
		while (true) {
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();
			
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return null; // 취소 확정 시 null 반환
				continue;
			}
			
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
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();
			
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return null; // 취소 확정 시 null 반환
				continue;
			}
			
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
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();
			
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return null; // 취소 확정 시 null 반환
				continue;
			}
			
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
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();
			
			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel()) return null; // 취소 확정 시 null 반환
				continue;
			}
			
			ValidationUtil.ValidationResult result = ValidationUtil.validateDescription(input);
			if (result.isValid()) {
				return result.getValue(String.class);
			} else {
				System.out.println("오류: " + result.getErrorMessage());
			}
		}
	}
}
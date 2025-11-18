package com.accountbook.ui;

import com.accountbook.model.LedgerItem;
import com.accountbook.service.LedgerService;
import com.accountbook.util.FileFormat;
import com.accountbook.util.CategoryManager;
import com.accountbook.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

// 개인 가계부 애플리케이션을 위한 명령줄 인터페이스입니다.
public class CliInterface {

	// 기본 제공 카테고리 6개 (삭제/수정 불가)
	private static final List<String> BASE_CATEGORIES = Arrays.asList(
			"Food",
			"Transport",
			"Living",
			"Shopping",
			"Transfer",
			"Hobby");

	// 사용자 지정 카테고리 (7~10번에 해당, 최대 4개)
	private final List<String> customCategories = new ArrayList<>();

	private final Scanner scanner;
	private final LedgerService ledgerService;
	private boolean running;

	public CliInterface() {
		// 생성 시 인코딩을 UTF-8로 명시
		this.scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
		this.ledgerService = new LedgerService();
		this.running = true;
	}

	public CliInterface(String fileName) {
		this.scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
		this.ledgerService = new LedgerService(fileName);
		this.running = true;
	}

	// CLI 애플리케이션을 시작합니다.
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

	// 메인 메뉴를 표시합니다.
	private void showMainMenu() {
		System.out.println("==== 개인 가계부 ====");
		System.out.printf("현재 파일 형식: %s%n", ledgerService.getCurrentFormat().getDescription());
		System.out.println("1. 내역 관리");
		System.out.println(" 1.1 내역 추가");
		System.out.println(" 1.2 내역 삭제");
		System.out.println(" 1.3 내역 수정");
		System.out.println("2. 내역 조회");
		System.out.println(" 2.1 전체 보기");
		System.out.println(" 2.2 날짜 범위별 보기");
		System.out.println(" 2.3 카테고리별 보기");
		System.out.println("3. 파일 불러오기");
		System.out.println("4. 파일 형식 변경");
		System.out.println("5. 프로그램 종료");
		System.out.println();
		System.out.print("옵션 선택: ");
	}

	// 메인 메뉴 선택을 처리합니다.
	private void handleMainMenuChoice() {
		String input = scanner.nextLine();
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
			case 3:
				loadFromFile();
				break;
			case 4:
				changeFileFormat();
				break;
			case 5:
				running = false;
				break;
		}
	}

	// 내역 관리 서브메뉴
	private void handleManageItemsMenu() {
		System.out.println("=== 내역 관리 ===");
		System.out.println("1. 내역 추가");
		System.out.println("2. 내역 삭제");
		System.out.println("3. 내역 수정");
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
				addItem();
				break;
			case 2:
				deleteItem();
				break;
			case 3:
				editItem();
				break;
		}
	}

	// 내역 조회 서브메뉴
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

	// 가계부에 새 항목을 추가합니다.
	private void addItem() {
		System.out.println("=== 새 내역 추가 ===");

		String type;
		while (true) {
			System.out.print("유형 입력 [1: 수입 (+), 2: 지출 (-)]: ");
			String input = scanner.nextLine();

			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel())
					return;
				continue;
			}

			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 2);

			if (!result.isValid()) {
				System.out.println("오류: " + result.getErrorMessage());
				continue;
			}

			int choice = result.getValue(Integer.class);
			type = (choice == 1) ? "수입" : "지출";
			break;
		}

		LocalDate date = getValidDate("날짜 입력 (YYYY-MM-DD): ");
		if (date == null)
			return;

		Integer amount = getValidAmount("금액 입력: ");
		if (amount == null)
			return;

		String typeSymbol;
		if (type.equals("지출")) {
			amount = -Math.abs(amount);
			typeSymbol = " 지출 (-)";
		} else {
			amount = Math.abs(amount);
			typeSymbol = "수입 (+)";
		}

		// 카테고리 가져오기
		String category = promptCategoryWithManagement(null);
		if (category == null) return;

		String description = getValidDescription("설명 입력 (선택 사항, 최대 50자 이내): ");
		if (description == null)
			return;

		boolean success = ledgerService.addItem(typeSymbol, date, amount, category, description);
		if (!success) {
			System.out.println("오류: 파일에 항목을 저장하지 못했습니다.");
		} else {
			System.out.println("항목이 성공적으로 추가되었습니다.");
		}
		System.out.println();
	}

	// 내역 삭제
	private void deleteItem() {
		System.out.println("=== 내역 삭제 ===");

		if (ledgerService.getItemCount() == 0) {
			System.out.println("삭제할 항목이 없습니다.");
			System.out.println();
			return;
		}

		System.out.println("현재 항목:");
		ledgerService.displayItems(ledgerService.getAllItems());
		System.out.println();

		System.out.print("삭제할 항목의 ID 입력: ");
		String input = scanner.nextLine();

		if ("cancel".equalsIgnoreCase(input.trim())) {
			if (confirmCancel())
				return;
		}

		try {
			int id = Integer.parseInt(input.trim());
			boolean deleted = ledgerService.deleteItem(id);
			if (deleted) {
				System.out.printf("ID %d 항목이 성공적으로 삭제되었습니다.%n", id);
			} else {
				System.out.println("오류: 해당 ID의 항목이 존재하지 않습니다.");
			}
		} catch (NumberFormatException e) {
			System.out.println("오류: 유효한 ID 번호를 입력해주세요.");
		}

		System.out.println();
	}

	// 내역 수정
	private void editItem() {
		System.out.println("=== 내역 수정 ===");

		if (ledgerService.getItemCount() == 0) {
			System.out.println("수정할 항목이 없습니다.");
			System.out.println();
			return;
		}

		System.out.println("현재 항목:");
		ledgerService.displayItems(ledgerService.getAllItems());
		System.out.println();

		System.out.print("수정할 항목의 ID 입력: ");
		String idInput = scanner.nextLine();

		if ("cancel".equalsIgnoreCase(idInput.trim())) {
			if (confirmCancel())
				return;
		}

		try {
			int id = Integer.parseInt(idInput.trim());

			Optional<LedgerItem> itemOpt = ledgerService.getAllItems().stream()
					.filter(item -> item.getId() == id)
					.findFirst();

			if (itemOpt.isEmpty()) {
				System.out.println("오류: 해당 ID의 항목이 존재하지 않습니다.");
				System.out.println();
				return;
			}

			LedgerItem itemToEdit = itemOpt.get();

			System.out.println("--- 항목 수정 모드 ---");
			String currentType = itemToEdit.getAmount() >= 0 ? "수입" : "지출";
			System.out.printf("ID: %d%n", itemToEdit.getId());
			System.out.printf("1. 유형: %s%n", currentType);
			System.out.printf("2. 날짜: %s%n", itemToEdit.getDate());
			System.out.printf("3. 금액: %d%n", Math.abs(itemToEdit.getAmount()));
			System.out.printf("4. 카테고리: %s%n", itemToEdit.getCategory());
			System.out.printf("5. 내용: %s%n", itemToEdit.getDescription());
			System.out.println("---------------------");

			System.out.println("수정할 항목을 선택하세요:");
			System.out.println("1. 유형 (수입/지출) 2. 날짜 3. 금액 4. 카테고리 5. 내용");
			System.out.print("선택 > (취소: 'cancel' 입력): ");

			String fieldChoiceInput = scanner.nextLine().trim();

			if ("cancel".equalsIgnoreCase(fieldChoiceInput)) {
				if (confirmCancel())
					return;
			}

			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(fieldChoiceInput, 1, 5);
			if (!result.isValid()) {
				System.out.println("오류: " + result.getErrorMessage());
				System.out.println();
				return;
			}

			int fieldChoice = result.getValue(Integer.class);
			Object newValue = null;

			switch (fieldChoice) {
				case 1: {
					String newType = getValidType("유형 입력 [1: 수입 (+), 2: 지출 (-)]: ");
					if (newType == null)
						return;
					newValue = newType.equals("지출")
							? -Math.abs(itemToEdit.getAmount())
							: Math.abs(itemToEdit.getAmount());
					break;
				}
				case 2: {
					newValue = getValidDate("날짜 입력 (YYYY-MM-DD): ");
					break;
				}
				case 3: {
					Integer newAmount = getValidAmount("금액 입력: ");
					if (newAmount == null)
						return;
					newValue = currentType.equals("지출") ? -Math.abs(newAmount) : Math.abs(newAmount);
					break;
				}
				case 4: {
					System.out.println("현재 카테고리: " + itemToEdit.getCategory());
					newValue = promptCategorySelection();
					break;
				}
				case 5: {
					newValue = getValidDescription("내용 입력 (선택사항, 최대 50자): ");
					break;
				}
			}

			if (newValue == null && fieldChoice != 1) {
				System.out.println("수정 작업이 취소되었습니다.");
				return;
			}

			updateItemToEdit(itemToEdit, fieldChoice, newValue);

			boolean success = ledgerService.saveData();

			if (success) {
				System.out.println("항목이 성공적으로 수정되었습니다.");
			} else {
				System.out.println("수정 작업이 취소되었거나 파일 저장에 실패했습니다.");
			}

		} catch (NumberFormatException e) {
			System.out.println("오류: 유효한 ID 번호를 입력해주세요.");
		}
		System.out.println();
	}

	// LedgerItem 객체에 변경 사항을 직접 반영
	private void updateItemToEdit(LedgerItem itemToEdit, int fieldChoice, Object newValue) {
		if (newValue == null)
			return;

		try {
			if (fieldChoice == 1 || fieldChoice == 3) {
				itemToEdit.setAmount((Integer) newValue);
				String newType = ((Integer) newValue >= 0) ? "수입 (+)" : " 지출 (-)";
				itemToEdit.setType(newType);
			} else {
				switch (fieldChoice) {
					case 2:
						itemToEdit.setDate((LocalDate) newValue);
						break;
					case 4:
						itemToEdit.setCategory((String) newValue);
						break;
					case 5:
						itemToEdit.setDescription((String) newValue);
						break;
				}
			}
		} catch (Exception e) {
			System.out.println("오류: 데이터 모델 업데이트 중 예외 발생: " + e.getMessage());
		}
	}

	// 유형 입력
	private String getValidType(String prompt) {
		while (true) {
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();

			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel())
					return null;
				continue;
			}

			ValidationUtil.ValidationResult result = ValidationUtil.validateMenuOption(input, 1, 2);

			if (result.isValid()) {
				int choice = result.getValue(Integer.class);
				return choice == 1 ? "수입" : "지출";
			} else {
				System.out.println("오류: " + result.getErrorMessage());
			}
		}
	}

	// 전체 보기
	private void viewAllItems() {
		System.out.println("=== 전체 내역 ===");
		List<LedgerItem> items = ledgerService.getAllItems();
		ledgerService.displayItems(items);
		System.out.println();
	}

	// 날짜 범위별 보기
	private void viewItemsByDateRange() {
		System.out.println("=== 날짜 범위별 보기 ===");

		LocalDate startDate = getValidDate("시작 날짜 입력 (YYYY-MM-DD): ");
		if (startDate == null)
			return;

		LocalDate endDate = getValidDate("종료 날짜 입력 (YYYY-MM-DD): ");
		if (endDate == null)
			return;

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

	// 카테고리별 보기
	private void viewItemsByCategory() {
		System.out.println("=== 카테고리별 보기 ===");

		String category = promptCategorySelection();
		if (category == null) return;
		

		List<LedgerItem> items = ledgerService.getItemsByCategory(category);
		System.out.printf("'%s' 카테고리의 항목:%n", category);
		ledgerService.displayItems(items);
		System.out.println();
	}

	// 파일 저장 (현재는 안 쓰일 수도 있음)
	private void saveToFile() {
		System.out.println("=== 파일에 저장 ===");
		boolean success = ledgerService.saveData();
		if (!success) {
			System.out.println("파일에 데이터를 저장하지 못했습니다.");
		}
		System.out.println();
	}

	// 파일 불러오기
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

	// 파일 형식 변경
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

	// 취소 확인
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

	// ====== 여기부터 입력 유효성 헬퍼들 (한 번만 존재) ======

	private LocalDate getValidDate(String prompt) {
		while (true) {
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();

			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel())
					return null;
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
				if (confirmCancel())
					return null;
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

	// 지금은 안 쓰일 수 있지만 남겨둠
	private String getValidCategory(String prompt) {
		while (true) {
			System.out.print(prompt + "(취소: 'cancel' 입력): ");
			String input = scanner.nextLine();

			if ("cancel".equalsIgnoreCase(input.trim())) {
				if (confirmCancel())
					return null;
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
				if (confirmCancel())
					return null;
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


	// 카테고리 전체 목록을 번호와 함께 출력합니다.
	private void printCategoryList() {
		List<String> categories = CategoryManager.getAllCategories();
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < categories.size(); i++) {
			if (i > 0) sb.append(' ');
			sb.append(i + 1).append(". ").append(categories.get(i));
		}
		sb.append("]");
		System.out.println("현재 카테고리: " + sb.toString());
	}

	// 2.1 내역 추가 - 카테고리 입력: 번호 선택 또는 추가(Y)/삭제(N) 관리 포함
	private String promptCategoryWithManagement(String currentLabel) {
		while (true) {
			if (currentLabel != null && !currentLabel.isEmpty()) {
				System.out.println("현재 값: " + currentLabel);
			}
			printCategoryList();
			System.out.print("카테고리 입력: [번호 선택 / 추가 Y / 삭제 N] (취소: 'cancel' 입력): ");
			String input = scanner.nextLine().trim();

			if ("cancel".equalsIgnoreCase(input)) {
				if (confirmCancel()) return null;
				continue;
			}

			// 번호 선택
			ValidationUtil.ValidationResult num = ValidationUtil.validateMenuOption(input, 1, CategoryManager.getAllCategories().size());
			if (num.isValid()) {
				int idx = num.getValue(Integer.class) - 1;
				return CategoryManager.getAllCategories().get(idx);
			}

			// 추가
			if (input.equalsIgnoreCase("Y")) {
				System.out.print("카테고리 명을 입력해주세요: ");
				String name = scanner.nextLine();
				if ("cancel".equalsIgnoreCase(name.trim())) {
					if (confirmCancel()) return null;
					continue;
				}
				CategoryManager.AddResult res = CategoryManager.addCustomCategory(name);
				if (!res.success) {
					System.out.println(res.message);
				} else {
					System.out.println("정상적으로 카테고리가 추가되었습니다.");
				}
				// 업데이트된 목록을 보여주고 다시 프롬프트로 복귀
				continue;
			}

			// 삭제
			if (input.equalsIgnoreCase("N")) {
				System.out.print("카테고리 명을 입력해주세요: ");
				String name = scanner.nextLine();
				if ("cancel".equalsIgnoreCase(name.trim())) {
					if (confirmCancel()) return null;
					continue;
				}
				CategoryManager.DeleteResult res = CategoryManager.deleteCustomCategory(name);
				if (!res.success) {
					System.out.println(res.message);
				} else {
					System.out.println("해당 카테고리 항목을 삭제하였습니다.");
				}
				// 자동 리넘버링은 리스트에서 자연스럽게 반영됨. 다시 프롬프트로 복귀
				continue;
			}

			System.out.println("오류: 유효한 번호, 'Y', 'N' 중 하나를 입력해주세요.");
		}
	}

	// 번호만으로 카테고리 선택 (수정, 카테고리별 보기에서 사용)
	private String promptCategorySelection() {
		while (true) {
			printCategoryList();
			System.out.print("카테고리 번호를 입력해주세요 (취소: 'cancel' 입력): ");
			String input = scanner.nextLine().trim();
			if ("cancel".equalsIgnoreCase(input)) {
				if (confirmCancel()) return null;
				continue;
			}
			ValidationUtil.ValidationResult num = ValidationUtil.validateMenuOption(input, 1, CategoryManager.getAllCategories().size());
			if (num.isValid()) {
				int idx = num.getValue(Integer.class) - 1;
				return CategoryManager.getAllCategories().get(idx);
			}
			System.out.println("오류: 유효한 번호를 입력해주세요.");
		}
	}
}

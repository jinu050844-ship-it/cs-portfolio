// W12_StudentInfo + W12_MyHashTable 테스트용 main 함수

public class W12_Main {
    public static void main(String[] args) {

        // 과제 2 조건
        // 초과 크기 13, load factor 기준 0.9
        W12_MyHashTable table = new W12_MyHashTable(13, 0.9f);

        // 학생 15명 추가 (과제 요구사항)
        table.put("CS2025123", "김민수");
        table.put("CS2025124", "김장우");
        table.put("ME2024567", "박진우");
        table.put("EE2023123", "권민재");
        table.put("CS2025001", "김영현");
        table.put("CS2025002", "김태리");
        table.put("ME2024001", "박서연");
        table.put("ME2024002", "이수근");
        table.put("EE2023001", "이승민");
        table.put("BE2023002", "최재원");

        // rehash 유도용 학생 5명 추가
        table.put("CS2025333", "권소윤");
        table.put("CS2025444", "오정민");
        table.put("ME2024555", "김강민");
        table.put("EE2023666", "박재훈");
        table.put("EE2023777", "권지철");

        //현재 상태 출력
        System.out.println("===== Hash Table Info =====");
        System.out.println("현재 size: " + table.size());
        System.out.println("현재 load factor: " + table.getLoadFactor());

        System.out.println("\n===== Bucket Array 내용 =====");
        table.printBuckets();

        // get/remove 테스트
        System.out.println("\n===== get / remove 테스트 =====");
        System.out.println("get(\"CS2025123\"): " + table.get("CS2025123"));
        System.out.println("remove(\"CS2025123\"): " + table.remove("CS2025123"));
        System.out.println("get(\"CS2025123\") after remove: " + table.get("CS2025123"));
    }
}
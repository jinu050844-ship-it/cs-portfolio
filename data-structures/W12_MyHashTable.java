// separate chaining + polynomial hash + load factor + rehash
import java.util.ArrayList;

public class W12_MyHashTable {

    // 필드
    private ArrayList<ArrayList<W12_StudentInfo>> bucketArray; // 버킷 배열(체인)
    private int bucketCapacity; // 버킷 크기
    private int size; // 저장된 엔트리 개수
    private float loadFactor; // 기준 load factor

    // 생성자
    // 과제 1. 기본 생성자 (loadFactor = 0.75)
    public W12_MyHashTable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    // 과제 2. 생성자 (loadFactor 받기)
    public W12_MyHashTable(int initialCapacity, float loadFactor) {
        this.bucketCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.size = 0;

        bucketArray = new ArrayList<>(bucketCapacity);
        for (int i = 0; i < bucketCapacity; i++) {
            bucketArray.add(new ArrayList<W12_StudentInfo>());
        }
    }

    // Hash Function
    private int hashFunc(String k) {
        int a = 33;
        long hash = 0;

        for (int i = 0; i < k.length(); i++) {
            hash = a* hash + k.charAt(i);
        }
        if (hash < 0) hash = -hash;

        return (int) (hash % bucketCapacity);
    }

    // rehash
    private void rehash(int newCapacity) {
        ArrayList<ArrayList<W12_StudentInfo>> oldBucketArray = bucketArray;

        bucketCapacity = newCapacity;
        bucketArray = new ArrayList<>(bucketCapacity);
        for (int i = 0; i < bucketCapacity; i++) {
            bucketArray.add(new ArrayList<W12_StudentInfo>());
        }

        int oldSize = size;
        size = 0;

        for (ArrayList<W12_StudentInfo> chain : oldBucketArray) {
            for (W12_StudentInfo info : chain) {
                put(info.getStudentID(), info.getStudentName());
            }
        }

        size = oldSize;
    }

    // 로드팩터 계산
    public float getLoadFactor() {
        if (bucketCapacity == 0) return 0.0f;
        return (float) size / (float) bucketCapacity;
    }

    // 기본 연산들

    public int size() {
        return size;
    }

    public String get(String k) {
        int idx = hashFunc(k);
        ArrayList<W12_StudentInfo> chain = bucketArray.get(idx);

        for (W12_StudentInfo info : chain) {
            if (info.getStudentID().equals(k)) {
                return info.getStudentName();
            }
        }
        return null;
    }

    public String put(String k, String v) {
        int idx = hashFunc(k);
        ArrayList<W12_StudentInfo> chain = bucketArray.get(idx);

        // 이미 존재하면 value 수정
        for (W12_StudentInfo info : chain) {
            if (info.getStudentID().equals(k)) {
                String oldValue = info.getStudentName();
                info.setStudentName(v);
                return oldValue;
            }
        }

        // 새로 삽입
        W12_StudentInfo newInfo = new W12_StudentInfo(k, v);
        chain.add(newInfo);
        size++;

        // load factor 체크
        float currentLoad = (float) size / (float) bucketCapacity;
        if (currentLoad >= loadFactor) {
            rehash(bucketCapacity * 2);
        }

        return null;
    }

    public String remove(String k) {
        int idx = hashFunc(k);
        ArrayList<W12_StudentInfo> chain = bucketArray.get(idx);

        for (int i = 0; i < chain.size(); i++) {
            W12_StudentInfo info = chain.get(i);
            if (info.getStudentID().equals(k)) {
                String value = info.getStudentName();
                chain.remove(i);
                size--;
                return value;
            }
        }
        return null;
    }

    // 디버깅용
    public void printBuckets() {
        for (int i = 0; i < bucketCapacity; i++) {
            System.out.print("[" + i + "]");
            ArrayList<W12_StudentInfo> chain = bucketArray.get(i);
            for (W12_StudentInfo info : chain) {
                System.out.print(info.toString() + " ");
            }
            System.out.println();
        }
    }
} 
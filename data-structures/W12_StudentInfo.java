// 한 명의 학생 정보를 저장하는 클래스
// key: studentID (학번, 예: CS2025123)
// value: studentName (이름)

public class W12_StudentInfo {

    // 필드
    private String studentID;
    private String studentName;

    // 생성자
    // 기본 생성자
    public W12_StudentInfo() {}

    // 학번과 이름을 한 번에 넣는 생성자
    public W12_StudentInfo(String id, String name) {
        this.studentID = id;
        this.studentName = name;
    }

    // setter
    public void setStudentID(String id) {
        this.studentID = id;
    }

    public void setStudentName(String name) {
        this.studentName = name;
    }

    // getter
    public String getStudentID() {
        return this.studentID;
    }


    public String getStudentName() {
        return this.studentName;
    }

    // 디버깅용
    @Override
    public String toString() {
        return "(" + studentID + ", " + studentName + ")";
    }
}
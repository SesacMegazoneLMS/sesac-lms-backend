package com.sesac.backend.lectures.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.repository.LectureRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 서비스 클래스 어노테이션
@Service
// 모든 필드를 포함하는 생성자 생성
@AllArgsConstructor
// 트랜잭션 관리
@Transactional
public class LectureService {

    // 강의 및 코스 리포지토리 의존성 주입
    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;


    // 비디오 상태 업데이트 메서드
    @Transactional
    public void updateVideoStatus(Long Id,String videoKey, String hlsUrl, String status) {
        // 비디오 키로 강의 조회
        Lecture lecture = lectureRepository.findById(Id)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + videoKey));
        
        // HLS URL 및 상태 업데이트
        lecture.setHlsUrl(hlsUrl);
        lecture.setStatus(status);
        lecture.setVideoKey(videoKey);
        // 강의 저장
        lectureRepository.save(lecture);
    }

    // 강의 생성 메서드
    public LectureResponse createLecture(LectureRequest request) {
        // 코스 ID로 코스 조회
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));

        // 강의 객체 생성 및 저장
        Lecture lecture = Lecture.builder()
            .course(course)
            .title(request.getTitle())
            .videoKey(request.getVideoKey())
            .orderIndex(request.getOrderIndex())
            .isFree(request.getIsFree())
            .duration(request.getDuration())
            .status("PROCESSING")  // 초기 상태
            .build();

        lecture = lectureRepository.save(lecture);
        // 강의 ID를 포함한 응답 반환
        return new LectureResponse(lecture.getId());
    }

    // 단일 강의 조회
    @Transactional(readOnly = true)
    public Lecture getLecture(Long id) {
        return lectureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));
    }

    // 전체 강의 목록 조회
    @Transactional(readOnly = true)
    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }

    // 특정 코스의 강의 목록 조회
    @Transactional(readOnly = true)
    public List<Lecture> getLecturesByCourseId(Long courseId) {
        return lectureRepository.findByCourseId(courseId);
    }
}
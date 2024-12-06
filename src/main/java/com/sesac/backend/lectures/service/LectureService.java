package com.sesac.backend.lectures.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.repository.LectureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;


    @Transactional
    public void updateVideoStatus(String videoKey, String hlsUrl, String status) {
        Lecture lecture = lectureRepository.findByVideoKey(videoKey)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + videoKey));
        
        lecture.setHlsUrl(hlsUrl);
        lecture.setStatus(status);
        lectureRepository.save(lecture);
    }

    public LectureResponse createLecture(LectureRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));

        Lecture lecture = Lecture.builder()
            .course(course)
            .title(request.getTitle())
            .videoKey(request.getVideoKey())
            .orderIndex(request.getOrderIndex())
            .status("PROCESSING")  // 초기 상태
            .build();

        lecture = lectureRepository.save(lecture);
        return new LectureResponse(lecture.getId());
    }
}

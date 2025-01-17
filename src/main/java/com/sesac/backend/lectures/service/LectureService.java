package com.sesac.backend.lectures.service;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.courses.repository.CourseRepository;
import com.sesac.backend.enrollments.repository.EnrollmentRepository;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.dto.request.LectureRequest;
import com.sesac.backend.lectures.dto.response.LectureNavigation;
import com.sesac.backend.lectures.dto.response.LectureResponse;
import com.sesac.backend.lectures.repository.LectureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 서비스 클래스 어노테이션
@Service
// 모든 필드를 포함하는 생성자 생성
@AllArgsConstructor
// 트랜잭션 관리
@Transactional
@Slf4j
public class LectureService {

    // 강의 및 코스 리포지토리 의존성 주입
    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;
    private final S3Client s3Client;
    private final EnrollmentRepository enrollmentRepository;

    private static final String BUCKET_NAME = "hip-media-input"; // S3 버킷 이름

    // 비디오 상태 업데이트 메서드
    @Transactional
    public void updateVideoStatus(Long lectureId, String videoKey, String status, String duration) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
        
            log.info("Updating lecture status - ID: {}, videoKey: {}, status: {}, duration: {}", 
            lectureId, videoKey, status, duration);
        
        lecture.setVideoKey(videoKey);
        lecture.setStatus(status);
        lecture.setDuration(duration);  // duration 설정
        
        lectureRepository.save(lecture);
    }

    // 강의 생성 메서드
    public LectureResponse createLecture(LectureRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));

        Lecture lecture = Lecture.builder()
            .course(course)
            .title(request.getTitle())
            .videoKey(request.getVideoKey())
            .orderIndex(request.getOrderIndex())
            .duration(request.getDuration())
            .status("PROCESSING")
            .cloudFrontUrl("https://cdn.sesac-univ.click")
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
    
    // 강의 네비게이션 조회
    @Transactional(readOnly = true)
    public List<LectureNavigation> getLectureNavigation(Long courseId) {
        List<Lecture> lectures = lectureRepository.findByCourseId(courseId);
        return lectures.stream()
            .map(LectureNavigation::from)
            .collect(Collectors.toList());
    }


    //강의 수정 메서드 
    public void updateLecture(Long id, Map<String, Object> updates) {
        Lecture lecture = lectureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + id));
    
        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    lecture.setTitle((String) value);
                    break;
                case "videoKey":
                    lecture.setVideoKey((String) value);
                    break;
                case "orderIndex":
                    lecture.setOrderIndex((Integer) value);
                    break;
                case "duration":
                    lecture.setDuration((String) value);
                    break;
                case "status":
                    lecture.setStatus((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("수정할 수 없는 필드: " + key);
            }
        });
    
        lectureRepository.save(lecture);
    }



    // 강의 삭제 메서드
    @Transactional
    public void deleteLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다: " + lectureId));
        
        // S3에서 비디오 파일 삭제
        if (lecture.getVideoKey() != null) {
            try {
                // 원본 비디오 파일 삭제
                deleteS3Object(BUCKET_NAME, lecture.getVideoKey());
                
                // 변환된 HLS 파일들 삭제
                String hlsPrefix = lecture.getVideoKey().split("\\.")[0];
                deleteHlsFiles(hlsPrefix);
                
                log.info("S3 파일 삭제 완료: {}", lecture.getVideoKey());
            } catch (Exception e) {
                log.error("S3 파일 삭제 실패: {}", e.getMessage(), e);
                throw new RuntimeException("비디오 파일 삭제 실패", e);
            }
        }
        
        // 강의 삭제
        lectureRepository.delete(lecture);
    }

    private void deleteS3Object(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        s3Client.deleteObject(deleteObjectRequest);
    }

    private void deleteHlsFiles(String prefix) {
        String outputBucketName = "hip-media-output";
        try {
            // .m3u8 파일 삭제
            deleteS3Object(outputBucketName, prefix + "/" + prefix + ".m3u8");
            
            // .ts 파일들 삭제
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(outputBucketName)
                .prefix(prefix + "/")
                .build();
                
            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            for (S3Object object : listResponse.contents()) {
                if (object.key().endsWith(".ts")) {
                    deleteS3Object(outputBucketName, object.key());
                    log.info("TS 파일 삭제 완료: {}", object.key());
                }
            }
            
            log.info("HLS 파일들 삭제 완료: {}", prefix);
        } catch (Exception e) {
            log.error("HLS 파일 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("HLS 파일 삭제 실패", e);
        }
    }

    
}
package Video.Player.Service;

import Video.Player.Model.Video;
import Video.Player.Repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private String uploadDir = "C:\\Users\\Raihan.fadhlullah\\Pictures\\AMS\\";
    public void uploadVideo(MultipartFile file, String title) throws IOException {
        Path filePath = Paths.get(uploadDir + title + file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
        Video video = new Video(title, title + file.getOriginalFilename());
        videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoByFileName(String fileName) {
        return videoRepository.findByFileName(fileName);
    }

    public Path getVideoPath(String fileName) {
        return Paths.get(uploadDir + fileName);
    }
}
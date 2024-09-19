package Video.Player.Controller;

import Video.Player.Model.Video;
import Video.Player.Service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/")
    public String index(Model model) {
        List<Video> videos = videoService.getAllVideos();
        model.addAttribute("videos", videos);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("title") String title,
                              @RequestParam("file") MultipartFile file) {
        try {
            videoService.uploadVideo(file, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/video/{fileName}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String fileName) {
        Video video = videoService.getVideoByFileName(fileName);
        Path videoPath = videoService.getVideoPath(video.getFileName());
        Resource resource;
        try {
            resource = new UrlResource(videoPath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + video.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/video/fullscreen/{fileName}")
    public String fullscreenVideo(@PathVariable String fileName, Model model) {
        Video video = videoService.getVideoByFileName(fileName);
        model.addAttribute("video", video);
        return "fullscreen";
    }

    @GetMapping("/video/play/fullscreen")
    public String fullscreenVideoplay(Model model) {
        List<Video> videos = videoService.getAllVideos();
        model.addAttribute("video", videos);
        return "fullscreen";
    }


//    private final String videoDir = "C:/Users/fadhl/OneDrive/Pictures/";
//
//    @GetMapping("/video/fullscreen/{fileName}")
//    public String fullscreenVideo(@PathVariable String fileName, Model model) {
//        model.addAttribute("fileName", fileName);
//        return "fullscreen";
//    }
//
//    @GetMapping("/video/{fileName}")
//    public ResponseEntity<Resource> streamVideo(@PathVariable String fileName) {
//        try {
//            Path videoPath = Paths.get(videoDir + fileName);
//            Resource resource = new UrlResource(videoPath.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.parseMediaType("video/mp4"))
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + fileName + "\"")
//                        .body(resource);
//            } else {
//                throw new RuntimeException("Could not read the file!");
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }


}
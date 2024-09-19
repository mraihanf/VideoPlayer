package Video.Player.Controller;

import Video.Player.Model.Video;
import Video.Player.Service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/admin")
    public String index(Model model) {
        List<Video> videos = videoService.getAllVideos();
        model.addAttribute("videos", videos);
        return "index";
    }

    @DeleteMapping("/delete")
    public  ResponseEntity<Void>  delete(Integer id ){
        try {
            videoService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("title") String title,
                              @RequestParam("file") MultipartFile file) {
        try {
            videoService.uploadVideo(file, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin";
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
        List<String> videoFileNames = videos.stream()
                .map(Video::getFileName)
                .collect(Collectors.toList());
        model.addAttribute("video", videoFileNames);
        return "fullscreen";
    }

    @GetMapping("/filename")
    public ResponseEntity<Map> filename(){
        Map data = new HashMap<>();
        List<Video> videos = videoService.getAllVideos();
        List<String> videoFileNames = videos.stream()
                .map(Video::getFileName)
                .collect(Collectors.toList());
        data.put("data", videoFileNames);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

//    private final String videoDir = "C:/Users/fadhl/OneDrive/Pictures/";
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
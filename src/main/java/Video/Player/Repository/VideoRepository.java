package Video.Player.Repository;

import Video.Player.Model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    Video findByFileName(String fileName);

}

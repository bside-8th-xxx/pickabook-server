package beside.sunday8turtle.pickabookserver.modules.bookmark.dto;

import beside.sunday8turtle.pickabookserver.modules.bookmark.domain.Bookmark;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPutResponseDTO {
        private String title;
        private String url;
        private String description;
        private String tag;
        private Date notidate;

        public static BookmarkPutResponseDTO fromBookmark(Bookmark bookmark) {
                return new BookmarkPutResponseDTO(
                        bookmark.getTitle(),
                        bookmark.getUrl(),
                        bookmark.getDescription(),
                        bookmark.getTag(),
                        bookmark.getNotidate()
                );
        }
}
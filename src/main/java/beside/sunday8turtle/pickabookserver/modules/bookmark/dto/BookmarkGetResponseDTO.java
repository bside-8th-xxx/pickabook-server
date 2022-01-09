package beside.sunday8turtle.pickabookserver.modules.bookmark.dto;

import beside.sunday8turtle.pickabookserver.modules.bookmark.domain.Bookmark;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkGetResponseDTO {
    private String title;
    private String url;
    private String description;
    private Date notidate;

    public static BookmarkGetResponseDTO fromBookmark(Bookmark bookmark) {
        return new BookmarkGetResponseDTO(
                bookmark.getTitle(),
                bookmark.getUrl(),
                bookmark.getDescription(),
                bookmark.getNotidate()
        );
    }
    public static List<BookmarkGetResponseDTO> fromBookmarks(Page<Bookmark> bookmarks) {
        return bookmarks.map(BookmarkGetResponseDTO::fromBookmark)
                .stream().collect(toList());
    }
}

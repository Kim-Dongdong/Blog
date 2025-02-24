package blogstudy.blog.dto;

import blogstudy.blog.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드값을 받는 생성자 생성
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntry() { // Article 객체 리턴
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}

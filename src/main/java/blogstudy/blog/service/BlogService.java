package blogstudy.blog.service;

import blogstudy.blog.domain.Article;
import blogstudy.blog.dto.AddArticleRequest;
import blogstudy.blog.dto.UpdateArticleRequest;
import blogstudy.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntry());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id) // 원래는 Optical을 통해 예외처리를 해줘야 하지만, 여기서 orElseThrow를 통해 예외처리를 해주었다.
                .orElseThrow(() -> new IllegalArgumentException("not found + " + id));
    }

    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}

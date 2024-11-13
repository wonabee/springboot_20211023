package com.example.demo.model.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.domain.Article;
import com.example.demo.model.domain.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // List<Article> findAll();  // 필요에 따라 사용
}


// @Repository
// public interface BlogRepository extends JpaRepository<Article, Long>{
    // List<Article> findAll();
// }

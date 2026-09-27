package com.librarymanagement.repository;
import com.librarymanagement.entity.Book; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
public interface BookRepository extends JpaRepository<Book,Long>{
 @Query("select b from Book b where (:search is null or lower(b.title) like lower(concat('%',:search,'%')) or lower(b.author) like lower(concat('%',:search,'%')) or lower(b.isbn) like lower(concat('%',:search,'%'))) and (:genre is null or lower(b.genre)=lower(:genre)) and (:available=false or b.availableCopies>0)")
 Page<Book> search(@Param("search") String search,@Param("genre") String genre,@Param("available") boolean available,Pageable pageable);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select b from Book b where b.id=:id") java.util.Optional<Book> lockById(@Param("id") Long id);
}

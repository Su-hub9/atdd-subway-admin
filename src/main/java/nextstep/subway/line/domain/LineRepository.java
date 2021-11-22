package nextstep.subway.line.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("SELECT l FROM Line l"
        + " JOIN FETCH l.sections.values s"
        + " JOIN FETCH s.upStation"
        + " JOIN FETCH s.downStation")
    List<Line> findAllWithSections();
}

package nextstep.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    public static final Distance addDistance = Distance.from(4);
    private Distance initDistance;
    private Section initSection;
    private Sections sections;

    @BeforeEach
    void setUp() {
        this.initDistance = Distance.from(10);
        this.initSection = new Section(SectionTest.강남역, SectionTest.역삼역, this.initDistance);
        this.sections = Sections.from(new ArrayList<>(Arrays.asList(this.initSection)));
    }

    @DisplayName("비어있는 지하철 구간 목록 생성")
    @Test
    void test_create_empty() {
        // given & when
        Sections emptySections = Sections.empty();
        // then
        assertThatNotNullAndSize(emptySections, 0);
    }

    @DisplayName("지하철 구간 목록 생성")
    @Test
    void test_create() {
        // given & when
        Sections newSections = Sections.from(Arrays.asList(this.initSection));
        // then
        assertThatNotNullAndSize(newSections, 1);
    }

    @DisplayName("목록에 상행역과 연결된 지하철 구간 추가 : 강남역-(선릉역)-역삼역")
    @Test
    void test_add_up_section() {
        // given
        Section connectUpSection = new Section(SectionTest.강남역, SectionTest.선릉역, addDistance);
        // when
        this.sections.add(connectUpSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 하행역과 연결된 지하철 구간 추가 : 강남역-(선릉역)-역삼역")
    @Test
    void test_add_down_section() {
        // given
        Section connectDownSection = new Section(SectionTest.선릉역, SectionTest.역삼역, addDistance);
        // when
        this.sections.add(connectDownSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 새로운 상행 종점 지하철 구간 추가 : (선릉역)-강남역-역삼역")
    @Test
    void test_add_edge_up_section() {
        // given
        Section edgeUpSection = new Section(SectionTest.선릉역, SectionTest.강남역, addDistance);
        // when
        this.sections.add(edgeUpSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 새로운 하행 종점 지하철 구간 추가 : 강남역-역삼역-(선릉역)")
    @Test
    void test_add_edge_down_section() {
        // given
        Section edgeDownSection = new Section(SectionTest.역삼역, SectionTest.선릉역, addDistance);
        // when
        this.sections.add(edgeDownSection);
        // then
        assertThatSizeAndContainsStation(sections, 2);
    }

    @DisplayName("목록에 지하철 구간을 추가하고 정렬 확인 : 강남역-역삼역-(선릉역)")
    @Test
    void test_add_sorted_section() {
        // given
        Section edgeDownSection = new Section(SectionTest.역삼역, SectionTest.선릉역, addDistance);
        // when
        this.sections.add(edgeDownSection);
        // then
        assertAll(
                () -> assertThat(sections).isNotNull(),
                () -> assertThat(sections.indexOfStation(SectionTest.강남역)).isEqualTo(0),
                () -> assertThat(sections.indexOfStation(SectionTest.역삼역)).isEqualTo(1),
                () -> assertThat(sections.indexOfStation(SectionTest.선릉역)).isEqualTo(2)
        );
    }

    @DisplayName("목록에서 상행 종점 제거 후 새로운 상행 종점 확인 : 강남역(x)-선릉역-역삼역")
    @Test
    void test_remove_edge_up_section() {
        // given
        Section addedSection = new Section(SectionTest.강남역, SectionTest.선릉역, addDistance);
        this.sections.add(addedSection);
        // when
        this.sections.removeByStation(SectionTest.강남역);
        // then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(1),
                () -> assertThat(sections.indexOfStation(SectionTest.선릉역)).isEqualTo(0)
        );
    }

    @DisplayName("목록에서 하행 종점 제거 후 새로운 하행 종점 확인 : 강남역-선릉역-역삼역(x)")
    @Test
    void test_remove_edge_down_section() {
        // given
        Section addedSection = new Section(SectionTest.강남역, SectionTest.선릉역, addDistance);
        this.sections.add(addedSection);
        // when
        this.sections.removeByStation(SectionTest.역삼역);
        // then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(1),
                () -> assertThat(sections.indexOfStation(SectionTest.선릉역)).isEqualTo(1)
        );
    }

    @DisplayName("목록에서 지하철역을 제거하고 정렬 확인 : 강남역-선릉역(x)-역삼역")
    @Test
    void test_remove_sorted_section() {
        // given
        Section addedSection = new Section(SectionTest.강남역, SectionTest.선릉역, addDistance);
        this.sections.add(addedSection);
        // when
        this.sections.removeByStation(SectionTest.선릉역);
        // then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(1),
                () -> assertThat(sections.indexOfStation(SectionTest.강남역)).isEqualTo(0),
                () -> assertThat(sections.indexOfStation(SectionTest.역삼역)).isEqualTo(1)
        );
    }

    private void assertThatNotNullAndSize(Sections sections, int expectedSize) {
        assertAll(
                () -> assertThat(sections).isNotNull(),
                () -> assertThat(sections.size()).isEqualTo(expectedSize)
        );
    }

    private void assertThatSizeAndContainsStation(Sections sections, int expectedSize) {
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(expectedSize),
                () -> assertThat(sections.containsStation(SectionTest.강남역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.역삼역)).isTrue(),
                () -> assertThat(sections.containsStation(SectionTest.선릉역)).isTrue()
        );
    }
}